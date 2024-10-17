package com.rocs.ticketing.system.domain.employees;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rocs.ticketing.system.domain.person.Person;
import com.rocs.ticketing.system.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Employees extends Person implements Serializable {

    @Column(length = 32, nullable = false)
    private String employeeNumber;


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

   }
