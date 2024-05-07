package com.example.spinlog.article.dto;

import lombok.Data;

@Data
public class UpdateArticleResponseDTO {
    private String content;
    private String event;
    private String thought;
    private String emotion;
    private String result;
    private Float satisfaction;
    private String reason;
    private String improvements;
    private String aiComment;
    private Integer amount;
    private String registerType;
}
