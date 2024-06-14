package com.example.spinlog.article.controller.request;

import com.example.spinlog.article.service.request.SearchCond;
import com.example.spinlog.utils.DateUtils;
import com.example.spinlog.utils.ParameterParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchCondRequestDto {
    private String registerType;
    private String emotion;
    private String from;
    private String to;
    private String satisfaction;
    private String word;

    public SearchCond toSearchCond() {
        return SearchCond.builder()
                .registerTypes(ParameterParser.parseRegisterTypes(this.getRegisterType()))
                .emotions(ParameterParser.parseEmotions(this.getEmotion()))
                .satisfactions(ParameterParser.parseCommaSeparatedFloat(this.getSatisfaction()))
                .from(DateUtils.parseStringToDate(this.getFrom()))
                .to(DateUtils.parseStringToDate(this.getTo()))
                .words(ParameterParser.decodeAndParse(this.getWord()))
                .build();
    }
}
