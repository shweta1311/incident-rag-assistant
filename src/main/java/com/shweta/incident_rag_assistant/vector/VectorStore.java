package com.shweta.incident_rag_assistant.vector;

import com.shweta.incident_rag_assistant.model.DocumentEmbedding;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VectorStore {

    private final List<DocumentEmbedding> store = new ArrayList<>();

    public void add(DocumentEmbedding doc) {
        store.add(doc);
    }

    public List<DocumentEmbedding> getAll() {
        return store;
    }
}