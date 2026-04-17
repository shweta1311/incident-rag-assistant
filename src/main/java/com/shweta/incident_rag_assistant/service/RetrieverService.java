package com.shweta.incident_rag_assistant.service;

import com.shweta.incident_rag_assistant.model.DocumentEmbedding;
import com.shweta.incident_rag_assistant.vector.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RetrieverService {
    private final VectorStore vectorStore;

    public RetrieverService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Retrieves the top K most similar DocumentEmbeddings to the query embedding, above a similarity threshold.
     */
    public List<DocumentEmbedding> retrieveTopK(List<Double> queryEmbedding, int k, double threshold) {
        return vectorStore.getAll().stream()
                .map(doc -> {
                    double score = cosineSimilarity(queryEmbedding, doc.getEmbedding());
                    System.out.printf("[RETRIEVER DEBUG] Source: %s | Similarity: %.4f\n", doc.getSource(), score);
                    return new ScoredEmbedding(doc, score);
                })
                .filter(se -> se.score() >= threshold)
                .sorted(Comparator.comparingDouble(ScoredEmbedding::score).reversed())
                .limit(k)
                .map(ScoredEmbedding::embedding)
                .collect(Collectors.toList());
    }

    /**
     * Backward compatible: retrieves top K without threshold (returns top K regardless of score).
     */
    public List<DocumentEmbedding> retrieveTopK(List<Double> queryEmbedding, int k) {
        return retrieveTopK(queryEmbedding, k, -1.0); // -1.0 means no threshold
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }
        return (norm1 == 0 || norm2 == 0) ? 0.0 : dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private record ScoredEmbedding(DocumentEmbedding embedding, double score) {}
}
