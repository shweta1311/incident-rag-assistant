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
    private final RetrieverService retrieverService;
    private final TextChunkerService textChunkerService;

    public RagService(DocumentLoader documentLoader,
                      EmbeddingService embeddingService,
                      VectorStore vectorStore,
                      GeminiClient geminiClient,
                      RetrieverService retrieverService,
                      TextChunkerService textChunkerService) {
        this.documentLoader = documentLoader;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.geminiClient = geminiClient;
        this.retrieverService = retrieverService;
        this.textChunkerService = textChunkerService;
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

    public QueryResponse answer1(String question) {
        // Step 1: Load docs
        List<Document> documents = documentLoader.loadDocuments();
        // Step 1.5: Build vocabulary for bag-of-words embedding
        List<String> allTexts = new ArrayList<>();
        for (Document doc : documents) allTexts.add(doc.getContent());
        allTexts.add(question);
        embeddingService.buildVocabulary(allTexts);
        // Step 2: Chunk documents
        List<Document> chunks = textChunkerService.chunkDocuments(documents);
        // Step 3: Store embeddings (only once ideally)
        vectorStore.getAll().clear();
        for (Document chunk : chunks) {
            List<Double> embedding = embeddingService.generateEmbedding(chunk.getContent());
            vectorStore.add(new DocumentEmbedding(
                    chunk.getContent(),
                    chunk.getSource(),
                    embedding
            ));
        }
        // Step 4: Embed question
        List<Double> questionEmbedding = embeddingService.generateEmbedding(question);
        // Step 5: Use RetrieverService for retrieval and debug logging
        int k = 5;
        double threshold = 0.2; // Lower threshold for more inclusive retrieval
        List<DocumentEmbedding> topDocs = retrieverService.retrieveTopK(questionEmbedding, k, threshold);
        // Step 6: Response
        QueryResponse response = new QueryResponse();
        List<String> sources = new ArrayList<>();
        StringBuilder context = new StringBuilder();
        for (DocumentEmbedding doc : topDocs) {
            sources.add(doc.getSource());
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