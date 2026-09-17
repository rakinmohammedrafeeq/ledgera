package com.ledgera.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing AI model fallbacks across Gemini and Groq providers.
 *
 * <p>Supports bidirectional cross-provider fallback chains:
 * <ul>
 *   <li><b>Groq-first</b>: Tries Groq text/vision models in order, and if all Groq models fail,
 *       automatically shifts to Gemini models and tries all Gemini fallbacks in order.
 *       Used by {@link GroqAiService} for advisor chat, insights, and categorization.</li>
 *   <li><b>Gemini-first</b>: Tries Gemini models in order, and if all Gemini models fail,
 *       automatically shifts to Groq models and tries all Groq fallbacks in order.
 *       Used by {@link GeminiAiService} for receipt OCR and image processing.</li>
 * </ul>
 *
 * <p>A model is skipped (and the next fallback tried) on:
 * rate limits (429), quota limits, 404 / model_not_found / decommissioned errors,
 * 503 / 500 / service unavailable errors, and connection timeouts.
 */
@Service
public class AiModelFallbackService {

    private static final Logger logger = LoggerFactory.getLogger(AiModelFallbackService.class);

    // ── Gemini-first combined lists (Gemini models -> Groq models) ───────────
    private final List<ModelConfig> geminiFirstTextModels;
    private final List<ModelConfig> geminiFirstVisionModels;

    // ── Groq-first combined lists (Groq models -> Gemini models) ─────────────
    private final List<ModelConfig> groqFirstTextModels;
    private final List<ModelConfig> groqFirstVisionModels;

    // ── Tool calling models (for agents) ─────────────────────────────────────
    private final List<ModelConfig> toolCallingModels;

    private final int maxRetryAttempts;
    private final long retryDelayMs;

    public AiModelFallbackService() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // ── 1. Load individual Gemini models ─────────────────────────────────
        List<ModelConfig> geminiText = new ArrayList<>();
        addIfPresent(geminiText, dotenv, "GEMINI_TEXT_PRIMARY",   ModelProvider.GEMINI);
        addIfPresent(geminiText, dotenv, "GEMINI_TEXT_FALLBACK1", ModelProvider.GEMINI);
        addIfPresent(geminiText, dotenv, "GEMINI_TEXT_FALLBACK2", ModelProvider.GEMINI);
        addIfPresent(geminiText, dotenv, "GEMINI_TEXT_FALLBACK3", ModelProvider.GEMINI);
        addIfPresent(geminiText, dotenv, "GEMINI_TEXT_FALLBACK4", ModelProvider.GEMINI);
        if (geminiText.isEmpty()) {
            geminiText.add(new ModelConfig("gemini-3.6-flash", ModelProvider.GEMINI));
            geminiText.add(new ModelConfig("gemini-flash-latest", ModelProvider.GEMINI));
            geminiText.add(new ModelConfig("gemini-3.8-flash", ModelProvider.GEMINI));
            geminiText.add(new ModelConfig("gemini-3.5-flash-lite", ModelProvider.GEMINI));
            geminiText.add(new ModelConfig("gemini-3.1-flash-lite", ModelProvider.GEMINI));
        }

        List<ModelConfig> geminiVision = new ArrayList<>();
        addIfPresent(geminiVision, dotenv, "GEMINI_VISION_PRIMARY",   ModelProvider.GEMINI);
        addIfPresent(geminiVision, dotenv, "GEMINI_VISION_FALLBACK1", ModelProvider.GEMINI);
        addIfPresent(geminiVision, dotenv, "GEMINI_VISION_FALLBACK2", ModelProvider.GEMINI);
        addIfPresent(geminiVision, dotenv, "GEMINI_VISION_FALLBACK3", ModelProvider.GEMINI);
        addIfPresent(geminiVision, dotenv, "GEMINI_VISION_FALLBACK4", ModelProvider.GEMINI);
        if (geminiVision.isEmpty()) {
            geminiVision.add(new ModelConfig("gemini-3.6-flash", ModelProvider.GEMINI));
            geminiVision.add(new ModelConfig("gemini-flash-latest", ModelProvider.GEMINI));
            geminiVision.add(new ModelConfig("gemini-3.8-flash", ModelProvider.GEMINI));
            geminiVision.add(new ModelConfig("gemini-3.5-flash-lite", ModelProvider.GEMINI));
            geminiVision.add(new ModelConfig("gemini-3.1-flash-lite", ModelProvider.GEMINI));
        }

        // ── 2. Load individual Groq models ───────────────────────────────────
        List<ModelConfig> groqText = new ArrayList<>();
        addIfPresent(groqText, dotenv, "GROQ_TEXT_MODEL",     ModelProvider.GROQ);
        addIfPresent(groqText, dotenv, "GROQ_TEXT_FALLBACK1", ModelProvider.GROQ);
        addIfPresent(groqText, dotenv, "GROQ_TEXT_FALLBACK2", ModelProvider.GROQ);
        addIfPresent(groqText, dotenv, "GROQ_TEXT_FALLBACK3", ModelProvider.GROQ);
        addIfPresent(groqText, dotenv, "GROQ_TEXT_FALLBACK4", ModelProvider.GROQ);
        if (groqText.isEmpty()) {
            groqText.add(new ModelConfig("groq/compound-mini", ModelProvider.GROQ));
            groqText.add(new ModelConfig("openai/gpt-oss-120b", ModelProvider.GROQ));
            groqText.add(new ModelConfig("openai/gpt-oss-20b", ModelProvider.GROQ));
            groqText.add(new ModelConfig("qwen/qwen3.8-27b", ModelProvider.GROQ));
            groqText.add(new ModelConfig("groq/compound", ModelProvider.GROQ));
        }

        List<ModelConfig> groqVision = new ArrayList<>();
        addIfPresent(groqVision, dotenv, "GROQ_VISION_MODEL",     ModelProvider.GROQ);
        addIfPresent(groqVision, dotenv, "GROQ_VISION_FALLBACK1", ModelProvider.GROQ);
        addIfPresent(groqVision, dotenv, "GROQ_VISION_FALLBACK2", ModelProvider.GROQ);
        if (groqVision.isEmpty()) {
            groqVision.add(new ModelConfig("qwen/qwen3.8-27b", ModelProvider.GROQ));
        }

        // ── 3. Load Groq Tool-calling models ─────────────────────────────────
        List<ModelConfig> tools = new ArrayList<>();
        addIfPresent(tools, dotenv, "GROQ_AGENT_MODEL",     ModelProvider.GROQ);
        addIfPresent(tools, dotenv, "GROQ_AGENT_FALLBACK1", ModelProvider.GROQ);
        addIfPresent(tools, dotenv, "GROQ_AGENT_FALLBACK2", ModelProvider.GROQ);
        if (tools.isEmpty()) {
            tools.add(new ModelConfig("openai/gpt-oss-120b", ModelProvider.GROQ));
            tools.add(new ModelConfig("openai/gpt-oss-20b", ModelProvider.GROQ));
            tools.add(new ModelConfig("qwen/qwen3.8-27b", ModelProvider.GROQ));
        }
        this.toolCallingModels = tools;

        // ── 4. Build combined fallback lists ─────────────────────────────────
        // Gemini-first: all Gemini models, then all Groq models
        this.geminiFirstTextModels   = combined(geminiText, groqText);
        this.geminiFirstVisionModels = combined(geminiVision, groqVision);

        // Groq-first: all Groq models, then all Gemini models
        this.groqFirstTextModels   = combined(groqText, geminiText);
        this.groqFirstVisionModels = combined(groqVision, geminiVision);

        // ── 5. Retry configuration ───────────────────────────────────────────
        this.maxRetryAttempts = Integer.parseInt(dotenv.get("AI_RETRY_ATTEMPTS", "3"));
        this.retryDelayMs     = Long.parseLong(dotenv.get("AI_RETRY_DELAY_MS", "500"));

        logger.info("AiModelFallbackService initialized successfully");
        logger.info("  Groq-first Text Chain  ({} models): {}", groqFirstTextModels.size(), groqFirstTextModels);
        logger.info("  Groq-first Vision Chain({} models): {}", groqFirstVisionModels.size(), groqFirstVisionModels);
        logger.info("  Gemini-first Text Chain({} models): {}", geminiFirstTextModels.size(), geminiFirstTextModels);
        logger.info("  Gemini-first Vision Chain({} models): {}", geminiFirstVisionModels.size(), geminiFirstVisionModels);
        logger.info("  Agent Tool Models: {}", toolCallingModels);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public execution methods
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Groq-first text fallback chain (used by GroqAiService):
     * Tries all configured Groq models; if all fail, smoothly shifts to Gemini models.
     */
    public <T> T executeWithGroqFirstTextFallback(CrossProviderExecutor<T> executor) throws Exception {
        return executeWithFallback(executor, groqFirstTextModels, "GROQ-FIRST-TEXT");
    }

    /**
     * Groq-first vision fallback chain:
     * Tries all Groq vision models; if all fail, smoothly shifts to Gemini vision models.
     */
    public <T> T executeWithGroqFirstVisionFallback(CrossProviderExecutor<T> executor) throws Exception {
        return executeWithFallback(executor, groqFirstVisionModels, "GROQ-FIRST-VISION");
    }

    /**
     * Gemini-first text fallback chain (used by GeminiAiService):
     * Tries all configured Gemini models; if all fail, smoothly shifts to Groq models.
     */
    public <T> T executeWithTextFallback(CrossProviderExecutor<T> executor) throws Exception {
        return executeWithFallback(executor, geminiFirstTextModels, "GEMINI-FIRST-TEXT");
    }

    /**
     * Gemini-first vision fallback chain (used for Receipt OCR):
     * Tries all configured Gemini vision models; if all fail, smoothly shifts to Groq vision models.
     */
    public <T> T executeWithVisionFallback(CrossProviderExecutor<T> executor) throws Exception {
        return executeWithFallback(executor, geminiFirstVisionModels, "GEMINI-FIRST-VISION");
    }

    /**
     * Tool-calling fallback chain for agent execution:
     * Tries tool-capable models (openai/gpt-oss-120b -> openai/gpt-oss-20b -> qwen/qwen3.8-27b).
     */
    public <T> T executeWithAgentToolFallback(CrossProviderExecutor<T> executor) throws Exception {
        return executeWithFallback(executor, toolCallingModels, "AGENT-TOOL-CALLING");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Core fallback loop
    // ─────────────────────────────────────────────────────────────────────────

    private <T> T executeWithFallback(
            CrossProviderExecutor<T> executor,
            List<ModelConfig> models,
            String label) throws Exception {

        if (models == null || models.isEmpty()) {
            throw new IllegalStateException("No models configured for chain: " + label);
        }

        Exception lastException = null;

        for (int i = 0; i < models.size(); i++) {
            ModelConfig cfg = models.get(i);
            String modelLabel = (i == 0) ? "PRIMARY" : "FALLBACK-" + i;

            logger.info("[{}] Attempting {} model: {} (provider: {})",
                    label, modelLabel, cfg.modelName, cfg.provider);

            try {
                T result = executor.execute(cfg.modelName, cfg.provider);
                if (i > 0) {
                    logger.info("[{}] Fallback SUCCESS: using {} model {} ({})",
                            label, modelLabel, cfg.modelName, cfg.provider);
                }
                return result;

            } catch (Exception e) {
                lastException = e;
                String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

                if (isFallbackWorthy(msg)) {
                    logger.warn("[{}] {} model {} ({}) failed with recoverable error: {}. Trying next fallback...",
                            label, modelLabel, cfg.modelName, cfg.provider, truncate(e.getMessage(), 250));

                    if (i < models.size() - 1) {
                        try {
                            Thread.sleep(retryDelayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    continue;
                }

                // If it's a non-fallback-worthy error (e.g. fatal programming bug), log and rethrow
                logger.error("[{}] {} model {} ({}) failed with non-recoverable error: {}",
                        label, modelLabel, cfg.modelName, cfg.provider, e.getMessage());
                throw e;
            }
        }

        logger.error("[{}] All {} models exhausted. Last error: {}",
                label, models.size(), lastException != null ? lastException.getMessage() : "unknown");
        throw new Exception("All models in chain " + label + " failed. Last error: " +
                (lastException != null ? lastException.getMessage() : "unknown"), lastException);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Error classification
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isFallbackWorthy(String msg) {
        return isRateLimitError(msg)
                || isQuotaExceededError(msg)
                || isModelNotFoundError(msg)
                || isServerError(msg)
                || isConnectionError(msg);
    }

    private boolean isRateLimitError(String msg) {
        return msg.contains("rate limit")
                || msg.contains("429")
                || msg.contains("too many requests")
                || msg.contains("resource_exhausted")
                || msg.contains("rate_limit_exceeded");
    }

    private boolean isQuotaExceededError(String msg) {
        return msg.contains("quota")
                || msg.contains("quota exceeded")
                || msg.contains("insufficient quota")
                || msg.contains("quota_exceeded");
    }

    private boolean isModelNotFoundError(String msg) {
        return msg.contains("404")
                || msg.contains("model_not_found")
                || msg.contains("does not exist")
                || msg.contains("model not found")
                || msg.contains("not found")
                || msg.contains("no access to it")
                || msg.contains("no longer available")
                || msg.contains("is not supported")
                || msg.contains("invalid_request_error")
                || msg.contains("cannot find model");
    }

    private boolean isServerError(String msg) {
        return msg.contains("503")
                || msg.contains("502")
                || msg.contains("504")
                || msg.contains("500")
                || msg.contains("service unavailable")
                || msg.contains("unavailable")
                || msg.contains("overloaded")
                || msg.contains("bad gateway")
                || msg.contains("gateway timeout")
                || msg.contains("internal server error");
    }

    private boolean isConnectionError(String msg) {
        return msg.contains("timeout")
                || msg.contains("timed out")
                || msg.contains("connection refused")
                || msg.contains("connection reset")
                || msg.contains("broken pipe");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Accessor helpers
    // ─────────────────────────────────────────────────────────────────────────

    public ModelConfig getPrimaryVisionModel() {
        return geminiFirstVisionModels.isEmpty()
                ? new ModelConfig("gemini-3.6-flash", ModelProvider.GEMINI)
                : geminiFirstVisionModels.get(0);
    }

    public ModelConfig getPrimaryTextModel() {
        return geminiFirstTextModels.isEmpty()
                ? new ModelConfig("gemini-3.6-flash", ModelProvider.GEMINI)
                : geminiFirstTextModels.get(0);
    }

    public ModelConfig getPrimaryGroqTextModel() {
        return groqFirstTextModels.isEmpty()
                ? new ModelConfig("groq/compound-mini", ModelProvider.GROQ)
                : groqFirstTextModels.get(0);
    }

    public List<ModelConfig> getGroqFirstTextModels() {
        return groqFirstTextModels;
    }

    public List<ModelConfig> getGeminiFirstTextModels() {
        return geminiFirstTextModels;
    }

    public List<ModelConfig> getGroqFirstVisionModels() {
        return groqFirstVisionModels;
    }

    public List<ModelConfig> getGeminiFirstVisionModels() {
        return geminiFirstVisionModels;
    }

    public List<ModelConfig> getToolCallingModels() {
        return toolCallingModels;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal utilities
    // ─────────────────────────────────────────────────────────────────────────

    private void addIfPresent(List<ModelConfig> list, Dotenv dotenv, String key, ModelProvider provider) {
        String value = dotenv.get(key);
        if (value != null && !value.isBlank() && !value.startsWith("your_")) {
            list.add(new ModelConfig(value.trim(), provider));
        }
    }

    private List<ModelConfig> combined(List<ModelConfig> first, List<ModelConfig> second) {
        List<ModelConfig> result = new ArrayList<>(first);
        result.addAll(second);
        return result;
    }

    private String truncate(String s, int max) {
        if (s == null) return "null";
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public types
    // ─────────────────────────────────────────────────────────────────────────

    @FunctionalInterface
    public interface CrossProviderExecutor<T> {
        T execute(String modelName, ModelProvider provider) throws Exception;
    }

    public static class ModelConfig {
        public final String modelName;
        public final ModelProvider provider;

        public ModelConfig(String modelName, ModelProvider provider) {
            this.modelName = modelName;
            this.provider = provider;
        }

        @Override
        public String toString() {
            return modelName + "(" + provider + ")";
        }
    }

    public enum ModelProvider {
        GEMINI,
        GROQ
    }
}
