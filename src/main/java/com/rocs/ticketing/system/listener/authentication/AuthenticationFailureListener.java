package com.rocs.ticketing.system.listener.authentication;


import com.rocs.ticketing.system.service.login.attempt.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

/**
 *  This handles authentication failure events.
 */
@Component
public class AuthenticationFailureListener {

    public LoginAttemptService loginAttemptService;
    /**
     * Constructs an AuthenticationFailureListener with the provided LoginAttemptService.
     *
     * @param loginAttemptService service responsible for handling login attempts
     */
    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
    /**
     * Listens for authentication failures caused by bad credentials and adds the user's
     *
     * @param event event triggered on authentication failure
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }

    }

}
