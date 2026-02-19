package com.project.back_end.services;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService, AdminRepository adminRepository,
                   DoctorRepository doctorRepository, PatientRepository patientRepository,
                   DoctorService doctorService, PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /** Validate token for a given user role */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Unauthorized or expired token");
            return ResponseEntity.status(401).body(response);
        }
        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    /** Validate admin login */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
        if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
            String token = tokenService.generateToken(admin.getUsername(), "admin");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        response.put("message", "Invalid username or password");
        return ResponseEntity.status(401).body(response);
    }

    /** Filter doctors by name, specialty, and time */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
    }

    /** Validate appointment availability */
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
        if (doctorOpt.isEmpty()) return -1; // Doctor not found

        List<String> availableSlots = doctorService.getDoctorAvailability(appointment.getDoctorId(),
                appointment.getAppointmentTime().toLocalDate());
        return availableSlots.contains(appointment.getAppointmentTime().toLocalTime().toString()) ? 1 : 0;
    }

    /** Validate whether a patient exists by email or phone */
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    /** Validate patient login credentials */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getIdentifier());
        if (patient != null && patient.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(patient.getEmail(), "patient");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        response.put("message", "Invalid email or password");
        return ResponseEntity.status(401).body(response);
    }

    /** Filter patient appointments by condition and doctor name */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Patient not found");
            return ResponseEntity.status(404).body(resp);
        }

        Map<String, Object> result;
        if (condition != null && name != null) {
            result = patientService.filterByDoctorAndCondition(condition, name, patient.getId());
        } else if (condition != null) {
            result = patientService.filterByCondition(condition, patient.getId());
        } else if (name != null) {
            result = patientService.filterByDoctor(name, patient.getId());
        } else {
            result = patientService.getPatientAppointment(patient.getId(), token).getBody();
        }

        return ResponseEntity.ok(result);
    }
}
