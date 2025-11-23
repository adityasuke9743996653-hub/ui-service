package com.example.ui_service.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.ui_service.DTO.Appointment;
import com.example.ui_service.DTO.Doctor;
import com.example.ui_service.DTO.Patient;
import com.example.ui_service.feignClient.AppointmentClient;
import com.example.ui_service.feignClient.DoctorClient;
import com.example.ui_service.feignClient.PatientClient;
import com.example.ui_service.service.DoctorEmailNotification;
import com.example.ui_service.service.EmailService;
import com.example.ui_service.service.PatientEmailNotification;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UiController {

    @Autowired 
    private PatientClient patientClient;

    @Autowired
    private DoctorClient doctorClient;

    @Autowired
    private AppointmentClient appointmentClient;

    @Autowired
    private DoctorEmailNotification doctorService;

    @Autowired
    private PatientEmailNotification patientService;

    @Autowired
    private EmailService emailService;

    // ===================== DASHBOARD =====================
    @GetMapping("/")
    public String viewDashboard() {
        return "dashboard";
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String redirectUri = "http://localhost:8084/"; // UI homepage after logout
        String keycloakLogoutUrl = "http://localhost:8080/realms/myrealm/protocol/openid-connect/logout?redirect_uri=" + redirectUri;
        response.sendRedirect(keycloakLogoutUrl);
    }

    // ===================== PATIENT =====================
    @GetMapping("/patients")
    public String viewPatients(Model model) {
        List<Patient> patients = patientClient.getAllPatients();
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/addPatient")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "addPatient";
    }

    @PostMapping("/addPatient")
    public String addPatient(@ModelAttribute Patient patient) {
        patientClient.addPatient(patient);
        return "redirect:/patients";
    }

    @GetMapping("/editPatient/{id}")
    public String editPatient(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientClient.getPatientById(id));
        return "editPatient";
    }

    @PostMapping("/editPatient/{id}")
    public String editPatient(@PathVariable Long id, @ModelAttribute Patient patient) {
        patientClient.updatePatient(id, patient);
        return "redirect:/patients";
    }

    @GetMapping("/deletePatient/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientClient.deletePatient(id);
        return "redirect:/patients";
    }

    // ===================== DOCTOR =====================
    @GetMapping("/doctors")
    public String viewDoctors(Model model) {
        model.addAttribute("doctors", doctorClient.getAllDoctors());
        return "doctors";
    }

    @GetMapping("/addDoctor")
    public String addDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "addDoctor";
    }

    @PostMapping("/addDoctor")
    public String addDoctor(@ModelAttribute Doctor doctor) {
        doctorClient.addDoctor(doctor);
        return "redirect:/doctors";
    }

    @GetMapping("/editDoctor/{id}")
    public String editDoctor(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorClient.getDoctorById(id));
        return "editDoctor";
    }

    @PostMapping("/editDoctor/{id}")
    public String editDoctor(@PathVariable Long id, @ModelAttribute Doctor doctor) {
        doctorClient.updateDoctor(id, doctor);
        return "redirect:/doctors";
    }

    @GetMapping("/deleteDoctor/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorClient.deleteDoctor(id);
        return "redirect:/doctors";
    }

    // ===================== APPOINTMENT =====================
    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        model.addAttribute("appointments", appointmentClient.getAllAppointments());
        return "appointments";
    }

    @GetMapping("/addAppointment")
    public String addAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("patients", patientClient.getAllPatients());
        model.addAttribute("doctors", doctorClient.getAllDoctors());
        return "addAppointment";
    }

    @PostMapping("/addAppointment")
    public String addAppointment(@ModelAttribute Appointment appointment) {
        populateAppointmentDetails(appointment);
        appointmentClient.addAppointment(appointment);

        doctorService.sendAppointmentNotification(appointment, "booked");
        patientService.sendAppointmentNotification(appointment, "booked");

        return "redirect:/appointments";
    }

    @GetMapping("/editAppointment/{id}")
    public String editAppointment(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentClient.getAppointmentById(id));
        model.addAttribute("patients", patientClient.getAllPatients());
        model.addAttribute("doctors", doctorClient.getAllDoctors());
        return "editAppointment";
    }

    @PostMapping("/editAppointment/{id}")
    public String editAppointment(@PathVariable Long id, @ModelAttribute Appointment appointment) {
        populateAppointmentDetails(appointment);
        appointmentClient.updateAppointment(id, appointment);

        doctorService.sendAppointmentNotification(appointment, "updated");
        patientService.sendAppointmentNotification(appointment, "updated");

        return "redirect:/appointments";
    }

    @GetMapping("/deleteAppointment/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        appointmentClient.deleteAppointment(id);
        return "redirect:/appointments";
    }

    // ===================== EXCEL / PDF / EMAIL =====================
    @GetMapping("/ui/appointments/{id}/excel")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable Long id) {
        var response = appointmentClient.downloadExcel(id);
        return ResponseEntity.ok()
                .headers(createDownloadHeaders("appointment_" + id + ".xlsx"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.getBody());
    }

    @GetMapping("/ui/appointments/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        var response = appointmentClient.downloadPdf(id);
        return ResponseEntity.ok()
                .headers(createDownloadHeaders("appointment_" + id + ".pdf"))
                .contentType(MediaType.APPLICATION_PDF)
                .body(response.getBody());
    }

    @GetMapping("/ui/appointments/{id}/send-email")
    public ResponseEntity<String> sendAppointmentEmail(@PathVariable Long id,
                                                       @RequestParam String email,
                                                       @RequestParam String type) throws Exception {

        byte[] fileBytes;
        String filename;
        String contentType;

        if ("excel".equalsIgnoreCase(type)) {
            var response = appointmentClient.downloadExcel(id);
            fileBytes = response.getBody();
            filename = "appointment_" + id + ".xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if ("pdf".equalsIgnoreCase(type)) {
            var response = appointmentClient.downloadPdf(id);
            fileBytes = response.getBody();
            filename = "appointment_" + id + ".pdf";
            contentType = "application/pdf";
        } else {
            return ResponseEntity.badRequest().body("Invalid type: use 'pdf' or 'excel'");
        }

        emailService.sendMailWithAttachment(email,
                "Your Appointment " + type.toUpperCase(),
                "Please find your appointment " + type + " attached.",
                fileBytes,
                filename,
                contentType);

        return ResponseEntity.ok("Email sent successfully to " + email);
    }

    // ===================== HELPERS =====================
    private void populateAppointmentDetails(Appointment appointment) {
        Patient patient = patientClient.getPatientById(appointment.getPatientId());
        if (patient != null) {
            appointment.setPatientName(patient.getName());
            appointment.setPatientContactNumber(patient.getContactNumber());
            appointment.setPatientEmailId(patient.getEmailId());
        }

        Doctor doctor = doctorClient.getDoctorById(appointment.getDoctorId());
        if (doctor != null) {
            appointment.setDoctorName(doctor.getName());
            appointment.setDoctorSpecialization(doctor.getSpecialization());
            appointment.setDoctorContactNumber(doctor.getContactNumber());
            appointment.setDoctorEmailId(doctor.getEmailId());
        }
    }

    private HttpHeaders createDownloadHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        return headers;
    }
}
