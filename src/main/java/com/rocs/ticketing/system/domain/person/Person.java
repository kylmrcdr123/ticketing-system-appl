package com.rocs.ticketing.system.domain.person;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.util.Date;

@MappedSuperclass
@Data
public class Person {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(length = 32)
    private String lastName;

    @Column(length = 64)
    private String firstName;

    @Column(length = 32)
    private String middleName;
    private Date birthdate;
    @Column(length = 64)
    private String email;

    @Column(length = 128)
    private String address;

    @Column(length = 11)
    private String contactNumber;

    private Date dateCreated;
}
