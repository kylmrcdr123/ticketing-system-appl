package com.rocs.ticketing.system.service.login.attempt;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
/**
 * Manage login attempts and prevent brute force attacks.
 */
@Service
public class LoginAttemptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptService.class);

    public static final int MAX_NUMBER_OF_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENTS = 1;

    private LoadingCache<String, Integer> loginAttemptCache;

    /**
     * Initializes the login attempt cache.
     */
    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }
    /**
     * Removes a user from the login attempt cache
     *
     * @param username username to remove from the cache
     */
    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }
    /**
     * Increments the login attempt count for user.
     *
     * @param username username to increment login attempts
     */
    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENTS + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            LOGGER.error("Failed to load login attempts for user: {}", username, e);
        }
        loginAttemptCache.put(username, attempts);
    }
    /**
     * Checks if a user has exceeded the maximum number
     *
     * @param username username to check
     */
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAX_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            LOGGER.error("Failed to retrieve login attempts for user: {}", username, e);
        }
        return false;
    }
}
