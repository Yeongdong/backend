package com.example.spinlog.article.controller;

import com.example.spinlog.article.dto.*;
import com.example.spinlog.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    /**
     * 게시글 작성
     *
     * @param article 등록 요청 데이터
     * @return 작성된 게시글 객체를 포함하는 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<WriteArticleResponseDTO> create(@RequestBody WriteArticleRequestDTO article) {
        WriteArticleResponseDTO responseDTO = articleService.createArticle(article);
        log.info("게시글 작성 성공");
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * 게시글 리스트 => 쿼리 추가 수정 필요
     *
     * @param pageable 페이지값, searchCondition 검색조건
     * @return 게시글 리스트를 포함하는 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<Page<ViewArticleResponseDTO>> viewList(Pageable pageable, SearchCond searchCond) {
        Page<ViewArticleResponseDTO> responseDto = articleService.listArticles(pageable, searchCond);
        log.info("게시글 리스트 불러오기 성공");
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 게시글 1개 조회
     *
     * @param id 조회 요청 데이터 Id
     * @return 조회 게시글 객체를 포함하는 ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<ViewArticleResponseDTO> viewDetails(@PathVariable Long id) {
        ViewArticleResponseDTO responseDTO = articleService.getArticle(id);
        log.info("게시글 1개 불러오기 성공");
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 게시글 수정
     *
     * @param id 업데이트 요청 데이터 Id
     * @param updateRequestDTO 업데이트 요청 데이터
     * @return 수정된 게시글 객체를 포함하는 ResponseEntity
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UpdateArticleResponseDTO> updateArticle(@PathVariable Long id, @RequestBody UpdateArticleRequestDTO updateRequestDTO) {
        UpdateArticleResponseDTO updatedArticle = articleService.updateArticle(id, updateRequestDTO);
        log.info("게시글 업데이트 성공");
        return ResponseEntity.ok(updatedArticle);
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제 요청 데이터 Id
     * @return 삭제 성공시 상태코드 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        log.info("ID {}의 게시글이 삭제되었습니다.", id);
        return ResponseEntity.noContent().build();
    }
}
