package net.skinwalker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.text.Text;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Multi-API System - Supports OpenAI, Grok, Anthropic, OpenRouter, and Gemini
 */
public class MultiAPISystem {
    
    public enum APIType {
        GEMINI, OPENAI, GROK, ANTHROPIC, OPENROUTER
    }
    
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Map<APIType, String> apiKeys = new HashMap<>();
    private static APIType defaultAPI = APIType.GEMINI;
    
    // Default free Gemini API key
    private static final String DEFAULT_GEMINI_KEY = "AIzaSyDX_MkrzqjE3q1jiDqSB5d95cV2fU6cMJw";
    
    static {
        apiKeys.put(APIType.GEMINI, DEFAULT_GEMINI_KEY);
    }
    
    public static void init() {
        // Load API keys from config file
        loadConfig();
    }
    
    private static void loadConfig() {
        // TODO: Load from config file
        // For now, use defaults
    }
    
    /**
     * Set API key for a specific provider
     */
    public static void setAPIKey(APIType type, String key) {
        apiKeys.put(type, key);
    }
    
    /**
     * Set default API provider
     */
    public static void setDefaultAPI(APIType type) {
        defaultAPI = type;
    }
    
    /**
     * Generate AI response using the specified or default API
     */
    public static CompletableFuture<String> generateResponse(String prompt, APIType apiType) {
        final APIType finalApiType = (apiType == null) ? defaultAPI : apiType;
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                switch (finalApiType) {
                    case GEMINI:
                        return callGeminiAPI(prompt);
                    case OPENAI:
                        return callOpenAIAPI(prompt);
                    case GROK:
                        return callGrokAPI(prompt);
                    case ANTHROPIC:
                        return callAnthropicAPI(prompt);
                    case OPENROUTER:
                        return callOpenRouterAPI(prompt);
                    default:
                        return callGeminiAPI(prompt);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        });
    }
    
    private static String callGeminiAPI(String prompt) throws Exception {
        String apiKey = apiKeys.getOrDefault(APIType.GEMINI, DEFAULT_GEMINI_KEY);
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
        
        String json = "{\"contents\": [{\"parts\":[{\"text\": \"" + escapeJson(prompt) + "\"}]}]}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();
        
        if (jsonObj.has("candidates")) {
            return jsonObj.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
        }
        return "No response";
    }
    
    private static String callOpenAIAPI(String prompt) throws Exception {
        String apiKey = apiKeys.get(APIType.OPENAI);
        if (apiKey == null) throw new Exception("OpenAI API key not set");
        
        String url = "https://api.openai.com/v1/chat/completions";
        String json = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}]}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();
        
        return jsonObj.getAsJsonArray("choices")
            .get(0).getAsJsonObject()
            .getAsJsonObject("message")
            .get("content").getAsString();
    }
    
    private static String callGrokAPI(String prompt) throws Exception {
        String apiKey = apiKeys.get(APIType.GROK);
        if (apiKey == null) throw new Exception("Grok API key not set");
        
        String url = "https://api.x.ai/v1/chat/completions";
        String json = "{\"model\": \"grok-beta\", \"messages\": [{\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}]}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();
        
        return jsonObj.getAsJsonArray("choices")
            .get(0).getAsJsonObject()
            .getAsJsonObject("message")
            .get("content").getAsString();
    }
    
    private static String callAnthropicAPI(String prompt) throws Exception {
        String apiKey = apiKeys.get(APIType.ANTHROPIC);
        if (apiKey == null) throw new Exception("Anthropic API key not set");
        
        String url = "https://api.anthropic.com/v1/messages";
        String json = "{\"model\": \"claude-3-haiku-20240307\", \"max_tokens\": 1024, \"messages\": [{\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}]}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();
        
        return jsonObj.getAsJsonArray("content")
            .get(0).getAsJsonObject()
            .get("text").getAsString();
    }
    
    private static String callOpenRouterAPI(String prompt) throws Exception {
        String apiKey = apiKeys.get(APIType.OPENROUTER);
        if (apiKey == null) throw new Exception("OpenRouter API key not set");
        
        String url = "https://openrouter.ai/api/v1/chat/completions";
        String json = "{\"model\": \"openai/gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}]}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();
        
        return jsonObj.getAsJsonArray("choices")
            .get(0).getAsJsonObject()
            .getAsJsonObject("message")
            .get("content").getAsString();
    }
    
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

