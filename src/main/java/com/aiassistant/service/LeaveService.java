package com.aiassistant.service;

import com.aiassistant.entity.Employee;
import com.aiassistant.entity.LeaveRequest;
import com.aiassistant.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    public LeaveService(LeaveRequestRepository leaveRequestRepository, EmailService emailService) {
		super();
		this.leaveRequestRepository = leaveRequestRepository;
		this.emailService = emailService;
	}

	private final LeaveRequestRepository leaveRequestRepository;
    private final EmailService emailService;

    public LeaveRequest applyLeave(Employee employee, LeaveRequest request) {
        request.setEmployee(employee);
        request.setStatus(LeaveRequest.LeaveStatus.PENDING);
        LeaveRequest saved = leaveRequestRepository.save(request);

        emailService.sendLeaveNotification(employee.getEmail(), saved);
        return saved;
    }

    public List<LeaveRequest> getLeavesForEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public LeaveRequest updateStatus(Long leaveId, LeaveRequest.LeaveStatus status) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        leave.setStatus(status);
        return leaveRequestRepository.save(leave);
    }

    public List<LeaveRequest> getAll() {
        return leaveRequestRepository.findAll();
    }
}
