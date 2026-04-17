package com.shweta.incident_rag_assistant.model;

import java.util.Map;

public class McpTool {
    private final String name;
    private final String description;
    private final Map<String, Object> input_schema;

    public McpTool(String name, String description, Map<String, Object> input_schema) {
        this.name = name;
        this.description = description;
        this.input_schema = input_schema;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getInput_schema() {
        return input_schema;
    }
}

