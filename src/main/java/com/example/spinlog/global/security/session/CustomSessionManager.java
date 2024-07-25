package com.example.spinlog.global.security.session;

import com.example.spinlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CustomSessionManager {
    private static final Map<String, CustomSession> sessions = new ConcurrentHashMap<>();
    private static final Map<String, String> authNameToSessionIdMap = new ConcurrentHashMap<>();

    public String createSession(String authenticationName, String email) {
        if(authNameToSessionIdMap.containsKey(authenticationName)){
            String oldSessionId = authNameToSessionIdMap.get(authenticationName);
            sessions.remove(oldSessionId);
            authNameToSessionIdMap.remove(authenticationName);
            log.info("createSession, remove old session: " + oldSessionId);
        }

        String sessionId = UUID.randomUUID().toString();
        sessions.put(
                sessionId,
                CustomSession.builder()
                        .authenticationName(authenticationName)
                        .email(email)
                        .createdTime(LocalDateTime.now())
                        .lastAccessedTime(LocalDateTime.now())
                        .build());
        authNameToSessionIdMap.put(authenticationName, sessionId);

        return sessionId;
    }

    public void deleteSession(String sessionId) {
        CustomSession removed = sessions.remove(sessionId);
        if(removed != null)
            authNameToSessionIdMap.remove(removed.getAuthenticationName());
    }

    public Optional<CustomSession> getSession(String sessionId) {
        if(sessionId == null)
            return Optional.empty();
        if(sessions.containsKey(sessionId)) {
            CustomSession session = sessions.get(sessionId);
            session.updateLastAccessedTime();
            return Optional.of(session);
        }
        return Optional.empty();
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void deleteExpiredSessions() {
        int beforeDeletion = sessions.size();

        sessions.entrySet().removeIf(entry -> {
            if (entry.getValue().getLastAccessedTime().isBefore(LocalDateTime.now().minusHours(1))) {
                authNameToSessionIdMap.remove(entry.getValue().getAuthenticationName());
                return true;
            }
            return false;
        });

        log.info("deleteExpiredSessions, " + beforeDeletion + " -> " + sessions.size());
    }
}
