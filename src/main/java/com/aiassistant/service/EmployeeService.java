package com.aiassistant.service;

import com.aiassistant.dto.*;
import com.aiassistant.entity.Employee;
import com.aiassistant.entity.Role;
import com.aiassistant.repository.EmployeeRepository;
import com.aiassistant.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Value("${app.owner-setup-key:}")
    private String ownerSetupKey;

    public Employee register(RegisterRequest request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        Role assignedRole = Role.EMPLOYEE;

        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                Role requested = Role.valueOf(request.getRole().toUpperCase());

                boolean callerIsAdmin =
                        SecurityContextHolder.getContext().getAuthentication() != null
                                && SecurityContextHolder.getContext().getAuthentication()
                                .getAuthorities()
                                .stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (requested != Role.EMPLOYEE && !callerIsAdmin) {
                    throw new IllegalArgumentException("Only an ADMIN can assign that role");
                }

                assignedRole = requested;

            } catch (IllegalArgumentException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("ADMIN")) {
                    throw ex;
                }
            }
        }

        Employee employee = new Employee();
        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setDateOfJoining(LocalDate.now());
        employee.setRole(assignedRole);
        employee.setActive(true);

        Employee saved = employeeRepository.save(employee);

        // Notify the new employee with their login email + password
        emailService.sendWelcomeEmail(
                saved.getEmail(),
                saved.getFullName(),
                request.getPassword());

        return saved;
    }

    public Employee registerOwner(OwnerRegisterRequest request) {

        if (ownerSetupKey == null || ownerSetupKey.isBlank()) {
            throw new IllegalStateException("Owner setup is not configured on this server");
        }

        if (!ownerSetupKey.equals(request.getSetupKey())) {
            throw new IllegalArgumentException("Invalid setup key");
        }

        if (employeeRepository.countByRole(Role.ADMIN) > 0) {
            throw new IllegalStateException("An owner account already exists.");
        }

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Employee owner = new Employee();

        owner.setFullName(request.getFullName());
        owner.setEmail(request.getEmail());
        owner.setPassword(passwordEncoder.encode(request.getPassword()));
        owner.setDepartment("Management");
        owner.setDesignation("Owner");
        owner.setDateOfJoining(LocalDate.now());
        owner.setRole(Role.ADMIN);
        owner.setActive(true);

        return employeeRepository.save(owner);
    }

    public AuthResponse login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found"));

        String token = jwtUtil.generateToken(
                employee.getEmail(),
                employee.getRole().name());

        return new AuthResponse(
                token,
                employee.getEmail(),
                employee.getRole().name());
    }

    public Employee getByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found"));
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public void forgotPassword(String email) {

        employeeRepository.findByEmail(email).ifPresent(employee -> {

            String token = UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

            employee.setResetToken(token);
            employee.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

            employeeRepository.save(employee);

            emailService.sendPasswordResetEmail(
                    employee.getEmail(),
                    token);
        });
    }

    public void resetPassword(String token, String newPassword) {

        Employee employee = employeeRepository.findByResetToken(token)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid or expired reset code"));

        if (employee.getResetTokenExpiry() == null
                || employee.getResetTokenExpiry().isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "This reset code has expired.");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setResetToken(null);
        employee.setResetTokenExpiry(null);

        employeeRepository.save(employee);
    }
}