package com.aiassistant.controller;

import com.aiassistant.dto.*;
import com.aiassistant.entity.Employee;
import com.aiassistant.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final EmployeeService employeeService;
    
    public AuthController(EmployeeService service) {
    	this.employeeService = service;
    }

    /**
     * Register a new employee or project manager account.
     * Restricted to ADMIN / PROJECT_MANAGER (see SecurityConfig) - only the
     * owner or a project manager can create accounts for others.
     */
    @PostMapping("/register")
    public ResponseEntity<Employee> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.register(request));
    }

    /**
     * One-time bootstrap to create the very first owner (ADMIN) account.
     * Public endpoint, but requires app.owner-setup-key to match.
     */
    @PostMapping("/register-owner")
    public ResponseEntity<Employee> registerOwner(@Valid @RequestBody OwnerRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.registerOwner(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(employeeService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        employeeService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("message",
                "If an account exists for that email, a reset code has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        employeeService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
    }
}
