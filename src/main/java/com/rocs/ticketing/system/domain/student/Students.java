package com.rocs.ticketing.system.domain.student;

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
public class Students extends Person  implements Serializable {

    @Column(length = 12, nullable = false)
    private String studentNumber;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

}
