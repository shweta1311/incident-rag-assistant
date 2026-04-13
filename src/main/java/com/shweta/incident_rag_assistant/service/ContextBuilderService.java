package com.shweta.incident_rag_assistant.service;

import com.shweta.incident_rag_assistant.model.DocumentEmbedding;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextBuilderService {
    /**
     * Combines the content of retrieved chunks into a single context string for LLM.
     * Each chunk is separated for clarity.
     */
    public String buildContext(List<DocumentEmbedding> chunks) {
        StringBuilder context = new StringBuilder();
        for (DocumentEmbedding chunk : chunks) {
            context.append("Source: ").append(chunk.getSource()).append("\n");
            context.append(chunk.getContent()).append("\n\n");
        }
        return context.toString();
    }
}

