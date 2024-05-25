package com.example.spinlog.global.security.session;

import com.example.spinlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CustomSessionManager {
    private static final Map<String, CustomSession> sessions = new ConcurrentHashMap<>();

    // TODO 세션 탈취 케이스 고려

    public void createSession(String sessionId, String authenticationName) {
        sessions.put(
                sessionId,
                CustomSession.builder()
                        .authenticationName(authenticationName)
                        .createdTime(LocalDateTime.now())
                        .lastAccessedTime(LocalDateTime.now())
                        .build());
    }

    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public Optional<CustomSession> getSession(String sessionId) {
        if(sessions.containsKey(sessionId)) {
            CustomSession session = sessions.get(sessionId);
            session.updateLastAccessedTime();
            return Optional.of(session);
        }
        return Optional.empty();
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void deleteExpiredSessions() {
        log.info("deleteExpiredSessions, before size: " + sessions.size());
        sessions.entrySet().removeIf(entry -> entry.getValue()
                .getLastAccessedTime()
                .isBefore(LocalDateTime.now().minusHours(1)));
    }
}
