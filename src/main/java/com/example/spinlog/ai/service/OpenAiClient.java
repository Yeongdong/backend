package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.CommentRequest;
import com.example.spinlog.ai.dto.CommentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "OpenAiClient", url = "${openai.url}")
public interface OpenAiClient {
    @PostMapping(value = "/v1/chat/completions", consumes = APPLICATION_JSON_VALUE)
    @CircuitBreaker(name = "openAiClient", fallbackMethod = "fallbackGetAiComment")
    @Retry(name = "openAiClient")
    CommentResponse getAiComment(@RequestHeader("Authorization") String authorization, @RequestBody CommentRequest commentRequest);
}
