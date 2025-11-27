package com.iiitb.endsemproject.rest_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal; // Use BigDecimal for currency to avoid floating point errors
import java.time.LocalDate; // Use LocalDate for dates

@Entity
@Table(name = "salary_disbursement")
@RequiredArgsConstructor
@Getter
@Setter
public class SalaryDisbursment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Relationship: Many salary records belong to One Faculty member
    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false) // 2. Creates the foreign key column
    private Faculty faculty;

    @Column(nullable = false)
    private LocalDate disbursementDate;

    @Column(precision = 10, scale = 2, nullable = false) // 10 total digits, 2 after decimal
    private BigDecimal grossSalary;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal deductions;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal netSalary;

//    @Column(nullable = true) // Path to where the PDF payslip is stored on the server
//    private String payslipFilePath;


}
