package com.example.spinlog.statistics.repository.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MemoDto {
    private String content;
    private String event;
    private String thought;
    private String result;
    private String reason;
    private String improvements;
}
