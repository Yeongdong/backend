package com.example.spinlog.statistics.service.dto;

import com.example.spinlog.statistics.repository.dto.MBTISatisfactionAverageDto;
import com.example.spinlog.user.entity.Mbti;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MBTISatisfactionAverageResponse {
    private Mbti mbti;
    private List<MBTISatisfactionAverageDto> mbtiSatisfactionAverages;
}
