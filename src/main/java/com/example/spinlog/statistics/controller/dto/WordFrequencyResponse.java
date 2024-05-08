package com.example.spinlog.statistics.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WordFrequencyResponse {
    private List<WordFrequency> allWordFrequencies;
    private List<WordFrequency> myWordFrequencies;

    @Getter
    @Builder
    public static class WordFrequency{
        private String word;
        private Long frequency;
    }
}
