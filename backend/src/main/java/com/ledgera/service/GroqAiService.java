package com.ledgera.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledgera.dto.*;
import com.ledgera.entity.FinancialRecord;
import com.ledgera.enums.TransactionType;
import com.ledgera.repository.FinancialRecordRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroqAiService {

    private static final int MAX_TOOL_CALL_GENERATION_ATTEMPTS = 5;
    private static final Logger logger = LoggerFactory.getLogger(GroqAiService.class);
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final String groqApiKey;
    private final String geminiApiKey;
    private final ObjectMapper objectMapper;
    private final FinancialRecordRepository financialRecordRepository;
    private final CurrentUserService currentUserService;
    private final AiModelFallbackService fallbackService;

    public GroqAiService(
            FinancialRecordRepository financialRecordRepository,
            CurrentUserService currentUserService,
            AiModelFallbackService fallbackService) {
        this.financialRecordRepository = financialRecordRepository;
        this.currentUserService = currentUserService;
        this.fallbackService = fallbackService;
        this.objectMapper = new ObjectMapper();

        // Load from environment
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.groqApiKey = dotenv.get("GROQ_API_KEY");
        this.geminiApiKey = dotenv.get("GEMINI_API_KEY");

        if (groqApiKey == null || groqApiKey.isBlank()) {
            logger.warn("GROQ_API_KEY not configured.");
        } else {
            logger.info("Groq AI Service initialized with fallback support (primary Groq: {})",
                    fallbackService.getPrimaryGroqTextModel());
        }
    }

    /**
     * Categorize a transaction using Groq AI with automatic fallback to multiple
     * Groq models and Gemini models.
     */
    public AiCategorizationResponse categorizeTransaction(AiCategorizationRequest request) {
        if (!isConfigured()) {
            return AiCategorizationResponse.builder()
                    .success(false)
                    .error("AI service not configured (neither GROQ_API_KEY nor GEMINI_API_KEY is available)")
                    .build();
        }

        try {
            String prompt = buildCategorizationPrompt(request);
            String response = fallbackService.executeWithGroqFirstTextFallback((modelName, provider) -> {
                return executeTextPrompt(prompt, true, modelName, provider);
            });
            return parseCategorizationResponse(response);
        } catch (Exception e) {
            logger.error("Error categorizing transaction with AI fallbacks", e);
            return AiCategorizationResponse.builder()
                    .success(false)
                    .error("Failed to categorize: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Generate financial insights for the current workspace using Groq AI
     * with automatic fallback across multiple Groq models and Gemini models.
     */
    public AiInsightsResponse generateInsights() {
        if (!isConfigured()) {
            return AiInsightsResponse.builder()
                    .success(false)
                    .error("AI service not configured")
                    .build();
        }

        try {
            var currentUser = currentUserService.requireCurrentUser();
            var workspace = currentUser.getCurrentWorkspace();

            if (workspace == null) {
                return AiInsightsResponse.builder()
                        .success(false)
                        .error("No workspace selected")
                        .build();
            }

            List<FinancialRecord> recentRecords = financialRecordRepository
                    .findTop50ByWorkspaceIdOrderByDateDesc(workspace.getId());

            if (recentRecords.isEmpty()) {
                return AiInsightsResponse.builder()
                        .success(true)
                        .summary("No transactions yet in this workspace.")
                        .keyInsights(List.of("Start adding transactions to get AI-powered insights."))
                        .recommendations(List.of("Add your income and expenses to track your financial health."))
                        .spendingAnalysis(AiInsightsResponse.SpendingAnalysis.builder()
                                .topCategory("N/A")
                                .percentageChange(0.0)
                                .comparisonPeriod("No data")
                                .build())
                        .trendAnalysis("Add more transactions to see trends over time.")
                        .build();
            }

            String prompt = buildInsightsPrompt(recentRecords);
            String response = fallbackService.executeWithGroqFirstTextFallback((modelName, provider) -> {
                return executeTextPrompt(prompt, true, modelName, provider);
            });
            return parseInsightsResponse(response);
        } catch (Exception e) {
            logger.error("Error generating insights with AI fallbacks", e);
            return AiInsightsResponse.builder()
                    .success(false)
                    .error("Failed to generate insights: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Generate financial advisor response with RAG context and full fallback chain:
     * Groq models first -> then Gemini models.
     */
    public String generateAdvisorResponse(String prompt) {
        if (!isConfigured()) {
            logger.error("AI service not configured - missing API keys");
            return "AI service is not configured. Please set GROQ_API_KEY or GEMINI_API_KEY in environment.";
        }

        try {
            logger.info("Calling AI advisor response with Groq-first fallback chain");
            return fallbackService.executeWithGroqFirstTextFallback((modelName, provider) -> {
                return executeTextPrompt(prompt, false, modelName, provider);
            });
        } catch (Exception e) {
            logger.error("Error generating advisor response after all fallbacks: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate advisor response: " + e.getMessage(), e);
        }
    }

    private String executeTextPrompt(String prompt, boolean forceJson, String modelName,
                                     AiModelFallbackService.ModelProvider provider) throws Exception {
        return switch (provider) {
            case GROQ -> callGroqApi(prompt, forceJson, modelName);
            case GEMINI -> callGeminiApi(prompt, modelName);
        };
    }

    public String callGroqApi(String prompt) throws Exception {
        return callGroqApi(prompt, false, fallbackService.getPrimaryGroqTextModel().modelName);
    }

    public String callGroqApi(String prompt, boolean forceJson) throws Exception {
        return callGroqApi(prompt, forceJson, fallbackService.getPrimaryGroqTextModel().modelName);
    }

    public String callGroqApi(String prompt, boolean forceJson, String modelName) throws Exception {
        if (groqApiKey == null || groqApiKey.isBlank()) {
            throw new RuntimeException("GROQ_API_KEY is not configured");
        }

        org.apache.hc.client5.http.config.RequestConfig requestConfig = org.apache.hc.client5.http.config.RequestConfig.custom()
                .setConnectTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(10))
                .setResponseTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(30))
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            HttpPost post = new HttpPost(GROQ_API_URL);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + groqApiKey);

            Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1500);

            if (forceJson) {
                requestBody.put("response_format", Map.of("type", "json_object"));
            }

            String requestJson = objectMapper.writeValueAsString(requestBody);
            post.setEntity(new StringEntity(requestJson, java.nio.charset.StandardCharsets.UTF_8));

            logger.debug("Sending request to Groq API with model: {}", modelName);
            long startTime = System.currentTimeMillis();

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), java.nio.charset.StandardCharsets.UTF_8);

                logger.info("Groq API [{}] response received in {}ms, status: {}", modelName, elapsedTime, statusCode);
                if (statusCode != 200) {
                    logger.warn("Groq API error: Status {}, Body: {}", statusCode, responseBody);
                    throw new RuntimeException("Groq API returned status " + statusCode + ": " + responseBody);
                }

                return extractTextFromGroqResponse(responseBody);
            }
        }
    }

    private String callGeminiApi(String prompt, String modelName) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new RuntimeException("GEMINI_API_KEY is not configured");
        }

        String url = GEMINI_API_URL + modelName + ":generateContent?key=" + geminiApiKey;

        org.apache.hc.client5.http.config.RequestConfig requestConfig = org.apache.hc.client5.http.config.RequestConfig.custom()
                .setConnectTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(10))
                .setResponseTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(30))
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            String requestJson = objectMapper.writeValueAsString(requestBody);
            post.setEntity(new StringEntity(requestJson, java.nio.charset.StandardCharsets.UTF_8));

            logger.debug("Sending fallback request to Gemini API with model: {}", modelName);
            long startTime = System.currentTimeMillis();

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), java.nio.charset.StandardCharsets.UTF_8);

                logger.info("Gemini API [{}] fallback response received in {}ms, status: {}", modelName, elapsedTime, statusCode);
                if (statusCode != 200) {
                    logger.warn("Gemini API error: Status {}, Body: {}", statusCode, responseBody);
                    throw new RuntimeException("Gemini API returned status " + statusCode + ": " + responseBody);
                }

                return extractTextFromGeminiResponse(responseBody);
            }
        }
    }

    private String extractTextFromGroqResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.path("choices");

        if (choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).path("message");
            return message.path("content").asText();
        }

        throw new RuntimeException("Invalid Groq API response: no choices found");
    }

    private String extractTextFromGeminiResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode candidates = root.path("candidates");

        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode content = candidates.get(0).path("content");
            JsonNode parts = content.path("parts");
            if (parts.isArray() && parts.size() > 0) {
                return parts.get(0).path("text").asText();
            }
        }

        throw new RuntimeException("Invalid Gemini API response: no candidates found");
    }

    private String buildCategorizationPrompt(AiCategorizationRequest request) {
        return String.format("""
            You are a financial expert AI. Analyze the following transaction and suggest the most appropriate category and type.
            
            Transaction Description: %s
            Amount: %s
            Date: %s
            
            Available INCOME categories:
            - Salary
            - Freelance
            - Business
            - Investment
            - Bonus
            - Interest
            - Rental Income
            - Refund
            - Other
            
            Available EXPENSE categories:
            - Food
            - Groceries
            - Shopping
            - Transportation
            - Fuel
            - Bills
            - Rent
            - EMI
            - Entertainment
            - Healthcare
            - Education
            - Travel
            - Subscription
            - Insurance
            - Gifts
            - Taxes
            - Investment
            - Savings
            - Other
            
            IMPORTANT: Use ONLY the exact category names listed above. Do not use variations like "Food & Dining" (use "Food"), "Bills & Utilities" (use "Bills"), etc.
            
            Respond ONLY with valid JSON in this exact format:
            {
              "category": "category name",
              "type": "INCOME or EXPENSE",
              "confidence": 0.95,
              "reasoning": "brief explanation"
            }
            """,
                request.getDescription(),
                request.getAmount() != null ? request.getAmount() : "unknown",
                request.getDate() != null ? request.getDate() : "unknown");
    }

    private String buildInsightsPrompt(List<FinancialRecord> records) {
        StringBuilder data = new StringBuilder();
        data.append("Analyze the following financial transactions and provide insights:\n\n");

        for (FinancialRecord record : records) {
            data.append(String.format("Date: %s, Type: %s, Category: %s, Amount: %s\n",
                    record.getDate(),
                    record.getType(),
                    record.getCategory(),
                    record.getAmount()));
        }

        data.append("""
            
            IMPORTANT: Use the ₹ symbol (not INR, not $) for all monetary amounts in your response.
            
            Provide actionable financial insights in JSON format:
            {
              "summary": "One sentence overview of financial health",
              "keyInsights": ["insight 1", "insight 2", "insight 3"],
              "recommendations": ["recommendation 1", "recommendation 2", "recommendation 3"],
              "spendingAnalysis": {
                "topCategory": "category name",
                "percentageChange": 15.5,
                "comparisonPeriod": "vs last month"
              },
              "trendAnalysis": "Description of spending trends"
            }
            
            Focus on practical, actionable advice.
            """);

        return data.toString();
    }

    private AiCategorizationResponse parseCategorizationResponse(String response) {
        try {
            String jsonStr = extractJson(response);
            JsonNode json = objectMapper.readTree(jsonStr);

            return AiCategorizationResponse.builder()
                    .category(json.path("category").asText())
                    .type(TransactionType.valueOf(json.path("type").asText().toUpperCase()))
                    .confidence(json.path("confidence").asDouble())
                    .reasoning(json.path("reasoning").asText())
                    .success(true)
                    .build();
        } catch (Exception e) {
            logger.error("Error parsing categorization response: {}", response, e);
            return AiCategorizationResponse.builder()
                    .success(false)
                    .error("Failed to parse AI response")
                    .build();
        }
    }

    private AiInsightsResponse parseInsightsResponse(String response) {
        try {
            String jsonStr = extractJson(response);
            JsonNode json = objectMapper.readTree(jsonStr);

            List<String> insights = new ArrayList<>();
            json.path("keyInsights").forEach(node -> insights.add(node.asText()));

            List<String> recommendations = new ArrayList<>();
            json.path("recommendations").forEach(node -> recommendations.add(node.asText()));

            JsonNode spendingNode = json.path("spendingAnalysis");
            AiInsightsResponse.SpendingAnalysis spending = AiInsightsResponse.SpendingAnalysis.builder()
                    .topCategory(spendingNode.path("topCategory").asText())
                    .percentageChange(spendingNode.path("percentageChange").asDouble())
                    .comparisonPeriod(spendingNode.path("comparisonPeriod").asText())
                    .build();

            return AiInsightsResponse.builder()
                    .summary(json.path("summary").asText())
                    .keyInsights(insights)
                    .recommendations(recommendations)
                    .spendingAnalysis(spending)
                    .trendAnalysis(json.path("trendAnalysis").asText())
                    .success(true)
                    .build();
        } catch (Exception e) {
            logger.error("Error parsing insights response: {}", response, e);
            return AiInsightsResponse.builder()
                    .success(false)
                    .error("Failed to parse insights")
                    .build();
        }
    }

    private String extractJson(String response) {
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    private boolean isConfigured() {
        return (groqApiKey != null && !groqApiKey.isBlank()) ||
                (geminiApiKey != null && !geminiApiKey.isBlank());
    }

    // ── Tool-calling API (used by AgentOrchestrationService) ──────────────────

    public GroqChatResponse callGroqApiWithTools(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> tools,
            String modelName) throws Exception {

        // If specific model is requested, try that model first; if it fails with
        // a model-not-found / decommissioned / rate-limit error, fall back to other tool models
        List<String> candidateModels = new ArrayList<>();
        if (modelName != null && !modelName.isBlank()) {
            candidateModels.add(modelName);
        }
        for (AiModelFallbackService.ModelConfig cfg : fallbackService.getToolCallingModels()) {
            if (!candidateModels.contains(cfg.modelName)) {
                candidateModels.add(cfg.modelName);
            }
        }

        Exception lastException = null;
        for (String model : candidateModels) {
            try {
                return executeToolCallWithModel(messages, tools, model);
            } catch (Exception e) {
                lastException = e;
                String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (msg.contains("not supported") || msg.contains("404") || msg.contains("model_not_found")
                        || msg.contains("rate limit") || msg.contains("429") || msg.contains("quota")) {
                    logger.warn("Groq tool model {} failed ({}). Trying next candidate...", model, e.getMessage());
                    continue;
                }
                throw e;
            }
        }

        throw new RuntimeException("All tool-calling models failed. Last error: "
                + (lastException != null ? lastException.getMessage() : "unknown"), lastException);
    }

    private GroqChatResponse executeToolCallWithModel(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> tools,
            String modelName) throws Exception {

        if (groqApiKey == null || groqApiKey.isBlank()) {
            throw new RuntimeException("GROQ_API_KEY not configured — agent cannot run");
        }

        org.apache.hc.client5.http.config.RequestConfig requestConfig =
                org.apache.hc.client5.http.config.RequestConfig.custom()
                        .setConnectTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(15))
                        .setResponseTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(60))
                        .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {

            for (int attempt = 1; attempt <= MAX_TOOL_CALL_GENERATION_ATTEMPTS; attempt++) {
                HttpPost post = new HttpPost(GROQ_API_URL);
                post.setHeader("Content-Type", "application/json");
                post.setHeader("Authorization", "Bearer " + groqApiKey);

                Map<String, Object> requestBody = new java.util.HashMap<>();
                requestBody.put("model", modelName);
                requestBody.put("messages", messages);
                requestBody.put("tools", tools);
                requestBody.put("tool_choice", "auto");
                double temperature = Math.max(0.0, 0.15 - (attempt * 0.05));
                requestBody.put("temperature", temperature);
                requestBody.put("max_tokens", 2000);
                if (attempt > 1) {
                    requestBody.put("parallel_tool_calls", false);
                }

                String requestJson = objectMapper.writeValueAsString(requestBody);
                post.setEntity(new StringEntity(requestJson, java.nio.charset.StandardCharsets.UTF_8));

                long start = System.currentTimeMillis();
                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    long elapsed = System.currentTimeMillis() - start;
                    int statusCode = response.getCode();
                    String responseBody = EntityUtils.toString(response.getEntity(), java.nio.charset.StandardCharsets.UTF_8);

                    logger.info("Groq tool-calling [{}]: status={}, elapsed={}ms, attempt={}",
                            modelName, statusCode, elapsed, attempt);

                    if (statusCode == 200) {
                        return parseGroqToolCallingResponse(responseBody);
                    }

                    if (isToolUseGenerationFailure(statusCode, responseBody)
                            && attempt < MAX_TOOL_CALL_GENERATION_ATTEMPTS) {
                        logger.warn("Groq rejected a malformed tool call; retrying (attempt {}/{})",
                                attempt + 1, MAX_TOOL_CALL_GENERATION_ATTEMPTS);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }

                    throw new RuntimeException("Groq API returned status " + statusCode + ": " + responseBody);
                }
            }
        }

        throw new IllegalStateException("Groq tool-call retry loop exited unexpectedly");
    }

    private boolean isToolUseGenerationFailure(int statusCode, String responseBody) {
        return statusCode == 400
                && responseBody != null
                && (responseBody.contains("\"code\":\"tool_use_failed\"")
                || responseBody.contains("Failed to call a function"));
    }

    private GroqChatResponse parseGroqToolCallingResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choice = root.path("choices").get(0);
        if (choice == null) {
            throw new RuntimeException("Empty choices array in Groq response");
        }

        String finishReason = choice.path("finish_reason").asText("stop");
        JsonNode messageNode = choice.path("message");

        Map<String, Object> assistantMessage = objectMapper.convertValue(
                messageNode,
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});

        if ("tool_calls".equals(finishReason)) {
            List<ToolCall> toolCalls = new ArrayList<>();
            JsonNode toolCallsNode = messageNode.path("tool_calls");
            if (toolCallsNode.isArray()) {
                for (JsonNode tc : toolCallsNode) {
                    toolCalls.add(new ToolCall(
                            tc.path("id").asText(),
                            tc.path("function").path("name").asText(),
                            tc.path("function").path("arguments").asText()
                    ));
                }
            }
            return new GroqChatResponse(finishReason, null, assistantMessage, toolCalls);
        }

        String textContent = messageNode.path("content").asText("");
        return new GroqChatResponse(finishReason, textContent, assistantMessage, null);
    }

    public record ToolCall(String id, String functionName, String argumentsJson) {}

    public record GroqChatResponse(
            String finishReason,
            String textContent,
            Map<String, Object> assistantMessage,
            List<ToolCall> toolCalls) {}
}
