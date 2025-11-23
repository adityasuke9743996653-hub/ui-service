package com.example.ui_service.DTO;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;   
   
    private String patientName;
   
    private String patientContactNumber;
    private String patientEmailId;
    private Long doctorId;    
    private String doctorName;
    private String doctorSpecialization;
    private String doctorContactNumber;
    private String doctorEmailId;

    private LocalDateTime appointmentDateTime;

    //private String status; // e.g., SCHEDULED, COMPLETED, CANCELLED
}

