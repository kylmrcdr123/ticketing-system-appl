package com.rocs.ticketing.system.domain.user;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.utils.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "login")
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String userId;
    private String username;
    private String password;
    private Date lastLoginDate;
    private Date joinDate;
    private String role;
    private String otp;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Students student;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Employees employee;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MisStaff misStaff;

    @Column(name = "authorities", nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> authorities = new ArrayList<>();

    private boolean isActive;
    private boolean isLocked;

}
