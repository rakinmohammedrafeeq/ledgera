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
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroqAiService {

    private static final Logger logger = LoggerFactory.getLogger(GroqAiService.class);
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    
    private final String apiKey;
    private final String model;
    private final ObjectMapper objectMapper;
    private final FinancialRecordRepository financialRecordRepository;
    private final CurrentUserService currentUserService;

    public GroqAiService(FinancialRecordRepository financialRecordRepository, CurrentUserService currentUserService) {
        this.financialRecordRepository = financialRecordRepository;
        this.currentUserService = currentUserService;
        this.objectMapper = new ObjectMapper();
        
        // Load from environment
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("GROQ_API_KEY");
        // Using Llama 3.1 8B - much faster, still very capable for chat
        this.model = dotenv.get("GROQ_MODEL", "llama-3.1-8b-instant");
        
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("GROQ_API_KEY not configured. Text-based AI features will be disabled.");
        } else {
            logger.info("Groq AI Service initialized with model: {}", model);
        }
    }

    /**
     * Categorize a transaction using Groq AI
     */
    public AiCategorizationResponse categorizeTransaction(AiCategorizationRequest request) {
        if (!isConfigured()) {
            return AiCategorizationResponse.builder()
                    .success(false)
                    .error("Groq AI service not configured")
                    .build();
        }

        try {
            String prompt = buildCategorizationPrompt(request);
            String response = callGroqApi(prompt, true); // Force JSON for categorization
            return parseCategorizationResponse(response);
        } catch (Exception e) {
            logger.error("Error categorizing transaction with Groq", e);
            return AiCategorizationResponse.builder()
                    .success(false)
                    .error("Failed to categorize: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Generate financial insights for the current workspace using Groq AI
     */
    public AiInsightsResponse generateInsights() {
        if (!isConfigured()) {
            return AiInsightsResponse.builder()
                    .success(false)
                    .error("Groq AI service not configured")
                    .build();
        }

        try {
            // Get workspace from current user
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

            // If no records, return a helpful message
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
            String response = callGroqApi(prompt, true); // Force JSON for insights
            return parseInsightsResponse(response);
        } catch (Exception e) {
            logger.error("Error generating insights with Groq", e);
            return AiInsightsResponse.builder()
                    .success(false)
                    .error("Failed to generate insights: " + e.getMessage())
                    .build();
        }
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

    private String callGroqApi(String prompt) throws Exception {
        return callGroqApi(prompt, false);
    }
    
    private String callGroqApi(String prompt, boolean forceJson) throws Exception {
        // Configure timeout for HTTP client
        org.apache.hc.client5.http.config.RequestConfig requestConfig = org.apache.hc.client5.http.config.RequestConfig.custom()
                .setConnectTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(10))
                .setResponseTimeout(org.apache.hc.core5.util.Timeout.ofSeconds(30))
                .build();
        
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            HttpPost post = new HttpPost(GROQ_API_URL);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + apiKey);
            
            // Build Groq API request (OpenAI-compatible format)
            Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.7);  // Slightly higher for natural conversation
            requestBody.put("max_tokens", 1000);
            
            // Only add JSON format for categorization/insights, not for chat
            if (forceJson) {
                requestBody.put("response_format", Map.of("type", "json_object"));
            }
            
            String requestJson = objectMapper.writeValueAsString(requestBody);
            post.setEntity(new StringEntity(requestJson));
            
            logger.debug("Sending request to Groq API with model: {}", model);
            long startTime = System.currentTimeMillis();
            
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                
                logger.info("Groq API response received in {}ms, status: {}", elapsedTime, statusCode);
                logger.debug("Groq API response body: {}", responseBody);
                
                if (statusCode != 200) {
                    logger.error("Groq API error: Status {}, Body: {}", statusCode, responseBody);
                    throw new RuntimeException("Groq API returned status " + statusCode + ": " + responseBody);
                }
                
                return extractTextFromGroqResponse(responseBody);
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
        
        throw new RuntimeException("Invalid Groq API response");
    }

    private AiCategorizationResponse parseCategorizationResponse(String response) {
        try {
            // Extract JSON from markdown code blocks if present
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
        // Remove markdown code blocks if present
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

    /**
     * Generate financial advisor response with RAG context
     */
    public String generateAdvisorResponse(String prompt) {
        if (!isConfigured()) {
            logger.error("Groq AI service not configured - missing GROQ_API_KEY");
            return "AI service is not configured. Please set GROQ_API_KEY in environment.";
        }

        try {
            logger.info("Calling Groq API for advisor response");
            logger.debug("Prompt length: {} characters", prompt.length());
            String response = callGroqApi(prompt);
            logger.info("Successfully received advisor response from Groq API");
            return response;
        } catch (Exception e) {
            logger.error("Error generating advisor response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate advisor response: " + e.getMessage(), e);
        }
    }

    private boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
