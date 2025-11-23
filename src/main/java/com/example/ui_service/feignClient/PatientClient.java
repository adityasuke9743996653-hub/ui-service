package com.example.ui_service.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.example.ui_service.DTO.Patient;
import com.example.ui_service.service.config.FeignClientConfig;

import java.util.List;

@FeignClient(name = "patient-service", url = "http://localhost:8081", configuration = FeignClientConfig.class)
public interface PatientClient {

    @GetMapping("/patients")
    List<Patient> getAllPatients();

    @PostMapping("/patients")
    Patient addPatient(@RequestBody Patient patient);

    // Update patient by ID
    @PutMapping("/patients/{id}")
    Patient updatePatient(@PathVariable Long id, @RequestBody Patient patient);

    // Delete patient by ID
    @DeleteMapping("/patients/{id}")
    void deletePatient(@PathVariable Long id);

    // Optional: Get patient by ID
    @GetMapping("/patients/{id}")
    Patient getPatientById(@PathVariable Long id);
}




