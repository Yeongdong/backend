package com.example.spinlog.ai.service;

import com.example.spinlog.ai.dto.AiRequestDto;
import com.example.spinlog.ai.dto.CommentRequest;
import com.example.spinlog.ai.dto.CommentResponse;
import com.example.spinlog.ai.dto.Message;
import com.example.spinlog.article.controller.request.WriteArticleRequestDto;
import com.example.spinlog.article.entity.Article;
import com.example.spinlog.article.repository.ArticleRepository;
import com.example.spinlog.article.service.request.ArticleCreateRequest;
import com.example.spinlog.global.error.exception.ai.EmptyCommentException;
import com.example.spinlog.user.entity.Gender;
import com.example.spinlog.user.entity.Mbti;
import com.example.spinlog.user.entity.User;
import com.example.spinlog.user.repository.UserRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = {WireMockConfig.class})
@AutoConfigureWireMock(port = 0)
@Transactional
@Slf4j
public class OpenAiClientTest {

    private final static String AI_MODEL = "gpt-3.5-turbo";
    private final static String AI_ROLE = "system";
    private final static String USER_ROLE = "user";
    private final static String MESSAGE_TO_AI = "Your role is to give advice.\n" +
            "The data is from a person in their 20s or 30s who made a purchase due to emotional expression. You need to provide empathy and advice based on the data.\n" +
            "Please follow the rules below when responding.\n" +
            "\n" +
            "Include an empathetic response based on the user's provided emotion, event, thoughts, amount, and spending details (or saving details).\n" +
            "Give a improvement suggestions.\n" +
            "Provide advice based on the user's provided emotion, event, thoughts, amount, and spending details (or saving details). Also, explain why you recommend each suggestion.\n" +
            "Example: If you feel (emotion) and want to (action), how about trying this? Improvement suggestion" +
            "Use up to 100 Korean characters.\n" +
            "Speak in a friendly tone as if talking to a friend, using the informal polite speech style (~해요) instead of the formal style (~다나까).\n" +
            "Include references to the latest trends, memes, or news in Korea.";

    @Autowired
    private OpenAiClient openAiClient;

    @Autowired  // userId가 필요해 Autowired 설정
    private UserRepository userRepository;

    @Autowired  // articleId가 필요해 Autowired 설정
    private ArticleRepository articleRepository;

    @Autowired
    private WireMockServer wireMockServer;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() throws FileNotFoundException {
        AiMocks.setupAiMockResponse(wireMockServer);
    }

    @Test
    @DisplayName("서버에 요청을 보내면 지정된 답변을 반환받는다.")
    void testGetAiComment() {
        // Given
        User buildUser = User.builder()
                .email("kakaoemail@kakao.com")
                .mbti(Mbti.ISTP)
                .gender(Gender.MALE)
                .authenticationName("123ab")
                .build();
        User user = userRepository.save(buildUser);

        ArticleCreateRequest writeRequestDto = ArticleCreateRequest.builder()
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
        Article article = articleRepository.save(writeRequestDto.toEntity(user));

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

        // When
        CommentResponse response = openAiClient.getAiComment(authorization, commentRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getChoices()
                .stream()
                .findFirst()
                .map(choice -> choice.getMessage().getContent())
                .orElseThrow(() -> new EmptyCommentException("fail to get ai comment"))).isEqualTo("This is a test!");
    }
}