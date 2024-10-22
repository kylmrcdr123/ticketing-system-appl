package com.rocs.ticketing.system.listener.authentication;

import com.rocs.ticketing.system.domain.user.User;
import com.rocs.ticketing.system.service.login.attempt.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 *  This handles authentication success events.
 */
@Component
public class AuthenticationSuccessListener {

    private LoginAttemptService loginAttemptService;

    /**
     * Constructs an AuthenticationSuccessListener with the provided LoginAttemptService
     *
     * @param loginAttemptService service responsible for managing login attempts
     */
    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
    /**
     * Listens for successful authentication events and evicts the user from the login attempt cache.
     *
     * @param event event triggered on successful authentication
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof User) {
            User user = (User) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
