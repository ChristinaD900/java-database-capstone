package com.project.back_end.services;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    /** Fetch available slots for a doctor on a specific date */
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<String> allSlots = Arrays.asList("09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00"); // example slots
        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX)
                );
        List<String> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    /** Save a new doctor */
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Update an existing doctor */
    public int updateDoctor(Doctor doctor) {
        Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
        if (existing.isEmpty()) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Retrieve all doctors */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /** Delete a doctor by ID, including associated appointments */
    public int deleteDoctor(long id) {
        Optional<Doctor> existing = doctorRepository.findById(id);
        if (existing.isEmpty()) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Validate doctor login credentials */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(doctor.getId(), "doctor");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    /** Find doctors by partial name */
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        result.put("doctors", doctors);
        return result;
    }

    /** Filter doctors by name, specialty, and AM/PM availability */
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    /** Filter doctors by name and AM/PM */
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findByNameLike(name);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    /** Filter doctors by name and specialty */
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> filtered = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    /** Filter doctors by specialty and AM/PM */
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    /** Filter doctors by specialty */
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    /** Filter doctors by AM/PM availability */
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> all = doctorRepository.findAll();
        List<Doctor> filtered = filterDoctorByTime(all, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    /** Private helper: filter a list of doctors by AM/PM */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isEmpty()) return doctors;

        return doctors.stream()
                .filter(d -> d.getAvailability().stream().anyMatch(slot ->
                        (amOrPm.equalsIgnoreCase("AM") && slot.endsWith("AM")) ||
                        (amOrPm.equalsIgnoreCase("PM") && slot.endsWith("PM"))
                ))
                .collect(Collectors.toList());
    }
}
