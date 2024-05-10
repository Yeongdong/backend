package com.example.spinlog.article.controller;

import com.example.spinlog.article.dto.*;
import com.example.spinlog.article.service.ArticleService;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ApiResponseWrapper<WriteArticleResponseDto> create(@RequestBody WriteArticleRequestDto article) {
        WriteArticleResponseDto responseDto = articleService.createArticle(article);
        log.info("게시글 작성 성공");
        return ResponseUtils.ok(responseDto, "게시글 작성 성공");  // info 로그 삭제 여부에 따라 변수로 관리하던지 아니면 직접 문자열을 넣을지 판단 필요
    }

    /**
     * 게시글 리스트 => 쿼리 추가 수정 필요
     *
     * @param pageable 페이지값, searchCondition 검색조건
     * @return 게시글 리스트를 포함하는 ResponseEntity
     */
    @GetMapping
    public ApiResponseWrapper<Page<ViewArticleListResponseDto>> viewList(Pageable pageable, SearchCond searchCond) {
        Page<ViewArticleListResponseDto> responseDto = articleService.listArticles(pageable, searchCond);
        log.info("게시글 리스트 불러오기 성공");
        return ResponseUtils.ok(responseDto, "게시글 리스트 불러오기 성공");
    }

    /**
     * 게시글 1개 조회
     *
     * @param id 조회 요청 데이터 Id
     * @return 조회 게시글 객체를 포함하는 ResponseEntity
     */
    @GetMapping("/{id}")
    public ApiResponseWrapper<ViewArticleResponseDto> viewDetails(@PathVariable Long id) {
        ViewArticleResponseDto responseDto = articleService.getArticle(id);
        log.info("게시글 1개 불러오기 성공");
        return ResponseUtils.ok(responseDto, "게시글 1개 불러오기 성공");
    }

    /**
     * 게시글 수정
     *
     * @param id               업데이트 요청 데이터 Id
     * @param updateRequestDTO 업데이트 요청 데이터
     * @return 업데이트 성공 메시지 ResponseEntity
     */
    @PatchMapping("/{id}")
    public ApiResponseWrapper<Void> updateArticle(@PathVariable Long id, @RequestBody UpdateArticleRequestDto updateRequestDTO) {
        articleService.updateArticle(id, updateRequestDTO);
        log.info("게시글 업데이트 성공");
        return ResponseUtils.ok("게시글 업데이트 성공");
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제 요청 데이터 Id
     * @return 삭제 성공시 상태코드 204
     */
    @DeleteMapping("/{id}")
    public ApiResponseWrapper<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        log.info("ID {}의 게시글이 삭제되었습니다.", id);
        return ResponseUtils.ok("게시글 삭제 성공");
    }
}
