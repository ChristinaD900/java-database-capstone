package com.project.back_end.services;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * Book a new appointment
     *
     * @param appointment the appointment object
     * @return 1 if successful, 0 if failure
     */
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Update an existing appointment
     *
     * @param appointment the appointment object with updated data
     * @return ResponseEntity containing success/failure message
     */
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());

        if (existingOpt.isPresent()) {
            Appointment existing = existingOpt.get();

            // Basic validation could be added here, e.g., check for time conflicts
            existing.setAppointmentTime(appointment.getAppointmentTime());
            existing.setStatus(appointment.getStatus());
            existing.setDoctor(appointment.getDoctor());
            existing.setPatient(appointment.getPatient());

            appointmentRepository.save(existing);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Cancel an appointment by ID and token
     *
     * @param id    appointment ID
     * @param token patient or doctor authorization token
     * @return ResponseEntity with success/failure message
     */
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();

            // Optionally validate token to ensure the user can cancel
            String userId = tokenService.extractUserId(token);
            if (!userId.equals(String.valueOf(appointment.getPatient().getId()))) {
                response.put("message", "Unauthorized to cancel this appointment");
                return ResponseEntity.status(403).body(response);
            }

            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Retrieve appointments for a doctor on a specific date, optionally filtered by patient name
     *
     * @param pname patient name filter (optional)
     * @param date  appointment date
     * @param token authorization token (doctor)
     * @return map containing appointments list
     */
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long doctorId = tokenService.extractDoctorId(token);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<Appointment> appointments;
            if (pname != null && !pname.isEmpty()) {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                doctorId, pname, startOfDay, endOfDay);
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
            }

            result.put("appointments", appointments);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("appointments", Collections.emptyList());
            return result;
        }
    }
}
