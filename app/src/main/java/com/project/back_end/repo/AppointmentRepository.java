package com.project.back_end.repo;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Retrieve appointments for a doctor within a given time range
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    /**
     * Filter appointments by doctor ID, partial patient name (case-insensitive), and time range
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId " +
           "AND LOWER(p.name) LIKE CONCAT('%', LOWER(:patientName), '%') " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    /**
     * Delete all appointments related to a specific doctor
     */
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    /**
     * Find all appointments for a specific patient
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Retrieve appointments for a patient by status, ordered by appointment time ascending
     */
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    /**
     * Search appointments by partial doctor name and patient ID
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE CONCAT('%', LOWER(:doctorName), '%') " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    /**
     * Filter appointments by doctor name, patient ID, and status
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE CONCAT('%', LOWER(:doctorName), '%') " +
           "AND a.patient.id = :patientId " +
           "AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);
}
