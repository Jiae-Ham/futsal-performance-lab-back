package com.alpaca.futsal_performance_lab_back.service.user;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, long expirationMillis) {
        long expireAt = System.currentTimeMillis() + expirationMillis;
        blacklist.put(token, expireAt);
    }

    public boolean isBlacklisted(String token) {
        Long exprieAt = blacklist.get(token);
        if (exprieAt == null) return false;
        if (exprieAt < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
