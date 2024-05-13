package com.example.spinlog.statistics.service;

import com.example.spinlog.statistics.service.dto.WordFrequency;
import kr.co.shineware.nlp.komoran.model.Token;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WordExtractionServiceTest {
    @InjectMocks
    WordExtractionService wordExtractionService;

    @Mock
    KomoranService komoranService;
    
    @Test
    @Order(1)
    void 메모_리스트를_파라미터로_받아_그대로_KomoranService에게_전달한다() throws Exception {
        // given
        List<String> parameter = List.of("Hello", "Komoran");
        
        // when
        wordExtractionService.analyzeWords(parameter);
        
        // then
        verify(komoranService)
                .getTokens(eq(parameter));
    }

    @Test
    @Order(2)
    void KomoranService로부터_토큰들을_받아_키워드만_추출한다() throws Exception {
        /**
         * 키워드 리스트(외부 라이브러리에서 제공하는 키워드 형태소)
         * NNG, NNP, NP, VV, VA, SL
         * */

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
        List<Token> returned = Stream
                .concat(keywords.stream(), nonKeywords.stream())
                .toList();

        when(komoranService.getTokens(any()))
                .thenReturn(returned);

        // when
        List<WordFrequency> results = wordExtractionService.analyzeWords(List.of());

        // then
        List<String> strings = results.stream()
                .map(WordFrequency::getWord)
                .toList();
        assertThat(strings)
                .containsExactlyInAnyOrderElementsOf(
                        keywords.stream()
                                .map(Token::getMorph)
                                .toList())
                .doesNotContainAnyElementsOf(
                        nonKeywords.stream()
                                .map(Token::getMorph)
                                .toList());
    }

    @Test
    @Order(3)
    void 키워드를_추출한_뒤_동사_형용사인_단어에_다_를_붙인다() throws Exception {
        /**
         * 동사: VV, 형용사: VA
         * */

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
        List<String> verbWordAndAdjectiveWord = keywords.stream()
                .filter(w -> w.getPos().startsWith("V"))
                .map(t -> t.getMorph() + "다")
                .toList();
        assertThat(results)
                .extracting(WordFrequency::getWord)
                .containsAnyElementsOf(verbWordAndAdjectiveWord);
    }

    @Test
    @Order(4)
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
        List<String> wordList = keywords.stream()
                .map(Token::getMorph)
                .distinct()
                .toList();
        assertThat(results)
                .extracting(WordFrequency::getWord)
                .containsExactlyInAnyOrderElementsOf(wordList);

        assertThat(results)
                .extracting(WordFrequency::getFrequency)
                .allMatch((f) -> f == 3L);
    }
}