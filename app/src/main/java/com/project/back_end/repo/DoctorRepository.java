package com.project.back_end.repo;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find a doctor by their email address
     *
     * @param email the email to search
     * @return Doctor entity if found, else null
     */
    Doctor findByEmail(String email);

    /**
     * Find doctors by partial name match using LIKE
     *
     * @param name partial name to match
     * @return List of matching doctors
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE CONCAT('%', LOWER(:name), '%')")
    List<Doctor> findByNameLike(String name);

    /**
     * Filter doctors by partial name and exact specialty (case-insensitive)
     *
     * @param name partial name to match
     * @param specialty specialty to match
     * @return List of matching doctors
     */
    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE CONCAT('%', LOWER(:name), '%') " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    /**
     * Find doctors by specialty, ignoring case
     *
     * @param specialty specialty to match
     * @return List of doctors with the given specialty
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
