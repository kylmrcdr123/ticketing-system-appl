package com.rocs.ticketing.system.service.user;

import com.rocs.ticketing.system.domain.user.User;
import com.rocs.ticketing.system.exception.domain.*;
import jakarta.mail.MessagingException;

import java.util.List;

public interface UserService {
    /**
     * Registers a new user
     *
     * @param user the user containing details for registration
     * @return the registered user
     */
    User register(User user) throws UsernameNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, PersonExistsException, UserNotFoundException;
    /**
     * Handles password recovery
     *
     * @param user  user containing the username for recovery
     * @return the updated user with OTP
     */
    User forgotPassword(User user) throws UsernameNotFoundException, MessagingException;
    /**
     * Retrieves list of all users.
     *
     * @return list of users
     */
    List<User> getUsers();
    /**
     * Find user by username.
     *
     * @param username username of the user to find
     * @return the user
     */
    User findUserByUsername(String username);
    /**
     * Verifies the OTP provided for password recovery.
     *
     * @param newUser the user containing the OTP and new password
     * @return the updated user with the new password
     */
    User verifyOtpForgotPassword(User newUser) throws PersonExistsException, OtpExistsException;
    /**
     * Verifies the OTP for user account activation.
     *
     * @param username the username of the user
     * @param otp the OTP to verify
     */
    boolean verifyOtp(String username, String otp);
    boolean checkUsernameExists(String username);
}
