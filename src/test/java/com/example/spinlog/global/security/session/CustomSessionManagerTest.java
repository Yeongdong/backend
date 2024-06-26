package com.example.spinlog.global.security.session;

import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CustomSessionManagerTest {
    private CustomSessionManager customSessionManager;

    @BeforeEach
    void setUp() {
        customSessionManager = new CustomSessionManager();
    }

    @Nested
    class createSession {
        @Test
        void 새로운_세션을_생성한다() throws Exception {
            // when
            String sessionId = customSessionManager.createSession("testName");

            // then
            assertThat(sessionId).isNotNull();
            Optional<CustomSession> session = customSessionManager.getSession(sessionId);
            assertThat(session.isPresent()).isTrue();
            assertThat(session.get().getAuthenticationName()).isEqualTo("testName");
        }

        @Test
        void 기존_세션이_있는_경우_기존_세션을_제거하고_새로운_세션을_생성한다() throws Exception {
            // given
            String oldSessionId = customSessionManager.createSession("testName");

            // when
            String newSessionId = customSessionManager.createSession("testName");

            // then
            assertThat(oldSessionId).isNotEqualTo(newSessionId);
            Optional<CustomSession> session = customSessionManager.getSession(oldSessionId);
            assertThat(session.isEmpty()).isTrue();
            Optional<CustomSession> newSession = customSessionManager.getSession(newSessionId);
            assertThat(newSession.isPresent()).isTrue();
            assertThat(newSession.get().getAuthenticationName()).isEqualTo("testName");
        }
    }

    @Nested
    class deleteSession {
        @Test
        void 세션을_삭제한다() throws Exception{
            // given
            String sessionId = customSessionManager.createSession("testName");

            // when
            customSessionManager.deleteSession(sessionId);

            // then
            Optional<CustomSession> session = customSessionManager.getSession(sessionId);
            assertThat(session.isEmpty()).isTrue();
        }
    }

    @Nested
    class getSession {
        @Test
        void 세션을_가져온다() throws Exception {
            // given
            String sessionId = customSessionManager.createSession("testName");

            // when
            Optional<CustomSession> session = customSessionManager.getSession(sessionId);

            // then
            assertThat(session.isPresent()).isTrue();
            assertThat(session.get().getAuthenticationName()).isEqualTo("testName");
        }

        @Test
        void 세션의_lastAccessedTime을_업데이트한다() throws Exception {
            // given
            String sessionId = customSessionManager.createSession("testName");
            CustomSession customSession = customSessionManager.getSession(sessionId).get();

            LocalDateTime lastAccessedTime = setLastAccessedTime(customSession, LocalDateTime.now().minusHours(3));

            // when
            Optional<CustomSession> session = customSessionManager.getSession(sessionId);

            // then
            assertThat(session.isPresent()).isTrue();
            assertThat(session.get().getLastAccessedTime()).isAfter(lastAccessedTime);
        }
    }

    @Nested
    class deleteExpiredSessions {
        @Test
        void lastAccessedTime이_1시간이_넘은_세션들을_삭제한다() throws Exception {
            // given
            String sessionId = customSessionManager.createSession("testName");
            CustomSession customSession = customSessionManager.getSession(sessionId).get();
            setLastAccessedTime(customSession, LocalDateTime.now().minusHours(1).minusSeconds(1));

            // when
            customSessionManager.deleteExpiredSessions();

            // then
            Optional<CustomSession> session = customSessionManager.getSession(sessionId);
            assertThat(session.isEmpty()).isTrue();
        }

        @Test
        void lastAccessedTime이_1시간이_넘지_않은_세션들은_삭제되지_않는다() throws Exception {
            // given
            String sessionId = customSessionManager.createSession("testName");
            CustomSession customSession = customSessionManager.getSession(sessionId).get();
            setLastAccessedTime(customSession, LocalDateTime.now().minusHours(1).plusSeconds(1));

            // when
            customSessionManager.deleteExpiredSessions();

            // then
            Optional<CustomSession> session = customSessionManager.getSession(sessionId);
            assertThat(session.isPresent()).isTrue();
        }
    }

    private static LocalDateTime setLastAccessedTime(CustomSession customSession, LocalDateTime dateTime) throws NoSuchFieldException, IllegalAccessException {
        Field lastAccessedTime = customSession.getClass().getDeclaredField("lastAccessedTime");
        lastAccessedTime.setAccessible(true);
        lastAccessedTime.set(customSession, dateTime);
        return dateTime;
    }
}