package com.example.spinlog.article.service;

import com.example.spinlog.article.dto.*;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public WriteArticleResponseDto createArticle(String userName, WriteArticleRequestDto requestDto) {
        User user = getUser(userName);
        Article articleEntity = requestDto.toEntity(user);
        Article savedArticle = articleRepository.save(articleEntity);
        log.info("게시글이 성공적으로 저장되었습니다. ID: {}", savedArticle.getArticleId());
        return WriteArticleResponseDto.from(savedArticle, modelMapper);
    }

    public ViewListResponseDto listArticles(String userName, Pageable pageable, SearchCondRequestDto searchCondDto) {
        User user = getUser(userName);
        SearchCond searchCond = searchCondDto.toSearchCond();

        Page<ViewArticleSumDto> response = articleRepository.search(user, pageable, searchCond);
        return ViewListResponseDto.builder()
                .nextPage(!response.isLast())
                .spendList(response.getContent())
                .build();
    }

    public ViewArticleResponseDto getArticle(String userName, Long id) {
        User user = getUser(userName);
        Article viewArticle = findArticleById(id);
        validateUserArticle(user, viewArticle);
        return ViewArticleResponseDto.from(viewArticle, modelMapper);
    }

    @Transactional
    public void updateArticle(String userName, Long id, UpdateArticleRequestDto requestDto) {
        User user = getUser(userName);
        Article updateArticle = findArticleById(id);
        validateUserArticle(user, updateArticle);
        updateArticle.update(requestDto);
        log.info("ID {}의 게시글이 업데이트되었습니다.", id);
    }

    @Transactional
    public void deleteArticle(String userName, Long id) {
        User user = getUser(userName);
        Article deleteArticle = findArticleById(id);
        validateUserArticle(user, deleteArticle);
        articleRepository.delete(deleteArticle);
        log.info("ID {}의 게시글이 성공적으로 삭제되었습니다.", id);
    }

    public Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID " + id + "에 해당하는 게시글을 찾을 수 없습니다."));
    }

    private User getUser(String userName) {
        return userRepository.findByAuthenticationName(userName)
                .orElseThrow(() -> new NoSuchElementException(userName + "에 해당하는 회원을 찾을 수 없습니다."));
    }

    private void validateUserArticle(User user, Article article) {
        if (!article.getUser().equals(user)) {
            throw new IllegalArgumentException("해당 게시글에 대한 권한이 없습니다.");
        }
    }
}
