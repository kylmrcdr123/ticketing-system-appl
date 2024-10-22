package com.rocs.ticketing.system.service.otp.attempt;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
/**
 * Manage OTP (One-Time Password) attempt limits.
 */
@Service
public class OtpAttemptService {
    private static final Logger logger = LoggerFactory.getLogger(OtpAttemptService.class);

    public static final int MAX_NUMBER_OF_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENTS = 1;
    private LoadingCache<String, Integer> otpAttemptCache;

    /**
     * Initializes the OTP attempt cache
     */
    public OtpAttemptService() {
        super();
        otpAttemptCache = CacheBuilder.newBuilder()
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
     * Evicts a user from the OTP attempt cache
     *
     * @param username the username to be evicted from the cache
     */
    public void evictUserFromLoginAttemptCache(String username) {
        otpAttemptCache.invalidate(username);
    }
    /**
     * Increments the OTP attempt count for the specified user.
     *
     * @param username the username whose OTP attempts will be incremented
     */
    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENTS + otpAttemptCache.get(username);
        } catch (ExecutionException e) {
            logger.error("Failed to retrieve OTP attempt count for user: {}", username, e);
        }
        otpAttemptCache.put(username, attempts);
    }
    /**
     * Checks if the specified user has exceeded the maximum number of OTP attempts.
     *
     * @param username username to check
     */
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return otpAttemptCache.get(username) >= MAX_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
