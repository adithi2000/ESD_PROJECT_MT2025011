package com.iiitb.endsemproject.rest_be.service;

import com.iiitb.endsemproject.rest_be.entity.SalaryDisbursment;
import com.iiitb.endsemproject.rest_be.repository.SalaryDisbursmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryDisbursmentRepository salaryDisbursmentRepository;

    // Returns a faculty's full salary payment history by email, most recent first
    public List<SalaryDisbursment> getPaymentHistoryByEmail(String email) {
        Sort sort = Sort.by(Sort.Direction.DESC, "disbursementDate");
        return salaryDisbursmentRepository.findByFaculty_Email(email, sort);
    }
        public Optional<SalaryDisbursment> getDisbursementDetailsForPayslip(Long payslipId, String facultyEmail) {
    // This method is primarily used for the SECURITY CHECK and data retrieval.
    // It fetches all data needed for the PDF (gross salary, date, etc.).
        return salaryDisbursmentRepository.findByIdAndFaculty_Email(payslipId, facultyEmail);
        }
        //pdf Creator helper:
    public byte[] generatePayslipPdf(Long payslipId, String facultyEmail) {
        Optional<SalaryDisbursment> disbursement = getDisbursementDetailsForPayslip(payslipId, facultyEmail);
        if (disbursement.isEmpty()) {
            throw new RuntimeException("Payslip not found");
        }
        SalaryDisbursment salary = disbursement.get();

        // Create PDF document
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            ImageData imgData = ImageDataFactory.create("src/main/resources/iiitb_logo.jpg");
            Image headerImg = new Image(imgData);
            // Add content to PDF
            document.add(headerImg.scaleToFit(120, 120));
            document.add(new Paragraph("International Institute of Information Technology, Bangalore:"));
            document.add(new Paragraph("Payslip:"));
            document.add(new Paragraph("Internal ID: "+salary.getFaculty().getInternal_id()));
            document.add(new Paragraph("Faculty: " + salary.getFaculty().getName()));
            document.add(new Paragraph("Disbursement Date: " + salary.getDisbursementDate()));
            document.add(new Paragraph("Gross Salary: " + salary.getGrossSalary()));
            document.add(new Paragraph("Deductions: " + salary.getDeductions()));
            document.add(new Paragraph("Net Salary: " + salary.getNetSalary()));
            document.add(new Paragraph("Invoice Verified by Institute"));
            document.setMargins(90, 36, 60, 36); // top, right, bottom, left
            // Close document
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
