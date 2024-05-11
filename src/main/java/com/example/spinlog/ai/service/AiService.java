package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.AiRequestDto;
import com.example.spinlog.ai.dto.AiResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface AiService {
    @Transactional
    AiResponseDto requestAiComment(AiRequestDto requestDto);
}
