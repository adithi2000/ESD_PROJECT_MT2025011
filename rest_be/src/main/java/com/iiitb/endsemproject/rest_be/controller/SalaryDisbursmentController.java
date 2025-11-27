package com.iiitb.endsemproject.rest_be.controller;


import com.iiitb.endsemproject.rest_be.dto.SalaryHistoryDto; // New Import
import com.iiitb.endsemproject.rest_be.service.SalaryService;
import com.iiitb.endsemproject.rest_be.entity.SalaryDisbursment; // New Import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors; // New Import
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api/faculty/salary")
@RequiredArgsConstructor
public class SalaryDisbursmentController {



    private final SalaryService salaryService;


    @GetMapping("/history")
    public ResponseEntity<List<SalaryHistoryDto>> getSalaryHistory() {

     String facultyEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("From SalaryController: "+facultyEmail);
        // 1. Get List of Entities from the Service
        List<SalaryDisbursment> historyEntities = salaryService.getPaymentHistoryByEmail(facultyEmail);

        // 2. Convert Entities to DTOs
        List<SalaryHistoryDto> historyDtos = historyEntities.stream()
                .map(this::convertToDto) // Calls the helper method below for each entity
                .collect(Collectors.toList());

        // 3. Return the List of DTOs

        ResponseEntity<List<SalaryHistoryDto>> data= ResponseEntity.ok(historyDtos);
        System.out.println(data);
        return data;
    }
    // Inside SalaryController.java (replace the previous downloadPayslip method)

    @GetMapping("/download/{payslipId}")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long payslipId) {

        String facultyEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Get the secured data
        Optional<SalaryDisbursment> disbursementOptional = salaryService.getDisbursementDetailsForPayslip(payslipId, facultyEmail);

        if (disbursementOptional.isEmpty()) {
            // Not found or not authorized for this user
            return ResponseEntity.notFound().build();
        }

        try {
            SalaryDisbursment disbursement_ = disbursementOptional.get();

            // 2. Call Service to generate the PDF content as bytes
            byte[] pdfBytes = salaryService.generatePayslipPdf(disbursement_.getId(),facultyEmail);

            // 3. Define the filename for the user's browser
            String filename = "Payslip_" + disbursement_.getDisbursementDate() + ".pdf";

            // 4. Set HTTP Headers to tell the browser this is a PDF file for download
            return ResponseEntity.ok()
                    // Set Content-Type to application/pdf
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    // Set Content-Disposition to trigger a download box
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    // Return the byte array as the response body
                    .body(pdfBytes);

        } catch (RuntimeException e) {
            // Catch exceptions thrown by the service layer during PDF generation
            return ResponseEntity.internalServerError().build();
        }
    }
    // --- Helper Method to perform the conversion ---
    private SalaryHistoryDto convertToDto(SalaryDisbursment entity) {
        SalaryHistoryDto dto = new SalaryHistoryDto();
        dto.setId(entity.getId());
        dto.setDisbursementDate(entity.getDisbursementDate());
        dto.setGrossSalary(entity.getGrossSalary());
        dto.setDeductions(entity.getDeductions());
        dto.setNetSalary(entity.getNetSalary());
        // We SKIP the entity.getPayslipFilePath() for security!
        return dto;
    }
}
