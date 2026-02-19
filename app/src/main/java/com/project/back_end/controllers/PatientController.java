package com.project.back_end.controllers;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    /**
     * Get patient details based on JWT token
     */
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable String token) {
        var validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }
        return patientService.getPatientDetails(token);
    }

    /**
     * Create a new patient
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        boolean valid = service.validatePatient(patient);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Patient with email id or phone no already exist"));
        }
        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Signup successful"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    /**
     * Patient login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginPatient(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    /**
     * Get all appointments for a patient
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        var validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }
        return patientService.getPatientAppointment(id, token);
    }

    /**
     * Filter patient appointments by condition and doctor
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {
        var validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }
        return service.filterPatient(condition, name, token);
    }
}
