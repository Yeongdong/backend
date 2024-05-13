package com.example.spinlog.statistics.service;

import com.example.spinlog.statistics.service.dto.WordFrequency;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordExtractionService {
    private final KomoranService komoranService;

    public List<WordFrequency> analyzeWords(List<String> memos){
        /**
         * 1. 명사 형용사 감탄사 명사만 뽑는다
         * 2. 명사, 형용사에 '다' 붙인다.
         * 3. 각 단어 빈도수 정리
         * */

        List<String> keyWords = getKeyWords(
                komoranService.getTokens(memos));
        Map<String, Long> collect = getWordFrequencies(keyWords);

        return collect.entrySet()
                .stream()
                .map(e -> WordFrequency.builder()
                        .word(e.getKey())
                        .frequency(e.getValue())
                        .build())
                .toList();
    }

    private static Map<String, Long> getWordFrequencies(List<String> keyWords) {
        return keyWords.stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
    }

    private List<String> getKeyWords(List<Token> tokens) {
        return tokens.stream()
                .filter(this::isKeyWord)
                .map(t -> {
                    if (isVerbOrAdjective(t))
                        return t.getMorph().concat("다");
                    return t.getMorph();
                })
                .toList();
    }

    private boolean isKeyWord(Token token){
        // KOMORAN 라이브러리에서 제공하는 명사, 대명사, 동사, 형용사, 외국어에 해당하는 pos
        // https://docs.komoran.kr/firststep/postypes.html
        return token.getPos().equals("NNG") ||
                token.getPos().equals("NNP") ||
                token.getPos().equals("NP") ||
                token.getPos().equals("VV") ||
                token.getPos().equals("VA") ||
                token.getPos().equals("SL");
    }

    private boolean isVerbOrAdjective(Token token) {
        return token.getPos().startsWith("V");
    }
}
