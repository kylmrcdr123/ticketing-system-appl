package com.rocs.ticketing.system.domain.register;


import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.domain.user.User;
import lombok.Data;

@Data
public class Register {
    private User user;
    private Employees employees;
    private MisStaff misStaff;
    private Students students;
}
