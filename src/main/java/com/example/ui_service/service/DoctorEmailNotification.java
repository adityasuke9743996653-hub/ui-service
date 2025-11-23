package com.example.ui_service.service;

import com.example.ui_service.DTO.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class DoctorEmailNotification {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAppointmentNotification(Appointment appointment, String type) {
        if (appointment.getPatientEmailId() == null) return; // no email

        String subject = "Appointment " + type;
        String message = "Hello " + appointment.getDoctorName() + ",\n\n" +
                         "Your appointment is " + type + " with Dr. " + appointment.getDoctorName() + 
                         " (" + appointment.getDoctorSpecialization() + ") on " + 
                         appointment.getAppointmentDateTime() + ".\n\n" +
                         "Thank you,\nHealthcare Management System";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(appointment.getDoctorEmailId());
        mail.setSubject(subject);
        mail.setText(message);

        mailSender.send(mail);
    }
}
