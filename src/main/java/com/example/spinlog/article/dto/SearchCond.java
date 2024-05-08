package com.example.spinlog.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCond {
    private String registerType;
    private String emotion;
    private String createdDate;
    private Float satisfaction;
    private String word;
}
