package com.iiitb.endsemproject.rest_be.service;

import com.iiitb.endsemproject.rest_be.dto.FacultyDto;
import com.iiitb.endsemproject.rest_be.entity.Faculty;
import com.iiitb.endsemproject.rest_be.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyDto getByEmail(String email) {
        Faculty faculty = facultyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        return toDto(faculty);
    }

//    public List<FacultyDto> getAll() {
//        return facultyRepository.findAll()
//                .stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }

    private FacultyDto toDto(Faculty faculty) {
        FacultyDto dto = new FacultyDto();
        dto.setEmail(faculty.getEmail());
        dto.setName(faculty.getName());
        dto.setProfilePictureUrl(faculty.getProfilePictureUrl());
        dto.setRole(faculty.getRole());
        dto.setDesignation(faculty.getDesignation());
        dto.setInternal_id(faculty.getInternal_id());
        return dto;
    }
}
