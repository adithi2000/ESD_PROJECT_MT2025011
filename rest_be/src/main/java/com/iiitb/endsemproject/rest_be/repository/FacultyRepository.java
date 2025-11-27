package com.iiitb.endsemproject.rest_be.repository;

import com.iiitb.endsemproject.rest_be.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Used to handle cases where a user might not be found

    // 1. Extends JpaRepository to get basic CRUD operations for the Faculty entity
    public interface FacultyRepository extends JpaRepository<Faculty, Long> {

        // 2. Custom Finder Method: Spring automatically understands how to write the SQL query for this method name.
        Optional<Faculty> findByEmail(String email);
        // We can add more methods like: List<Faculty> findByRole(String role);
    }

