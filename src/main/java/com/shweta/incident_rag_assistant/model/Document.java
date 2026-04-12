package com.shweta.incident_rag_assistant.model;

public class Document {

    private String content;
    private String source;

    public Document(String content, String source) {
        this.content = content;
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public String getSource() {
        return source;
    }
}