package com.example.spinlog.dashboard.dto;

import com.example.spinlog.article.entity.Emotion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmotionAmount {
    private String emotion;
    private Integer amount;

    public static EmotionAmount of(Emotion emotion, Integer amount) {
        return new EmotionAmount(emotion.name(), amount);
    }
}
