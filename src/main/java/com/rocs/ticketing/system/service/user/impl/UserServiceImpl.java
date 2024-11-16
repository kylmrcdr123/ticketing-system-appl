package com.rocs.ticketing.system.service.user.impl;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.domain.user.User;
import com.rocs.ticketing.system.domain.user.principal.UserPrincipal;
import com.rocs.ticketing.system.exception.domain.OtpExistsException;
import com.rocs.ticketing.system.exception.domain.PersonExistsException;
import com.rocs.ticketing.system.exception.domain.UserNotFoundException;
import com.rocs.ticketing.system.exception.domain.UsernameExistsException;
import com.rocs.ticketing.system.repository.employee.EmployeeRepository;
import com.rocs.ticketing.system.repository.misStaff.MisStaffRepository;
import com.rocs.ticketing.system.repository.student.StudentRepository;
import com.rocs.ticketing.system.repository.user.UserRepository;
import com.rocs.ticketing.system.service.email.EmailService;
import com.rocs.ticketing.system.service.login.attempt.LoginAttemptService;
import com.rocs.ticketing.system.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.rocs.ticketing.system.utils.security.enumeration.Role.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private MisStaffRepository misStaffRepository;
    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private EmployeeRepository employeeRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           StudentRepository studentRepository,
                           EmployeeRepository employeeRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService, MisStaffRepository misStaffRepository) {
        this.employeeRepository = employeeRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.misStaffRepository = misStaffRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error("Username not found...");
            throw new UsernameNotFoundException("Username not found.");
        }

        validateLoginAttempt(user);
        user.setLastLoginDate(new Date());
        this.userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        LOGGER.info("User information found...");
        return userPrincipal;
    }

    private void validateLoginAttempt(User user) {
        if (!user.isLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setLocked(true);
            } else {
                user.setLocked(false);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(User newUser) throws UsernameNotFoundException, UsernameExistsException, MessagingException, PersonExistsException, UserNotFoundException {
        validateNewUsername(newUser.getUsername());  // Check if username already exists
        validatePassword(newUser.getPassword());     // Validate password strength
        String otp = generateOTP();  // Generate a One Time Password (OTP)
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setJoinDate(new Date());
        user.setActive(true);

        // Handle Student Registration
        if (newUser.getStudent() != null && newUser.getStudent().getStudentNumber() != null) {
            handleStudentRegistration(newUser, user, otp);
        }
        // Handle Employee Registration
        else if (newUser.getEmployee() != null && newUser.getEmployee().getEmployeeNumber() != null) {
            handleEmployeeRegistration(newUser, user, otp);
        }
        // Handle MIS Staff Registration
        else if (newUser.getMisStaff() != null && newUser.getMisStaff().getMisStaffNumber() != null) {
            handleMisStaffRegistration(newUser, user, otp);
        } else {
            throw new PersonExistsException("Invalid data. Ensure all necessary fields are filled.");
        }

        userRepository.save(user);  // Save the new user to the repository
        LOGGER.info("User registered successfully!");
        return user;
    }

    private void handleStudentRegistration(User newUser, User user, String otp) throws MessagingException {
        String studentNumber = newUser.getStudent().getStudentNumber();
        String email = newUser.getStudent().getEmail();

        if (userRepository.existsByUserId(studentNumber)) {
            throw new PersonExistsException("Student already exists!");
        }

        Students student = newUser.getStudent();
        studentRepository.save(student);

        emailService.sendNewPasswordEmail(email, otp);
        user.setUserId(studentNumber);
        user.setOtp(otp);
        user.setLocked(true);
        user.setRole(ROLE_STUDENT.name());
        user.setAuthorities(Arrays.asList(ROLE_STUDENT.getAuthorities()));
    }

    private void handleEmployeeRegistration(User newUser, User user, String otp) throws MessagingException {
        String employeeNumber = newUser.getEmployee().getEmployeeNumber();
        String email = newUser.getEmployee().getEmail();

        if (userRepository.existsByUserId(employeeNumber)) {
            throw new PersonExistsException("Employee already exists!");
        }

        Employees employee = newUser.getEmployee();
        employeeRepository.save(employee);

        emailService.sendNewPasswordEmail(email, otp);
        user.setUserId(employeeNumber);
        user.setOtp(otp);
        user.setLocked(true);
        user.setRole(ROLE_EMPLOYEE.name());
        user.setAuthorities(Arrays.asList(ROLE_EMPLOYEE.getAuthorities()));
    }

    private void handleMisStaffRegistration(User newUser, User user, String otp) throws MessagingException {
        String misStaffNumber = newUser.getMisStaff().getMisStaffNumber();
        String email = newUser.getMisStaff().getEmail();

        if (userRepository.existsByUserId(misStaffNumber)) {
            throw new PersonExistsException("MIS Staff already exists!");
        }

        MisStaff misStaff = newUser.getMisStaff();
        misStaffRepository.save(misStaff);

        emailService.sendNewPasswordEmail(email, otp);
        user.setUserId(misStaffNumber);
        user.setOtp(otp);
        user.setLocked(true);
        user.setRole(ROLE_MISSTAFF.name());
        user.setAuthorities(Arrays.asList(ROLE_MISSTAFF.getAuthorities()));
    }

    @Override
    public boolean verifyOtp(String username, String otp) {
        User user = userRepository.findUserByUsername(username);
        if (user != null && user.getOtp().equals(otp)) {
            user.setLocked(false);
            user.setOtp(null);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public User forgotPassword(User newUser) throws UsernameNotFoundException, MessagingException {
        String username = newUser.getUsername();
        boolean isUsernameExist = userRepository.existsUserByUsername(username);
        if(isUsernameExist){
            User user = userRepository.findUserByUsername(username);
            String otp = generateOTP();
            user.setOtp(otp);
            String userNumber = user.getUserId();
            Students studentNumber = studentRepository.findByStudentNumber(userNumber);
            Employees employeeNumber = employeeRepository.findByEmployeeNumber(userNumber);
            MisStaff staffNumber = misStaffRepository.findByMisStaffNumber(userNumber);
            if(studentNumber != null){
                emailService.sendNewPasswordEmail(studentNumber.getEmail(),otp);
            } else if(employeeNumber != null){
                emailService.sendNewPasswordEmail(employeeNumber.getEmail(),otp);
            } else if (staffNumber != null) {
            emailService.sendNewPasswordEmail(staffNumber.getEmail(), otp);
        }
            userRepository.save(user);
            LOGGER.info("Username Found!");
        } else {
            throw new UsernameNotFoundException("Username Not Found!");
        }
        return newUser;
    }
    @Override
    public User verifyOtpForgotPassword(User newUser) throws UsernameNotFoundException, PersonExistsException, OtpExistsException {
        validatePassword(newUser.getPassword());
        String username = newUser.getUsername();
        String newPassword = passwordEncoder.encode(newUser.getPassword());
        String otp = newUser.getOtp();
        User user = userRepository.findUserByUsername(username);
        if(user.getOtp().equals(otp)){
            user.setPassword(newPassword);
            user.setOtp(null);
        } else {
            throw new OtpExistsException("Incorrect OTP code!");
        }
        return newUser;
    }

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
    private void validateNewUsername(String newUsername) throws UsernameExistsException {
        User userByNewUsername = findUserByUsername(newUsername);
        if (userByNewUsername != null) {
            throw new UsernameExistsException("Username already exists.");
        }
    }

    private void validatePassword(String password) throws PersonExistsException {
        if (password == null || password.isEmpty()) {
            throw new PersonExistsException("Password cannot be null or empty.");
        }

        String passwordPattern = ".*[^a-zA-Z0-9].*";
        if (!password.matches(passwordPattern)) {
            throw new PersonExistsException("Please create a stronger password. Password should contain special characters.");
        }
    }

    private String generateOTP() {
        return RandomStringUtils.randomNumeric(5); // Generates a 5-digit numeric OTP
    }
    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findUserByUsername(username);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}

