package com.shweta.incident_rag_assistant.service;

import com.shweta.incident_rag_assistant.model.McpTool;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class McpToolRegistry {
    public List<McpTool> listTools() {
        Map<String, Object> analyzerSchema = new HashMap<>();
        analyzerSchema.put("type", "object");
        Map<String, Object> analyzerProps = new HashMap<>();
        analyzerProps.put("question", Map.of("type", "string"));
        analyzerSchema.put("properties", analyzerProps);

        Map<String, Object> searchSchema = new HashMap<>();
        searchSchema.put("type", "object");
        Map<String, Object> searchProps = new HashMap<>();
        searchProps.put("query", Map.of("type", "string"));
        searchSchema.put("properties", searchProps);

        McpTool incidentRagAnalyzer = new McpTool(
                "incident_rag_analyzer",
                "Analyze production incidents using the RAG system",
                analyzerSchema
        );
        McpTool searchIncidents = new McpTool(
                "search_incidents",
                "Search incident knowledge base",
                searchSchema
        );
        return Arrays.asList(incidentRagAnalyzer, searchIncidents);
    }
}
