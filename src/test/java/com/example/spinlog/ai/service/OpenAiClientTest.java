package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.AiRequestDto;
import com.example.spinlog.ai.dto.CommentRequest;
import com.example.spinlog.ai.dto.CommentResponse;
import com.example.spinlog.ai.dto.Message;
import com.example.spinlog.article.dto.WriteArticleRequestDto;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.global.security.oauth2.user.CustomOAuth2User;
import com.example.spinlog.user.custom.securitycontext.WithMockCustomOAuth2User;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.example.spinlog.user.custom.securitycontext.OAuth2Provider.KAKAO;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OpenAiClientTest {

    private final static String AI_MODEL = "gpt-3.5-turbo";
    private final static String AI_ROLE = "system";
    private final static String USER_ROLE = "user";
    private final static String MESSAGE_TO_AI = "You are collecting emotional and consumption data from users in their 20s and 30s. Connect emotions and consumption and provide users with consumption-related advice in one sentence in Korean.";

    @MockBean
    private OpenAiClient openAiClient;

    @Autowired  // userId가 필요해 Autowired 설정
    private UserRepository userRepository;

    @Autowired  // articleId가 필요해 Autowired 설정
    private ArticleRepository articleRepository;

    @Test
    @WithMockCustomOAuth2User(
            provider = KAKAO, email = "kakaoemail@kakao.com", providerMemberId = "123ab", isFirstLogin = false
    )
    void testGetAiComment() {
        // Given
        CustomOAuth2User oAuth2User = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = oAuth2User.getOAuth2Response().getAuthenticationName();
        User buildUser = User.builder()
                .email(oAuth2User.getName())
                .mbti(Mbti.ISTP)
                .gender(Gender.MALE)
                .authenticationName(authenticationName)
                .build();
        User user = userRepository.save(buildUser);

        WriteArticleRequestDto WriteRequestDto = WriteArticleRequestDto.builder()
                .content("투썸플레이스 아이스아메리카노")
                .spendDate("2024-04-04T11:22:33")
                .event("부장님께 혼남")
                .thought("회사 그만 두고 싶다")
                .emotion("ANNOYED")
                .satisfaction(2F)
                .reason("홧김에 돈 쓴게 마음에 들지 않는다")
                .improvements("소비 전에 한번 더 생각하고 참아본다")
                .amount(5000)
                .registerType("SPEND")
                .build();
        Article article = articleRepository.save(WriteRequestDto.toEntity(user));

        AiRequestDto requestDto = AiRequestDto.builder()
                .articleId(article.getArticleId())
                .build();

        String authorization = "test-key";

        Message message1 = Message.builder()
                .role(AI_ROLE)
                .content(MESSAGE_TO_AI)
                .build();
        Message message2 = Message.builder()
                .role(USER_ROLE)
                .content(requestDto.toString())
                .build();
        List<Message> messages = Arrays.asList(message1, message2);

        CommentRequest commentRequest = CommentRequest.builder()
                .model(AI_MODEL)
                .messages(messages)
                .build();
        CommentResponse fakeResponse = new CommentResponse();

        when(openAiClient.getAiComment(authorization, commentRequest))
                .thenReturn(fakeResponse);

        // When
        CommentResponse response = openAiClient.getAiComment(authorization, commentRequest);

        // Then
        assertThat(response).isNotNull();
    }
}