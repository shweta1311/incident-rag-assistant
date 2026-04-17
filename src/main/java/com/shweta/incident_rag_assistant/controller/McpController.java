package com.shweta.incident_rag_assistant.controller;

import com.shweta.incident_rag_assistant.model.McpTool;
import com.shweta.incident_rag_assistant.model.QueryRequest;
import com.shweta.incident_rag_assistant.model.QueryResponse;
import com.shweta.incident_rag_assistant.service.McpToolRegistry;
import com.shweta.incident_rag_assistant.service.RagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mcp")
public class McpController {
    private final McpToolRegistry toolRegistry;
    private final RagService ragService;

    public McpController(McpToolRegistry toolRegistry, RagService ragService) {
        this.toolRegistry = toolRegistry;
        this.ragService = ragService;
    }

    @GetMapping("/tools")
    public List<McpTool> listTools() {
        return toolRegistry.listTools();
    }

    @PostMapping("/tools/incident_rag_analyzer")
    public QueryResponse analyzeIncident(@RequestBody QueryRequest request) {
        return ragService.answer1(request.getQuestion());
    }
}

