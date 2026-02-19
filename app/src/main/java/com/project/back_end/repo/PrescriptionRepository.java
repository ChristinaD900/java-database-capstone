package com.project.back_end.repo;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Find prescriptions associated with a specific appointment
     *
     * @param appointmentId the appointment ID to search
     * @return list of matching prescriptions
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}
