package com.shweta.incident_rag_assistant.service;

import com.shweta.incident_rag_assistant.aiconfig.GeminiClient;
import com.shweta.incident_rag_assistant.model.Document;
import com.shweta.incident_rag_assistant.model.DocumentEmbedding;
import com.shweta.incident_rag_assistant.model.QueryResponse;
import com.shweta.incident_rag_assistant.vector.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {

    private final DocumentLoader documentLoader;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final GeminiClient geminiClient;

    public RagService(DocumentLoader documentLoader,
                      EmbeddingService embeddingService,
                      VectorStore vectorStore, GeminiClient geminiClient) {
        this.documentLoader = documentLoader;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.geminiClient = geminiClient;
    }
  /*  public QueryResponse answer(String question) {

        // Step 1: Simulate retrieved documents
        String retrievedContext = """
        Incident: Kafka Consumer Lag

        Root Cause:
        Consumers are slower than producers.

        Resolution:
        Increase consumer instances or optimize processing.
        """;

        // Step 2: Simulate LLM answer
        String answer = "Kafka consumer lag occurs when message consumption is slower than production.";

        QueryResponse response = new QueryResponse();
        response.setAnswer(answer);
        response.setSources(List.of("kafka-consumer-lag.txt"));

        return response;
    }*/

  /*  public QueryResponse answer(String question) {

        List<Document> documents = documentLoader.loadDocuments();

        List<Document> matchedDocs = new ArrayList<>();



      *//*  String[] keywords = questionLower.split(" ");
        for (Document doc : documents) {

            String contentLower = doc.getContent().toLowerCase();

            for (String keyword : keywords) {
                if (contentLower.contains(keyword)) {
                    matchedDocs.add(doc);
                    break;
                }
            }
        }*//*

        String questionLower = question.toLowerCase();

        for (Document doc : documents) {

            String contentLower = doc.getContent().toLowerCase();

            if ((questionLower.contains("kafka") && contentLower.contains("kafka")) ||
                    ((questionLower.contains("null") && questionLower.contains("pointer"))
                            && contentLower.contains("nullpointer")) ||
                    (questionLower.contains("timeout") && contentLower.contains("timeout"))) {

                matchedDocs.add(doc);
            }
        }

        // fallback if nothing matches
        if (matchedDocs.isEmpty()) {
            matchedDocs = documents;
        }

        StringBuilder context = new StringBuilder();
        List<String> sources = new ArrayList<>();

        for (Document doc : matchedDocs) {
            context.append(doc.getContent()).append("\n\n");
            sources.add(doc.getSource());
        }

        // Simulate LLM for now
        String answer = "Based on incidents, the issue may be related to system behavior described in logs.";

        QueryResponse response = new QueryResponse();
        response.setAnswer(answer);
        response.setSources(sources);

        return response;
    }*/

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {

        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }

        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public QueryResponse answer1(String question) {

        // Step 1: Load docs
        List<Document> documents = documentLoader.loadDocuments();

        // Step 2: Store embeddings (only once ideally)
        vectorStore.getAll().clear();

        for (Document doc : documents) {

            List<Double> embedding = embeddingService.generateEmbedding(doc.getContent());

            vectorStore.add(new DocumentEmbedding(
                    doc.getContent(),
                    doc.getSource(),
                    embedding
            ));
        }

        // Step 3: Embed question
        List<Double> questionEmbedding =
                embeddingService.generateEmbedding(question);

        // Step 4: Find best match
        List<DocumentEmbedding> topDocs = vectorStore.getAll()
                .stream()
                .map(doc -> new Object() {
                    DocumentEmbedding document = doc;
                    double score = cosineSimilarity(questionEmbedding, doc.getEmbedding());
                })
                .filter(obj -> obj.score > 0.6)   // 🔥 threshold --/ Only documents with similarity > 0.6 will be selected
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(2)
                .map(obj -> obj.document)
                .toList();

        // Step 5: Response
        QueryResponse response = new QueryResponse();

        List<String> sources = new ArrayList<>();

        for (DocumentEmbedding doc : topDocs) {
            sources.add(doc.getSource());
        }
        StringBuilder context = new StringBuilder();

        for (DocumentEmbedding doc : topDocs) {
            context.append(doc.getContent()).append("\n\n");
        }

        String prompt = """
                        You are a Site Reliability Engineer (SRE).
                        
                        Analyze the following incidents and answer the question clearly.
                        
                        Context:
                        %s
                        
                        Question:
                        %s
                        
                        Respond in this format:
                        
                        Root Cause:
                        <short explanation>
                        
                        Suggested Fix:
                        <clear action steps>
                        
                        Keep it concise and practical.
                    """.formatted(context.toString(), question);

        String aiAnswer = geminiClient.generateAnswer(prompt);

        response.setAnswer(aiAnswer);
        response.setSources(sources);

        return response;
    }
}