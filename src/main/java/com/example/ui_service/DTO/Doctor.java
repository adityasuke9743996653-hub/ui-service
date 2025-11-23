package com.example.ui_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    private Long id;
    private String name;
    private String specialization;
    private String contactNumber;
    private String emailId;
}

