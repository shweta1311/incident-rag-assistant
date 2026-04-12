package com.shweta.incident_rag_assistant.aiconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateAnswer(String prompt) {

        try {
            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

            String body = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {
                              "text": "%s"
                            }
                          ]
                        }
                      ]
                    }
                    """.formatted(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // 🔥 PARSE RESPONSE
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());


            return json
                    .get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();
        }catch (Exception e) {
            return "Error generating AI response: " + e.getMessage();
        }
    }
    }
