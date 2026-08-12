package com.aiassistant.controller;

import com.aiassistant.entity.Attendance;
import com.aiassistant.service.AttendanceService;
import com.aiassistant.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    public AttendanceController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<Attendance> checkIn(@AuthenticationPrincipal UserDetails userDetails) {
        var employee = employeeService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(attendanceService.checkIn(employee));
    }

    @PostMapping("/check-out")
    public ResponseEntity<Attendance> checkOut(@AuthenticationPrincipal UserDetails userDetails) {
        var employee = employeeService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(attendanceService.checkOut(employee));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Attendance>> history(@AuthenticationPrincipal UserDetails userDetails) {
        var employee = employeeService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(attendanceService.getHistory(employee.getId()));
    }
}