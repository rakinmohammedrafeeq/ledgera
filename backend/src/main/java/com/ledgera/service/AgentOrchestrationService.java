package com.ledgera.service;

import com.ledgera.dto.AgentRequest;
import com.ledgera.dto.AgentResponse;
import com.ledgera.dto.PendingAction;
import com.ledgera.entity.User;
import com.ledgera.enums.WorkspacePermission;
import com.ledgera.exception.ForbiddenException;
import com.ledgera.exception.ResourceNotFoundException;
import com.ledgera.repository.WorkspaceMemberRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core AI agent orchestrator for Ledgera.
 *
 * <h3>Agent loop (max 5 iterations)</h3>
 * <ol>
 *   <li>Validates workspace membership; derives RBAC-filtered tool list.</li>
 *   <li>Calls Groq with the user message + system prompt + permitted tool schemas.</li>
 *   <li>If the model returns a text answer → return {@code FINAL_ANSWER}.</li>
 *   <li>If the model calls a <em>read</em> tool → execute, append observation, loop.</li>
 *   <li>If the model calls a <em>write</em> tool → store in {@link PendingActionStore},
 *       return {@code PENDING_CONFIRMATION} (nothing is written until the user confirms).</li>
 * </ol>
 *
 * <h3>Confirmation flow</h3>
 * <ul>
 *   <li>{@link #confirmAction(String)} — executes the stored write and returns a final answer.</li>
 *   <li>{@link #cancelAction(String)} — discards the stored write without executing.</li>
 * </ul>
 *
 * <h3>Model selection</h3>
 * Uses {@code GROQ_TEXT_MODEL} from the .env file (defaults to {@code llama-3.3-70b-versatile}).
 * Gemini is intentionally not used for tool-calling — neither current GeminiAiService method
 * sends function-calling fields, and the configured Gemini key appears invalid (AQ. prefix).
 */
@Service
public class AgentOrchestrationService {

    private static final Logger logger = LoggerFactory.getLogger(AgentOrchestrationService.class);
    private static final int MAX_ITERATIONS = 5;

    private final GroqAiService groqAiService;
    private final AgentToolRegistry toolRegistry;
    private final AgentToolExecutorService toolExecutor;
    private final PendingActionStore pendingActionStore;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CurrentUserService currentUserService;
    private final String agentModel;

    public AgentOrchestrationService(
            GroqAiService groqAiService,
            AgentToolRegistry toolRegistry,
            AgentToolExecutorService toolExecutor,
            PendingActionStore pendingActionStore,
            WorkspaceMemberRepository workspaceMemberRepository,
            CurrentUserService currentUserService) {
        this.groqAiService = groqAiService;
        this.toolRegistry = toolRegistry;
        this.toolExecutor = toolExecutor;
        this.pendingActionStore = pendingActionStore;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.currentUserService = currentUserService;

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.agentModel = dotenv.get("GROQ_AGENT_MODEL", dotenv.get("GROQ_TEXT_MODEL", "openai/gpt-oss-120b"));
        logger.info("AgentOrchestrationService initialized. model={}", agentModel);
    }

    // ── Primary entry point ────────────────────────────────────────────────────

    /**
     * Processes a user message through the full agent loop.
     * Returns either a {@code FINAL_ANSWER} or a {@code PENDING_CONFIRMATION}.
     */
    public AgentResponse processMessage(AgentRequest request) {
        User currentUser = currentUserService.requireCurrentUser();
        Long workspaceId = request.getWorkspaceId();

        WorkspacePermission permission = workspaceMemberRepository
                .findPermissionByWorkspaceAndUser(workspaceId, currentUser.getId())
                .orElseThrow(() -> new ForbiddenException(
                        "You don't have access to workspace " + workspaceId));

        List<Map<String, Object>> permittedTools = toolRegistry.buildToolList(permission);

        logger.info("Agent request — user={}, workspace={}, permission={}, tools={}",
                currentUser.getId(), workspaceId, permission, permittedTools.size());

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", buildSystemPrompt(workspaceId)));

        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            int start = Math.max(0, request.getHistory().size() - 10);
            for (int i = start; i < request.getHistory().size(); i++) {
                AgentRequest.ChatMessage item = request.getHistory().get(i);
                if (item != null && item.getContent() != null && !item.getContent().isBlank()) {
                    String role = "agent".equalsIgnoreCase(item.getRole()) || "assistant".equalsIgnoreCase(item.getRole())
                            ? "assistant" : "user";
                    messages.add(Map.of("role", role, "content", item.getContent()));
                }
            }
        }

        messages.add(Map.of("role", "user", "content", request.getMessage()));

        return runLoop(messages, permittedTools, workspaceId, currentUser);
    }

    // ── Confirmation flow ──────────────────────────────────────────────────────

    /**
     * Executes a previously proposed write action after the user explicitly confirms it.
     */
    public AgentResponse confirmAction(String actionId) {
        User currentUser = currentUserService.requireCurrentUser();

        PendingActionStore.StoredEntry stored = pendingActionStore
                .retrieveValid(actionId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pending action '" + actionId + "' not found or expired. Please ask again."));

        logger.info("Confirming action {} for user {} (tool={})",
                actionId, currentUser.getId(), stored.action().getToolName());

        String result = toolExecutor.executeWrite(stored.action(), stored.workspaceId(), currentUser);
        pendingActionStore.remove(actionId);
        return AgentResponse.finalAnswer(result);
    }

    /**
     * Cancels a pending write action without executing it.
     */
    public void cancelAction(String actionId) {
        User currentUser = currentUserService.requireCurrentUser();

        pendingActionStore.retrieveValid(actionId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pending action '" + actionId + "' not found or expired."));

        pendingActionStore.remove(actionId);
        logger.info("User {} cancelled action {}", currentUser.getId(), actionId);
    }

    // ── Agent loop ─────────────────────────────────────────────────────────────

    private AgentResponse runLoop(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> permittedTools,
            Long workspaceId,
            User currentUser) {

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            logger.debug("Agent loop iteration {}/{}", iteration + 1, MAX_ITERATIONS);

            GroqAiService.GroqChatResponse llmResponse;
            try {
                llmResponse = groqAiService.callGroqApiWithTools(messages, permittedTools, agentModel);
            } catch (Exception e) {
                logger.error("LLM call failed on iteration {}: {}", iteration + 1, e.getMessage());
                throw new RuntimeException("AI service error: " + e.getMessage(), e);
            }

            // Model produced a plain text answer — loop ends
            boolean isTextAnswer = !"tool_calls".equals(llmResponse.finishReason())
                    || llmResponse.toolCalls() == null
                    || llmResponse.toolCalls().isEmpty();

            if (isTextAnswer) {
                logger.info("Agent produced final answer on iteration {}", iteration + 1);
                return AgentResponse.finalAnswer(llmResponse.textContent());
            }

            // Append the full assistant message (carries tool_calls metadata) to conversation history
            messages.add(llmResponse.assistantMessage());

            // Process each tool call in the order the model returned them
            for (GroqAiService.ToolCall toolCall : llmResponse.toolCalls()) {
                logger.debug("Agent invoking tool: {}", toolCall.functionName());

                if (toolRegistry.isWriteTool(toolCall.functionName())) {
                    // Halt the loop — do NOT execute. Store and return for user confirmation.
                    PendingAction pending =
                            pendingActionStore.store(toolCall, workspaceId, currentUser.getId());
                    logger.info("Write tool '{}' proposed — awaiting confirmation (actionId={})",
                            toolCall.functionName(), pending.getActionId());
                    return AgentResponse.pendingConfirmation(pending);
                }

                // Execute read tool and feed result back as a tool-role message
                String toolResult = toolExecutor.executeRead(toolCall, workspaceId, currentUser);
                logger.debug("Tool '{}' result: {} chars", toolCall.functionName(), toolResult.length());

                messages.add(Map.of(
                        "role", "tool",
                        "tool_call_id", toolCall.id(),
                        "content", toolResult
                ));
            }
        }

        logger.warn("Agent reached max iterations ({}) without a final answer", MAX_ITERATIONS);
        return AgentResponse.finalAnswer(
                "I've worked through your request across multiple steps but couldn't produce a complete "
                + "answer within the allowed limit. Please try a more focused question.");
    }

    // ── System prompt ──────────────────────────────────────────────────────────

    private String buildSystemPrompt(Long workspaceId) {
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        
        return "You are a financial assistant for Ledgera, a workspace-based financial tracking platform.\n"
                + "Your role is to help users understand their financial data, find transactions, and manage records.\n\n"
                + "CRITICAL EXECUTION RULES:\n"
                + "1. YOU MUST CALL TOOLS DIRECTLY. Whenever the user asks to add, record, create, update, or delete a transaction, "
                + "OR whenever the user confirms with 'yes', 'confirm', 'proceed', 'do it', etc., YOU MUST DIRECTLY CALL THE APPROPRIATE TOOL (create_transaction, update_transaction, delete_transaction).\n"
                + "2. NEVER ASK FOR CONFIRMATION IN TEXT. Never say 'Would you like me to create this?', 'Shall I proceed?', 'Please confirm', or similar phrases. "
                + "The Ledgera platform automatically catches your tool call and renders an interactive confirmation modal for the user. "
                + "Therefore, calling the tool IS safe, required, and expected immediately.\n"
                + "3. Always use tools to fetch real data before answering any data-related question. Never invent amounts or dates.\n"
                + "4. Be concise. Reference actual values from the data you retrieve.\n"
                + "5. If data is insufficient to answer, say so clearly instead of guessing.\n"
                + "6. Dates use YYYY-MM-DD format. Today is " + today + ".\n"
                + "7. The current workspace ID is " + workspaceId + ". "
                + "You MUST pass this exact integer (" + workspaceId + ") as the workspace_id parameter in every tool call. "
                + "Never use a string, placeholder, or variable name for workspace_id.\n"
                + "8. Currency is Indian Rupees. Always use the ₹ symbol (not $ or USD) when displaying any monetary amount in your responses.\n"
                + "9. When users ask about spending \"this month\", use start_date: " + firstOfMonth + " and end_date: " + today + ".\n"
                + "10. When users ask about spending with no time period specified, do NOT provide start_date or end_date to get all-time totals.\n"
                + "11. For tool parameters, ONLY include the parameters that are needed. If start_date and end_date are not needed, omit them entirely from the function call.\n"
                + "12. When deleting or updating transactions, you MUST use the actual numeric transaction_id from the database. "
                + "If the user says \"delete the last expense\" or \"update the recent transaction\", you must:\n"
                + "    a) First call get_transactions with appropriate filters to find the transaction(s)\n"
                + "    b) Extract the numeric ID from the results\n"
                + "    c) Then call delete_transaction or update_transaction with that numeric ID\n"
                + "    NEVER pass placeholder strings like \"last_expense_id\" — transaction_id must always be an actual integer from the database.\n\n"
                + "When the user asks about spending, income, or transactions, use the relevant tools first, "
                + "then synthesize your answer from the returned data.";
    }}

