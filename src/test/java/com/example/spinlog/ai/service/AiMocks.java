package com.example.spinlog.ai.service;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AiMocks {

    public static void setupAiMockResponse(WireMockServer mockService) {
        mockService.stubFor(post(urlEqualTo("/api/articles/ai"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("payload/ai-response.json")
                )
        );
    }
}
