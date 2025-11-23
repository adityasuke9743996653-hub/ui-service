package com.example.ui_service.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ui_service.DTO.Appointment;
import com.example.ui_service.service.config.FeignClientConfig;


@FeignClient(name = "appointment-service", url = "http://localhost:8083", configuration = FeignClientConfig.class)
public interface AppointmentClient {

    @GetMapping("/appointments")
    List<Appointment> getAllAppointments();

    @PostMapping("/appointments")
    Appointment addAppointment(@RequestBody Appointment appointment);

    // Update appointment by ID
    @PutMapping("/appointments/{id}")
    Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment);

    // Delete appointment by ID
    @DeleteMapping("/appointments/{id}")
    void deleteAppointment(@PathVariable Long id);

    // Optional: Get appointment by ID
    @GetMapping("/appointments/{id}")
    Appointment getAppointmentById(@PathVariable Long id);
    
    @GetMapping("/appointments/{id}/excel")  // <-- include /appointments
    ResponseEntity<byte[]> downloadExcel(@PathVariable("id") Long id);

    @GetMapping("/appointments/{id}/pdf")  // for PDF
    ResponseEntity<byte[]> downloadPdf(@PathVariable("id") Long id);
}
