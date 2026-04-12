package com.shweta.incident_rag_assistant.service;

import com.shweta.incident_rag_assistant.model.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentLoader {

    public List<Document> loadDocuments() {

        List<Document> documents = new ArrayList<>();

        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources =
                    resolver.getResources("classpath:incidents/*.txt");

            for (Resource resource : resources) {

                String content = new String(Files.readAllBytes(resource.getFile().toPath()));

                documents.add(new Document(content, resource.getFilename()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return documents;
    }
}