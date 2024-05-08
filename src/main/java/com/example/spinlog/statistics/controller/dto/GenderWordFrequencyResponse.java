package com.example.spinlog.statistics.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GenderWordFrequencyResponse {
    private List<WordFrequency> maleWordFrequencies;
    private List<WordFrequency> femaleWordFrequencies;

    @Getter
    @Builder
    public static class WordFrequency{
        private String word;
        private Long frequency;
    }
}
