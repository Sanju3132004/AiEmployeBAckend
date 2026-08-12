package com.aiassistant.service;

import com.aiassistant.entity.Attendance;
import com.aiassistant.entity.Employee;
import com.aiassistant.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    
    public AttendanceService(AttendanceRepository repo) {
    	this.attendanceRepository = repo;
    }

    public Attendance checkIn(Employee employee) {

        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(employee.getId(), today)
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setEmployee(employee);
                    a.setDate(today);
                    a.setStatus("PRESENT");
                    return a;
                });

        attendance.setCheckIn(LocalTime.now());

        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Employee employee) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today)
                .orElseThrow(() -> new IllegalArgumentException("No check-in found for today"));
        attendance.setCheckOut(LocalTime.now());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getHistory(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }
}