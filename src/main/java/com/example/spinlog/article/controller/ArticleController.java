package com.example.spinlog.article.controller;

import com.example.spinlog.article.controller.request.SearchCondRequestDto;
import com.example.spinlog.article.controller.request.UpdateArticleRequestDto;
import com.example.spinlog.article.controller.request.WriteArticleRequestDto;
import com.example.spinlog.article.service.ArticleService;
import com.example.spinlog.article.service.response.ViewArticleResponseDto;
import com.example.spinlog.article.service.response.ViewListResponseDto;
import com.example.spinlog.article.service.response.WriteArticleResponseDto;
import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResponseWrapper<WriteArticleResponseDto> create(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @RequestBody @Valid WriteArticleRequestDto article) {
        String userName = oAuth2User.getOAuth2Response().getAuthenticationName();
        WriteArticleResponseDto responseDto = articleService.createArticle(userName, article.toServiceRequest());
        log.info("게시글 작성 성공");
        return ResponseUtils.ok(responseDto, "게시글 작성 성공");
    }

    /**
     * 게시글 리스트
     *
     * @param pageable 페이지값, searchCondition 검색조건
     * @return 게시글 리스트를 포함하는 ResponseEntity
     */
    @GetMapping
    public ApiResponseWrapper<ViewListResponseDto> viewList(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                            @PageableDefault Pageable pageable,
                                                            SearchCondRequestDto searchCond) {
        String userName = oAuth2User.getOAuth2Response().getAuthenticationName();
        ViewListResponseDto responseDto = articleService.listArticles(userName, pageable, searchCond.toSearchCond());
        log.info("게시글 리스트 불러오기 성공");
        return ResponseUtils.ok(responseDto, "게시글 리스트 불러오기 성공");
    }

    /**
     * 게시글 1개 조회
     *
     * @param articleId 조회 요청 데이터 Id
     * @return 조회 게시글 객체를 포함하는 ResponseEntity
     */
    @GetMapping("/{articleId}")
    public ApiResponseWrapper<ViewArticleResponseDto> viewDetails(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @PathVariable Long articleId) {
        String userName = oAuth2User.getOAuth2Response().getAuthenticationName();
        ViewArticleResponseDto responseDto = articleService.getArticle(userName, articleId);
        log.info("게시글 1개 불러오기 성공");
        return ResponseUtils.ok(responseDto, "게시글 1개 불러오기 성공");
    }

    /**
     * 게시글 수정
     *
     * @param articleId        업데이트 요청 데이터 Id
     * @param updateRequestDto 업데이트 요청 데이터
     * @return 업데이트 성공 메시지 ResponseEntity
     */
    @PatchMapping("/{articleId}")
    public ApiResponseWrapper<Void> updateArticle(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @PathVariable Long articleId, @RequestBody @Valid UpdateArticleRequestDto updateRequestDto) {
        String userName = oAuth2User.getOAuth2Response().getAuthenticationName();
        articleService.updateArticle(userName, articleId, updateRequestDto.toServiceUpdateRequest());
        log.info("게시글 업데이트 성공");
        return ResponseUtils.ok("게시글 업데이트 성공");
    }

    /**
     * 게시글 삭제
     *
     * @param articleId 삭제 요청 데이터 Id
     * @return 삭제 성공 메시지 ResponseEntity
     */
    @DeleteMapping("/{articleId}")
    public ApiResponseWrapper<Void> deleteArticle(@AuthenticationPrincipal CustomOAuth2User oAuth2User, @PathVariable Long articleId) {
        String userName = oAuth2User.getOAuth2Response().getAuthenticationName();
        articleService.deleteArticle(userName, articleId);
        log.info("ID {}의 게시글이 삭제되었습니다.", articleId);
        return ResponseUtils.ok("게시글 삭제 성공");
    }
}
