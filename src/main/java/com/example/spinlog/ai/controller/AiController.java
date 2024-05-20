package com.example.spinlog.ai.controller;

import com.example.spinlog.ai.dto.AiRequestDto;
import com.example.spinlog.ai.dto.AiResponseDto;
import com.example.spinlog.ai.service.AiService;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles/ai")
public class AiController {
    private final AiService aiService;

    @PostMapping
    public ApiResponseWrapper<AiResponseDto> getAiComment(@RequestBody AiRequestDto requestDto) {
        return ResponseUtils.ok(aiService.requestAiComment(requestDto), "AI 한마디 가져오기 성공");
    }
}
