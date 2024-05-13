package com.example.spinlog.statistics.service;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class KomoranService {
    private final Komoran komoran;

    public KomoranService() {
        this.komoran = new Komoran(DEFAULT_MODEL.LIGHT);
    }

    public List<Token> getTokens(List<String> memos) {
        final int NUM_THREADS = 1;
        return komoran.analyze(memos, NUM_THREADS)
                .stream()
                .map(KomoranResult::getTokenList)
                .flatMap(Collection::stream)
                .toList();
    }

}
