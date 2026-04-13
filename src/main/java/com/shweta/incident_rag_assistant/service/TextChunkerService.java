package com.shweta.incident_rag_assistant.service;

import com.shweta.incident_rag_assistant.model.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkerService {
    private static final int CHUNK_SIZE = 500;

    /**
     * Splits a document into chunks of ~500 characters.
     * Each chunk is a new Document with the same source.
     */
    public List<Document> chunkDocument(Document document) {
        List<Document> chunks = new ArrayList<>();
        String content = document.getContent();
        String source = document.getSource();
        int length = content.length();
        int start = 0;
        while (start < length) {
            int end = Math.min(start + CHUNK_SIZE, length);
            String chunkContent = content.substring(start, end);
            chunks.add(new Document(chunkContent, source));
            start = end;
        }
        return chunks;
    }

    /**
     * Splits a list of documents into chunks.
     */
    public List<Document> chunkDocuments(List<Document> documents) {
        List<Document> allChunks = new ArrayList<>();
        for (Document doc : documents) {
            allChunks.addAll(chunkDocument(doc));
        }
        return allChunks;
    }
}

