package com.example.ui_service.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.example.ui_service.DTO.Doctor;
import com.example.ui_service.service.config.FeignClientConfig;

@FeignClient(name = "doctor-service", url = "http://localhost:8082", configuration = FeignClientConfig.class)
public interface DoctorClient {

    @GetMapping("/doctors")
    List<Doctor> getAllDoctors();

    @PostMapping("/doctors")
    Doctor addDoctor(@RequestBody Doctor doctor);

    // Update doctor by ID
    @PutMapping("/doctors/{id}")
    Doctor updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor);

    // Delete doctor by ID
    @DeleteMapping("/doctors/{id}")
    void deleteDoctor(@PathVariable Long id);

    // Optional: Get doctor by ID
    @GetMapping("/doctors/{id}")
    Doctor getDoctorById(@PathVariable Long id);
}
