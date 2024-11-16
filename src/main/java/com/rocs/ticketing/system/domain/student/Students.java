package com.rocs.ticketing.system.domain.student;

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
public class Students extends Person  implements Serializable {

    private Long studentId;
    @Column(length = 11)
    private String studentNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}
