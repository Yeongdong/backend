package com.example.spinlog.article.service;

import com.example.spinlog.article.dto.*;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public WriteArticleResponseDto createArticle(WriteArticleRequestDto requestDto) {
        Article articleEntity = requestDto.toEntity();
        Article saveArticle = articleRepository.save(articleEntity);
        log.info("게시글이 성공적으로 저장되었습니다. ID: {}", saveArticle.getArticleId());
        return WriteArticleResponseDto.from(saveArticle, modelMapper);
    }

    // 게시글 리스트 => 검색어 추가 수정 필요
    public Page<ViewArticleResponseDto> listArticles(Pageable pageable, SearchCond searchCond) {
        return articleRepository.search(searchCond, pageable);
    }

    public ViewArticleResponseDto getArticle(Long id) {
        Article viewArticle = findArticleById(id);
        return ViewArticleResponseDto.from(viewArticle, modelMapper);
    }

    @Transactional
    public UpdateArticleResponseDto updateArticle(Long id, UpdateArticleRequestDto requestDto) {
        Article article = findArticleById(id);
        Article updateArticle = article.update(requestDto);
        log.info("ID {}의 게시글이 업데이트되었습니다.", id);
        return UpdateArticleResponseDto.from(updateArticle, modelMapper);
    }

    @Transactional
    public void deleteArticle(Long id) {
        Article article = findArticleById(id);
        articleRepository.delete(article);
        log.info("ID {}의 게시글이 성공적으로 삭제되었습니다.", id);
    }

    private Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID " + id + "에 해당하는 게시글을 찾을 수 없습니다."));
    }
}
