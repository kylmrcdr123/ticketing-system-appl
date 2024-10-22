package com.rocs.ticketing.system.domain.misStaff;

import com.rocs.ticketing.system.domain.person.Person;
import com.rocs.ticketing.system.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Entity
@Data
public class MisStaff extends Person implements Serializable  {

    private Long staffId;
    @Column(length = 10)
    private String misStaffNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
