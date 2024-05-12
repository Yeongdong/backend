package com.example.spinlog.statistics.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GenderWordFrequencyResponse {
    private List<WordFrequency> maleWordFrequencies;
    private List<WordFrequency> femaleWordFrequencies;
}
