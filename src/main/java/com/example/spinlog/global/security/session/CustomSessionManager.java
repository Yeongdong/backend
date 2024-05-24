package com.example.spinlog.global.security.session;

import com.example.spinlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomSessionManager {
    private static final Map<String, CustomSession> sessions = new ConcurrentHashMap<>();

    public static void createSession(String sessionId, String authenticationName) {
        sessions.put(
                sessionId,
                CustomSession.builder()
                        .authenticationName(authenticationName)
                        .createdTime(LocalDateTime.now())
                        .lastAccessedTime(LocalDateTime.now())
                        .build());
    }

    public static void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static Optional<CustomSession> getSession(String sessionId) {
        if(sessions.containsKey(sessionId)) {
            CustomSession session = sessions.get(sessionId);
            session.updateLastAccessedTime();
            return Optional.of(session);
        }
        return Optional.empty();
    }
}
