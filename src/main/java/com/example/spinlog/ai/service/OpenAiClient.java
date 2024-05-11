package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.CommentRequest;
import com.example.spinlog.ai.dto.CommentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "OpenAiClient", url = "https://api.openai.com/v1/chat/completions")
public interface OpenAiClient {
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    CommentResponse getAiComment(@RequestHeader("Authorization") String authorization, @RequestBody CommentRequest commentRequest);
}
