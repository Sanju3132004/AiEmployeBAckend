package com.aiassistant.controller;

import com.aiassistant.entity.Employee;
import com.aiassistant.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    
    public EmployeeController(EmployeeService service) {
    	this.employeeService = service;
    }

    @GetMapping("/me")
    public ResponseEntity<Employee> getCurrentEmployee(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(employeeService.getByEmail(userDetails.getUsername()));
    }
}
