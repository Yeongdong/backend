package com.example.spinlog.statistics.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WordFrequencyResponse {
    private List<WordFrequency> allWordFrequencies;
    private List<WordFrequency> myWordFrequencies;
}
