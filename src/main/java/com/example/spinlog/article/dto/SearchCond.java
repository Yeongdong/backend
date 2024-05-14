package com.example.spinlog.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchCond {
    private String registerType;
    private String emotion;
    private String from;
    private String to;
    private String satisfaction;
    private String word;
}
