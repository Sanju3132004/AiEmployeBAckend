package com.aiassistant.controller;

import com.aiassistant.entity.Employee;
import com.aiassistant.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminController {

    private final EmployeeService employeeService;
    
    public AdminController(EmployeeService service) {
    	this.employeeService = service;
    }

    /**
     * Lists all employees. Restricted to ADMIN / HR / PROJECT_MANAGER (see SecurityConfig).
     * Used by the frontend to populate the employee picker on the payroll generation form.
     */
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> listEmployees() {
        return ResponseEntity.ok(employeeService.getAll());
    }
}
