package com.iiitb.endsemproject.rest_be.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity // 1. Marks this class as a JPA Entity (maps to a database table)
@Table(name = "faculty") // 2. Specifies the name of the table in MySQL // 3. Lombok annotation for getters, setters, toString, etc.
@RequiredArgsConstructor
@Getter
@Setter
public class Faculty {

    @Id // 4. Designates this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 5. Configures auto-incrementing ID
    private Long id;

    @Column(nullable = false, unique = true) // 6. Ensures email is unique and not empty
    private String email;

    @Column(nullable = false)
    private String name;

    // We don't store a password because we are using OAuth2!

    // Role will be crucial for Spring Security authorization
    @Column(nullable = false)
    private String role = "FACULTY"; // Default role for all users

    @Column(name = "designation")
    private String designation;

    @Column(nullable =true) // Set a larger length for the URL string
    private String profilePictureUrl;

    @Column(nullable =true) // Set a larger length for the URL string
    private String internal_id;
}
