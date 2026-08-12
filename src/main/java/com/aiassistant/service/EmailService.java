package com.aiassistant.service;

import com.aiassistant.entity.LeaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender sender) {
    	this.mailSender = sender;
    }

    public void sendLeaveNotification(String toEmail, LeaveRequest leaveRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Leave Request Submitted");
            message.setText("Your leave request from " + leaveRequest.getStartDate() +
                    " to " + leaveRequest.getEndDate() + " has been submitted and is pending approval.");
            mailSender.send(message);
        } catch (Exception e) {
            // Log and continue - email failure should not block the leave application
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String toEmail, String fullName, String rawPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your Employee Account Has Been Created");
            message.setText(
                "Hi " + fullName + ",\n\n" +
                "An account has been created for you on the AI Employee Support Assistant.\n\n" +
                "Login Email: " + toEmail + "\n" +
                "Temporary Password: " + rawPassword + "\n\n" +
                "Please log in and change your password as soon as possible.\n\n" +
                "If you did not expect this account, please contact your admin/HR."
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText(
                "We received a request to reset your password.\n\n" +
                "Your password reset code is: " + resetToken + "\n\n" +
                "This code will expire in 15 minutes. Enter it in the app's Reset Password page " +
                "along with your new password.\n\n" +
                "If you did not request this, you can safely ignore this email."
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }
}
