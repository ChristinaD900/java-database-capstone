package com.project.back_end.repo;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by their email address
     *
     * @param email the email to search
     * @return Patient entity if found, else null
     */
    Patient findByEmail(String email);

    /**
     * Find a patient using either email or phone number
     *
     * @param email email to search
     * @param phone phone number to search
     * @return Patient entity if found, else null
     */
    Patient findByEmailOrPhone(String email, String phone);
}

