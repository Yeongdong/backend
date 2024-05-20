package com.example.spinlog.user.controller;

import com.example.spinlog.global.response.ApiResponseWrapper;
import com.example.spinlog.global.response.ResponseUtils;
import com.example.spinlog.user.dto.request.UpdateUserRequestDto;
import com.example.spinlog.user.dto.response.ViewUserResponseDto;
import com.example.spinlog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 회원 정보 조회
     *
     * @return 회원 정보 (email, mbti, gender, budget) 를 담은 ResponseApi
     */
    @GetMapping("/details")
    public ApiResponseWrapper<ViewUserResponseDto> viewDetails() {
        ViewUserResponseDto responseDto = userService.findUser();

        return ResponseUtils.ok(responseDto, "User 정보 조회에 성공했습니다.");
    }


    /**
     * 회원 정보 저장 및 수정
     *
     * @param requestDto 수정할 회원 정보 (mbti, gender, budget)
     * @return 성공 또는 실패 ResponseApi
     */
    @PostMapping("/details")
    public ApiResponseWrapper<Object> storeDetails(@RequestBody UpdateUserRequestDto requestDto) {
        userService.updateUserInfo(requestDto);

        return ResponseUtils.ok("User 정보 저장에 성공했습니다.");
    }

}
