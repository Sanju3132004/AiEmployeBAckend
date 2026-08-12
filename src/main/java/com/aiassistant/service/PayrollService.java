package com.aiassistant.service;

import com.aiassistant.entity.Payroll;
import com.aiassistant.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollService {

    public PayrollService(PayrollRepository payrollRepository) {
		super();
		this.payrollRepository = payrollRepository;
	}

	private final PayrollRepository payrollRepository;

    public List<Payroll> getPayslips(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }

    public Payroll generatePayslip(Payroll payroll) {
        var net = payroll.getBasicSalary()
                .add(payroll.getAllowances() != null ? payroll.getAllowances() : java.math.BigDecimal.ZERO)
                .subtract(payroll.getDeductions() != null ? payroll.getDeductions() : java.math.BigDecimal.ZERO);
        payroll.setNetSalary(net);
        return payrollRepository.save(payroll);
    }
}
