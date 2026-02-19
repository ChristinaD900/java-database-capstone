package com.project.back_end.repo;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Find an admin by their username.
     *
     * @param username the admin's username
     * @return Admin entity if found, else null
     */
    Admin findByUsername(String username);
}
