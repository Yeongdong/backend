package com.example.spinlog.ai.dto;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class CommentResponse implements Serializable {
    private List<Choice> choices;
}
