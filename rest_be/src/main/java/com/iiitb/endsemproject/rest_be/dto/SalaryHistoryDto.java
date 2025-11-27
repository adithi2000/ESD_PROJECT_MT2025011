package com.iiitb.endsemproject.rest_be.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalaryHistoryDto {

    private Long id; // We include the ID so React can use it to request the payslip
    private LocalDate disbursementDate;
    private BigDecimal grossSalary;
    private BigDecimal deductions;
    private BigDecimal netSalary;
}