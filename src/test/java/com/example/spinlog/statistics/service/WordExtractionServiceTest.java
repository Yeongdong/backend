package com.example.spinlog.statistics.service;

import com.example.spinlog.statistics.service.dto.WordFrequency;
import kr.co.shineware.nlp.komoran.model.Token;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WordExtractionServiceTest {

    final List<String> keywordPos = List.of("NNG","NNP","NP","VV","VA","SL");

    @InjectMocks
    WordExtractionService wordExtractionService;

    @Mock
    KomoranService komoranService;

    @Test
    void 토큰들_중_키워드만_추출한다() throws Exception {
        // given
        List<Token> keywords = List.of(
                new Token("단어1", "NNG", 0, 0),
                new Token("단어2", "NNP", 0, 0),
                new Token("단어3", "NP", 0, 0),
                new Token("단어4", "SL", 0, 0)
        );
        List<Token> nonKeywords = List.of(
                new Token("단어6", "XXX", 0, 0),
                new Token("단어7", "XXX", 0, 0)
        );
        List<Token> input = new ArrayList<>(keywords);
        input.addAll(nonKeywords);

        when(komoranService.getTokens(any()))
                .thenReturn(input);

        // when
        List<WordFrequency> results = wordExtractionService.analyzeWords(List.of());

        // then
        List<String> strings = results.stream()
                .map(WordFrequency::getWord)
                .toList();
        assertThat(keywords)
                .extracting(Token::getMorph)
                .containsExactlyInAnyOrderElementsOf(strings);
        assertThat(nonKeywords)
                .extracting(Token::getMorph)
                .doesNotContainAnyElementsOf(strings);
    }

    @Test
    void 동사_형용사인_단어에_다_를_붙인다() throws Exception {
        // given
        List<Token> keywords = List.of(
                new Token("단어1", "NNG", 0, 0),
                new Token("단어2", "NNP", 0, 0),
                new Token("단어3", "NP", 0, 0),
                new Token("동사", "VV", 0, 0),
                new Token("형용사", "VA", 0, 0)
        );

        when(komoranService.getTokens(any()))
                .thenReturn(keywords);

        // when
        List<WordFrequency> results = wordExtractionService.analyzeWords(List.of());

        // then
        results.stream()
                .map(WordFrequency::getWord)
                .filter(w -> w.startsWith("동사") || w.startsWith("형용사"))
                .forEach(w -> assertThat(w).isIn("동사다", "형용사다"));
    }

    @Test
    void 단어들의_빈도수를_세서_반환한다() throws Exception {
        // given
        List<Token> keywords = List.of(
                new Token("단어1", "NNG", 0, 0),
                new Token("단어1", "NNG", 0, 0),
                new Token("단어1", "NNG", 0, 0),
                new Token("단어2", "NNP", 0, 0),
                new Token("단어2", "NNP", 0, 0),
                new Token("단어2", "NNP", 0, 0)
        );

        when(komoranService.getTokens(any()))
                .thenReturn(keywords);

        // when
        List<WordFrequency> results = wordExtractionService.analyzeWords(List.of());

        // then
        assertThat(results)
                .extracting(WordFrequency::getFrequency)
                .allMatch((f) -> f == 3L);
    }
}