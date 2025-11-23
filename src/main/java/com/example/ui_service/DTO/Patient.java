package com.example.ui_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String contactNumber;
    private String emailId;

    // Getters and Setters
}
