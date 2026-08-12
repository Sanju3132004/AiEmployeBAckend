package com.aiassistant.controller;

import com.aiassistant.entity.LeaveRequest;
import com.aiassistant.service.EmployeeService;
import com.aiassistant.service.LeaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@Tag(name = "Leave Management")
public class LeaveController {

    public LeaveController(LeaveService leaveService, EmployeeService employeeService) {
		super();
		this.leaveService = leaveService;
		this.employeeService = employeeService;
	}

	private final LeaveService leaveService;
    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<LeaveRequest> applyLeave(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody LeaveRequest request) {
        var employee = employeeService.getByEmail(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.applyLeave(employee, request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<LeaveRequest>> myLeaves(@AuthenticationPrincipal UserDetails userDetails) {
        var employee = employeeService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(leaveService.getLeavesForEmployee(employee.getId()));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<LeaveRequest>> allLeaves() {
        return ResponseEntity.ok(leaveService.getAll());
    }

    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<LeaveRequest> updateStatus(@PathVariable Long id,
                                                       @RequestParam LeaveRequest.LeaveStatus status) {
        return ResponseEntity.ok(leaveService.updateStatus(id, status));
    }
}
