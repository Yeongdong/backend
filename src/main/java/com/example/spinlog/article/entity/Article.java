package com.example.spinlog.article.entity;

import com.example.spinlog.article.dto.*;
import com.example.spinlog.global.entity.BaseTimeEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "articles")
@Getter
@DynamicUpdate
@NoArgsConstructor
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId; // 일기 번호
    private String content;  //내용
    private String event; // 사건
    private String thought; // 생각
    @Enumerated(EnumType.STRING)
    private Emotion emotion; // 감정
    private String result; // 결과
    private Float satisfaction; // 만족도
    private String reason; // 이유
    private String improvements; // 개선점
    @Nullable
    private String aiComment; // AI 한마디
    private Integer amount; // 금액
    @Enumerated(EnumType.STRING)
    private RegisterType registerType; // 지출과 소비

    @Builder
    public Article(String content, String event, String thought, Emotion emotion, String result, Float satisfaction, String reason, String improvements, @Nullable String aiComment, Integer amount, RegisterType registerType) {
        this.content = content;
        this.event = event;
        this.thought = thought;
        this.emotion = emotion;
        this.result = result;
        this.satisfaction = satisfaction;
        this.reason = reason;
        this.improvements = improvements;
        this.aiComment = aiComment;
        this.amount = amount;
        this.registerType = registerType;
    }

    public Article update(UpdateArticleRequestDTO updateArticle) {
        this.content = updateArticle.getContent();
        this.event = updateArticle.getEvent();
        this.thought = updateArticle.getThought();
        this.emotion = Emotion.valueOf(updateArticle.getEmotion());
        this.result = updateArticle.getResult();
        this.satisfaction = updateArticle.getSatisfaction();
        this.reason = updateArticle.getReason();
        this.improvements = updateArticle.getImprovements();
        this.aiComment = updateArticle.getAiComment();
        this.amount = updateArticle.getAmount();
        this.registerType = RegisterType.valueOf(updateArticle.getRegisterType());
        return this;
    }
}
