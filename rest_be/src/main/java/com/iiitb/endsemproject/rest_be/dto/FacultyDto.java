package com.iiitb.endsemproject.rest_be.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Lombok: Getters, Setters, toString
@NoArgsConstructor // Lombok: Default constructor
@AllArgsConstructor // Lombok: Constructor with all fields
public class FacultyDto {

    private String email;
    private String name;
    private String profilePictureUrl;
    private String role;
    private String designation;
    private String internal_id;
    // We don't include the internal ID (Long id) or salary details here.
}
