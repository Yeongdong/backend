package com.example.spinlog.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Usage implements Serializable {
    @JsonProperty("prompt_tokens")
    private String promptTokens;

    @JsonProperty("completion_tokens")
    private String completionTokens;

    @JsonProperty("total_tokens")
    private String totalTokens;
}
