package com.project.back_end.services;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    /** Create a new patient */
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Fetch all appointments for a patient */
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.getEmailFromToken(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null || !patient.getId().equals(id)) {
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }

            List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(id).stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            response.put("appointments", appointments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error retrieving appointments");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Filter appointments by past or future condition */
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> filtered = appointmentRepository.findByPatientId(id).stream()
                    .map(AppointmentDTO::new)
                    .filter(a -> {
                        if (condition.equalsIgnoreCase("past")) {
                            return a.getAppointmentTime().isBefore(LocalDateTime.now());
                        } else if (condition.equalsIgnoreCase("future")) {
                            return a.getAppointmentTime().isAfter(LocalDateTime.now());
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            response.put("appointments", filtered);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Filter appointments by doctor's name */
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> filtered = appointmentRepository
                    .filterByDoctorNameAndPatientId(name, patientId)
                    .stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            response.put("appointments", filtered);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering by doctor");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Filter appointments by doctor and condition (past/future) */
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> filtered = appointmentRepository
                    .filterByDoctorNameAndPatientId(name, patientId)
                    .stream()
                    .map(AppointmentDTO::new)
                    .filter(a -> {
                        if (condition.equalsIgnoreCase("past")) {
                            return a.getAppointmentTime().isBefore(LocalDateTime.now());
                        } else if (condition.equalsIgnoreCase("future")) {
                            return a.getAppointmentTime().isAfter(LocalDateTime.now());
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            response.put("appointments", filtered);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering by doctor and condition");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Get patient details from token */
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.getEmailFromToken(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(404).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error retrieving patient details");
            return ResponseEntity.status(500).body(response);
        }
    }
}
