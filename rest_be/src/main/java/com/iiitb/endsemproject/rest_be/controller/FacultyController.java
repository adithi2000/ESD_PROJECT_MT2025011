package com.iiitb.endsemproject.rest_be.controller;

import com.iiitb.endsemproject.rest_be.dto.FacultyDto;
import com.iiitb.endsemproject.rest_be.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/faculty/")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping("/details")
    public ResponseEntity<FacultyDto> getFaculty() {
        String facultyEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("From FacultyController: "+facultyEmail);
       ResponseEntity<FacultyDto> data=ResponseEntity.ok(facultyService.getByEmail(facultyEmail));
        System.out.println(data);
       return data;
    }
}
