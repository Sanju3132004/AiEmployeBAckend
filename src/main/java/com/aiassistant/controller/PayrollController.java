package com.aiassistant.controller;

import com.aiassistant.entity.Payroll;
import com.aiassistant.service.EmployeeService;
import com.aiassistant.service.PayrollService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@Tag(name = "Payroll")
public class PayrollController {

    public PayrollController(PayrollService payrollService, EmployeeService employeeService) {
		super();
		this.payrollService = payrollService;
		this.employeeService = employeeService;
	}

	private final PayrollService payrollService;
    private final EmployeeService employeeService;

    @GetMapping("/me")
    public ResponseEntity<List<Payroll>> myPayslips(@AuthenticationPrincipal UserDetails userDetails) {
        var employee = employeeService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(payrollService.getPayslips(employee.getId()));
    }

    @PostMapping("/admin/generate")
    public ResponseEntity<Payroll> generate(@RequestBody Payroll payroll) {
        return ResponseEntity.ok(payrollService.generatePayslip(payroll));
    }
}
