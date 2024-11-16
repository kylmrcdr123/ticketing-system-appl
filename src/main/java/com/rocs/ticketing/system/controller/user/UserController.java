package com.rocs.ticketing.system.controller.user;

import com.rocs.ticketing.system.domain.user.User;
import com.rocs.ticketing.system.domain.user.principal.UserPrincipal;
import com.rocs.ticketing.system.exception.domain.*;
import com.rocs.ticketing.system.service.user.UserService;
import com.rocs.ticketing.system.utils.security.jwt.provider.token.JWTTokenProvider;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.rocs.ticketing.system.utils.security.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    /**
     * Constructs a new UserController with the provided services.
     *
     * @param userService service handling user operations
     * @param authenticationManager handles authentication
     * @param jwtTokenProvider provides JWT token
     */
    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    /**
     * Registers a new user.
     *
     * @param user user to register
     * @return the newly registered user
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user)
            throws UsernameNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, PersonExistsException, UserNotFoundException {
        User newUser = this.userService.register(user);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
    /**
     * Initiates a password reset process.
     *
     * @param user user requesting the password reset
     * @return user with reset instructions
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<User> forgotPassword(@RequestBody User user)
            throws UsernameNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, PersonExistsException, UserNotFoundException {
        User newUser = this.userService.forgotPassword(user);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
    /**
     * Verifies OTP for password reset.
     *
     * @param user user verifying the OTP
     * @return user if OTP verification is successful
     */
    @PostMapping("/verify-forgot-password")
    public ResponseEntity<User> verifyForgotPassword(@RequestBody User user)
            throws UsernameNotFoundException, PersonExistsException, OtpExistsException {
        User newUser = this.userService.verifyOtpForgotPassword(user);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
    /**
     * Verifies OTP to unlock the user account.
     *
     * @param request contains username and OTP
     * @return success or failure message based on OTP validation
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otp = request.get("otp");

        if (username == null || otp == null) {
            return new ResponseEntity<>("Both username and otp are required", HttpStatus.BAD_REQUEST);
        }
        boolean isVerified = userService.verifyOtp(username, otp);
        if (isVerified) {
            return new ResponseEntity<>("Account unlocked successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid OTP", HttpStatus.UNAUTHORIZED);
        }
    }
    /**
     * Log in the user and generates a JWT token.
     *
     * @param user user attempting to log in
     * @return the logged-in user with JWT in headers
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) throws UsernameNotFoundException {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);

        String token = jwtTokenProvider.generateJwtToken(userPrincipal);
        HttpHeaders jwtHeaders = new HttpHeaders();
        jwtHeaders.add("Authorization", "Bearer " + token); // Make sure "Authorization" is used here

        return new ResponseEntity<>(loginUser, jwtHeaders, HttpStatus.OK);
    }

    /**
     * Retrieves the list of all users.
     *
     * @return list of all users
     */
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/exists/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.checkUsernameExists(username);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
    /**
     * Authenticates the user using username and password.
     *
     * @param username the username
     * @param password the password
     */
    private void authenticate(String username, String password) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
    /**
     * Generates JWT headers for the user.
     *
     * @param userPrincipal user's principal
     * @return HTTP headers with the JWT token
     */
    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }


}
