package com.shweta.incident_rag_assistant.controller;

import com.shweta.incident_rag_assistant.model.QueryRequest;
import com.shweta.incident_rag_assistant.model.QueryResponse;
import com.shweta.incident_rag_assistant.service.RagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag")
public class QueryController {

    private final RagService ragService;

    public QueryController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public QueryResponse ask(@RequestBody QueryRequest request) {
       // return ragService.answer(request.getQuestion());
        //--or run below onefor better rag

        return ragService.answer1(request.getQuestion());
    }
}