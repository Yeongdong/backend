package com.example.spinlog.article.dto;

import com.example.spinlog.article.entity.Emotion;
import com.example.spinlog.article.entity.RegisterType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchCond {
    private List<RegisterType> registerTypes;
    private List<Emotion> emotions;
    private List<Float> satisfactions;
    private LocalDate from;
    private LocalDate to;
    private List<String> words;
}
