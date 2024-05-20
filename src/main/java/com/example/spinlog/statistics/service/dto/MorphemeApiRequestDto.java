package com.example.spinlog.statistics.service.dto;

import lombok.*;

@Builder
@Getter
@EqualsAndHashCode
public class MorphemeApiRequestDto {

    private final String request_id;
    private final Argument argument;

    @Getter
    @Builder
    @EqualsAndHashCode
    public static class Argument {
        private final String analysis_code = "morp";
        private final String text;

        public Argument(String text) {
            this.text = text;
        }
    }
}
