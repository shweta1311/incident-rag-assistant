package com.shweta.incident_rag_assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Fake embedding generator (for learning)
    public List<Double> generateEmbedding(String text) {

        List<Double> vector = new ArrayList<>();

        int hash = text.hashCode();

        for (int i = 0; i < 10; i++) {
            vector.add((double) (hash % (i + 5)) / 100);
        }

        return vector;
    }

    // below is embeding gemini api not supoorted to my apikey
    public List<Double> generateEmbedding1(String text) {

        try {

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/embedding-001:embedContent";

            String requestBody = """
            {
              "model": "models/embedding-001",
              "content": {
                "parts": [
                  { "text": "%s" }
                ]
              }
            }
            """.formatted(text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());

            JsonNode values = json
                    .get("embedding")
                    .get("values");

            List<Double> embedding = new ArrayList<>();

            for (JsonNode val : values) {
                embedding.add(val.asDouble());
            }

            return embedding;

        } catch (Exception e) {
            throw new RuntimeException("Embedding API error: " + e.getMessage());
        }
    }
}


