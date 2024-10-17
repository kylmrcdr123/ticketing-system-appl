package com.rocs.ticketing.system.service.user.impl;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.register.Register;
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
    public Register register(Register register) throws UsernameNotFoundException, UsernameExistsException, MessagingException, PersonExistsException, UserNotFoundException {
        validateNewUsername(register.getUser().getUsername());  // Check if username already exists
        validatePassword(register.getUser().getPassword());     // Validate password strength
        String otp = generateOTP();  // Generate a One Time Password (OTP)
        User user = new User();
        user.setUsername(register.getUser().getUsername());
        user.setPassword(passwordEncoder.encode(register.getUser().getPassword()));
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setLocked(false);  // User should not be locked immediately upon registration

        // Student Registration
        if (register.getStudents() != null && register.getStudents().getStudentNumber() != null) {
            handleStudentRegistration(register, user, otp);
        }
        // Employee Registration
        else if (register.getEmployees() != null && register.getEmployees().getEmployeeNumber() != null) {
            handleEmployeeRegistration(register, user, otp);
        }
        // MIS Staff Registration
        else if (register.getMisStaff() != null && register.getMisStaff().getMisStaffNumber() != null) {
            handleMisStaffRegistration(register, user, otp);
        } else {
            throw new PersonExistsException("Invalid data. Ensure all necessary fields are filled.");
        }

        userRepository.save(user);  // Save the new user to the repository
        LOGGER.info("User registered successfully!");
        return register;
    }

    private void handleStudentRegistration(Register register, User user, String otp) throws MessagingException {
        String studentNumber = register.getStudents().getStudentNumber();
        String email = register.getStudents().getEmail();

        // Check if a student with the same student number already exists
        if (studentRepository.existsByStudentNumber(studentNumber)) {
            throw new PersonExistsException("Student Already Exists!");
        }

        Students student = register.getStudents();
        student.setUser(user);  // Link the student to the user
        studentRepository.save(student);

        emailService.sendNewPasswordEmail(email, otp);

        user.setOtp(otp);
        user.setRole(ROLE_STUDENT.name());
        user.setAuthorities(Arrays.stream(ROLE_STUDENT.getAuthorities()).toList());
    }

    private void handleEmployeeRegistration(Register register, User user, String otp) throws MessagingException {
        String employeeNumber = register.getEmployees().getEmployeeNumber();
        String email = register.getEmployees().getEmail();

        // Check if an employee with the same employee number already exists
        if (employeeRepository.existsByEmployeeNumber(employeeNumber)) {
            throw new PersonExistsException("Employee Already Exists!");
        }

        Employees employee = register.getEmployees();
        employee.setUser(user);  // Link the employee to the user
        employeeRepository.save(employee);

        emailService.sendNewPasswordEmail(email, otp);

        user.setOtp(otp);
        user.setRole(ROLE_EMPLOYEE.name());
        user.setAuthorities(Arrays.stream(ROLE_EMPLOYEE.getAuthorities()).toList());
    }

    private void handleMisStaffRegistration(Register register, User user, String otp) throws MessagingException {
        String misStaffNumber = register.getMisStaff().getMisStaffNumber();
        String email = register.getMisStaff().getEmail();

        // Check if MIS Staff with the same MIS Staff number already exists
        if (misStaffRepository.existsByMisStaffNumber(misStaffNumber)) {
            throw new PersonExistsException("MIS Staff Already Exists!");
        }

        MisStaff misStaff = register.getMisStaff();
        misStaff.setUser(user);  // Link the MIS Staff to the user
        misStaffRepository.save(misStaff);

        emailService.sendNewPasswordEmail(email, otp);

        user.setOtp(otp);
        user.setRole(ROLE_MISSTAFF.name());
        user.setAuthorities(Arrays.stream(ROLE_MISSTAFF.getAuthorities()).toList());
    }


    @Override
    public User forgotPassword(User newUser) throws UsernameNotFoundException, MessagingException {
        String username = newUser.getUsername();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username Not Found!");
        }

        String otp = generateOTP();
        user.setOtp(otp);

        Students studentAccount = studentRepository.findByUser_Id(user.getId());
        Employees employeeAccount = employeeRepository.findByUser_Id(user.getId());
        MisStaff misStaffAccount = misStaffRepository.findByUser_Id(user.getId());

        if(studentAccount != null && studentAccount.getEmail() != null) {
            emailService.sendNewPasswordEmail(studentAccount.getEmail(), otp);
        } else if(employeeAccount != null && employeeAccount.getEmail() != null) {
            emailService.sendNewPasswordEmail(employeeAccount.getEmail(), otp);
        } else if(misStaffAccount != null && misStaffAccount.getEmail() != null) {
            emailService.sendNewPasswordEmail(misStaffAccount.getEmail(), otp);
        } else {
            throw new MessagingException("No email associated with this user for password reset.");
        }
        userRepository.save(user);
        return user;
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
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
    private void validateNewUsername(String newUsername)
            throws UserNotFoundException, UsernameExistsException, PersonExistsException {
        User userByNewUsername = findUserByUsername(newUsername);
        if (StringUtils.isNotBlank(StringUtils.EMPTY)) {
            User currentUser = findUserByUsername(StringUtils.EMPTY);
            if (currentUser == null) {
                throw new UserNotFoundException("User not found.");
            }
            if (userByNewUsername != null && !userByNewUsername.getId().equals(currentUser.getId())) {
                throw new PersonExistsException("Username already exists.");
            }
        } else {
            if (userByNewUsername != null) {
                throw new PersonExistsException("Username already exists.");
            }
        }
    }
    private void validatePassword(String password) throws PersonExistsException {
        String passwordPattern = ".*[^a-zA-Z0-9].*";
        if (!password.matches(passwordPattern)) {
            throw new PersonExistsException("Please create a stronger password. Password should contain special characters.");
        }
    }
    private String generateOTP() {
        return RandomStringUtils.randomAlphanumeric(10);
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

