package com.shweta.incident_rag_assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmbeddingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Set<String> vocabulary = new HashSet<>();
    private List<String> vocabList = new ArrayList<>();
    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");

    /**
     * Call this before embedding any text: builds the vocabulary from all texts (docs + question).
     */
    public void buildVocabulary(List<String> texts) {
        Set<String> vocab = new HashSet<>();
        for (String text : texts) {
            Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
            while (matcher.find()) {
                vocab.add(matcher.group());
            }
        }
        this.vocabulary = vocab;
        this.vocabList = new ArrayList<>(vocab);
        Collections.sort(this.vocabList);
    }

    /**
     * Returns a bag-of-words vector for the given text, using the current vocabulary.
     */
    public List<Double> generateEmbedding(String text) {
        double[] vec = new double[vocabList.size()];
        Map<String, Integer> counts = new HashMap<>();
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group();
            counts.put(word, counts.getOrDefault(word, 0) + 1);
        }
        for (int i = 0; i < vocabList.size(); i++) {
            vec[i] = counts.getOrDefault(vocabList.get(i), 0);
        }
        List<Double> result = new ArrayList<>();
        for (double v : vec) result.add(v);
        return result;
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
