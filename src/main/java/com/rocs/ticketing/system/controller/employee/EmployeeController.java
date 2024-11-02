package com.rocs.ticketing.system.controller.employee;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.service.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/EmployeeService")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employees>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @PostMapping("/employee/add")
    public ResponseEntity<String> addEmployee(@RequestBody Employees employee) {
        try {
            employeeService.addEmployee(employee);
            return new ResponseEntity<>("Employee successfully added", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Employee cannot be added: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/employee/update/{userId}")
    public ResponseEntity<String> updateEmployee(@PathVariable("employeeId") String userId, @RequestBody Employees employee) {
        try {
            Employees updatedEmployee = employeeService.updateEmployee(userId, employee);
            if (updatedEmployee != null) {
                return new ResponseEntity<>("Employee successfully updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/employee/{userId}")
    public ResponseEntity<Employees> getEmployeeByUserId(@PathVariable("userId") String userId) {
        Employees employee = employeeService.getEmployeeByUserId(userId);
        if (employee != null) {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
