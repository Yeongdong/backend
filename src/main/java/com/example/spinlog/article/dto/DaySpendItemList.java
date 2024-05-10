package com.example.spinlog.article.dto;

import lombok.Data;

@Data
public class DaySpendItemList {
    private Long articleId;
    private String registerType;
    private int amount;
    private String content;
    private Float satisfaction;
    private String emotion;
}
