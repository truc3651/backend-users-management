package com.backend.users.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${app.base-url}")
  private String baseUrl;

  public void sendPasswordResetEmail(String toEmail, String token) {
    String resetLink = baseUrl + "/v1/api/auth/reset-password?token=" + token;

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromEmail);
    message.setTo(toEmail);
    message.setSubject("Password Reset Request");
    message.setText(
        "Hello,\n\n"
            + "You have requested to reset your password. Please click the link below to reset your"
            + " password:\n\n"
            + resetLink
            + "\n\n"
            + "This link will expire in 1 hour.\n\n"
            + "If you did not request this password reset, please ignore this email.\n\n"
            + "Best regards,\n"
            + "The Team");

    try {
      mailSender.send(message);
      log.info("Password reset email sent to: {}", toEmail);
    } catch (Exception e) {
      log.error("Failed to send password reset email to: {}", toEmail, e);
      throw new RuntimeException("Failed to send email", e);
    }
  }
}
