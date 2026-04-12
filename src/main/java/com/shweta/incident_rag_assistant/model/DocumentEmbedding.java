package com.shweta.incident_rag_assistant.model;

import java.util.List;

public class DocumentEmbedding {

    private String content;
    private String source;
    private List<Double> embedding;

    public DocumentEmbedding(String content, String source, List<Double> embedding) {
        this.content = content;
        this.source = source;
        this.embedding = embedding;
    }

    public String getContent() {
        return content;
    }

    public String getSource() {
        return source;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }
}