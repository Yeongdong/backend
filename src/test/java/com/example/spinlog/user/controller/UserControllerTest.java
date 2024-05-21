package com.example.spinlog.user.controller;

import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.user.custom.securitycontext.WithMockCustomOAuth2User;
import com.example.spinlog.user.dto.request.UpdateUserRequestDto;
import com.example.spinlog.user.dto.response.ViewUserResponseDto;
import com.example.spinlog.user.entity.BudgetEntity;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.spinlog.user.custom.securitycontext.OAuth2Provider.GOOGLE;
import static com.example.spinlog.user.custom.securitycontext.OAuth2Provider.KAKAO;
import static net.minidev.json.JSONValue.toJSONString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private UserService userService;

    @DisplayName("회원 정보 조회 - 정상")
    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void givenUser_whenRequestRetrievingUserInfo_thenReturnCorrectly() throws Exception {
        // Given
        CustomOAuth2User oAuth2User = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = User.builder()
                .email(oAuth2User.getName())
                .mbti(Mbti.INFP)
                .gender(Gender.MALE)
                .build();
        user.addCurrentMonthBudget(99_999_999, LocalDate.now());
        ViewUserResponseDto responseDto = ViewUserResponseDto.of(user, 99_999_999);

        when(userService.findUser())
                .thenReturn(responseDto);


        //When
        ResultActions actions = mvc.perform(
                get("/api/users/details")
        );


        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.email").value(responseDto.getEmail()))
                .andExpect(jsonPath("$.data.mbti").value(responseDto.getMbti()))
                .andExpect(jsonPath("$.data.gender").value(responseDto.getGender()))
                .andExpect(jsonPath("$.data.budget").value(responseDto.getBudget()));

        verify(userService, times(1))
                .findUser();
    }

    @DisplayName("회원 정보 저장 및 변경 - 정상")
    @Test
    @WithMockCustomOAuth2User(
            provider = GOOGLE, email = "googleemail@kakao.com", providerMemberId = "google-member-Id-123", isFirstLogin = false
    )
    void givenUser_whenRequestUpdatingUserInfo_thenUpdateCorrectly() throws Exception {
        // Given
        CustomOAuth2User oAuth2User = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = User.builder()
                .email(oAuth2User.getName())
                .mbti(Mbti.ESTJ)
                .gender(Gender.FEMALE)
                .authenticationName(oAuth2User.getOAuth2Response().getAuthenticationName())
                .build();
        BudgetEntity budget = user.addCurrentMonthBudget(12_345_678, LocalDate.now());

        System.out.println("user.getEmail() = " + user.getEmail());
        System.out.println("user.getMbti() = " + user.getMbti());
        System.out.println("user.getGender() = " + user.getGender());
        System.out.println("user.getBudget() = " + budget.getBudget());
        System.out.println("user.getAuthenticationName() = " + user.getAuthenticationName());

        UpdateUserRequestDto requestDto = UpdateUserRequestDto.builder()
                .mbti("ENTP")
                .gender("MALE")
                .budget(12_345)
                .build();

        /*
        doAnswer().when(). ...
        updateUserInfo() 에 any() 없이 그냥 requestDto 적으면 테스트 실패
        실제 컨트롤러에서 updateUserInfo() 를 호출할 때, 이 테스트 코드에서 작성한 requestDto 를 사용하는 것이 아니기 때문
        */
        doAnswer(invocation -> {
            user.change(requestDto.getMbti(),
                        requestDto.getGender());
            budget.change(requestDto.getBudget());
            return null;
        }).when(userService).updateUserInfo(any(UpdateUserRequestDto.class));


        // When
        //별도의 설정파일을 적용해주지 않으면, GET 요청을 제외한 모든 요청에는 기본적으로 csrf 를 사용하기 때문에 with(csrf()) 추가
        ResultActions actions = mvc.perform(
                post("/api/users/details")
                        .content(toJSONString(requestDto))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
        );


        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User 정보 저장에 성공했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService, times(1))
                .updateUserInfo(any(UpdateUserRequestDto.class)); //여기서도 위와 마찬가지로 any()로 클래스 타입 일치 여부를 검사

        assertThat(user)
                .hasFieldOrPropertyWithValue("mbti", Mbti.valueOf(requestDto.getMbti()))
                .hasFieldOrPropertyWithValue("gender", Gender.valueOf(requestDto.getGender()));

        assertThat(budget)
                .hasFieldOrPropertyWithValue("budget", requestDto.getBudget());
    }

}