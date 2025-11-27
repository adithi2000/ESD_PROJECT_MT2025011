// src/models/Salary.ts

// 1. Interface for the data returned by the /history API
export interface SalaryHistoryDto {
  id: number; // For the database ID (which is Long in Java)
  disbursementDate: string; // Date often comes as a string (e.g., "2025-11-30")
  grossSalary: number; // BigDecimal from Java
  deductions: number;
  netSalary: number;
}

// 2. Interface for the data you might need about the faculty profile
export interface FacultyProfileDto {
  email: string;
  name: string;
  role: string;
  designation: string; // Added the new designation field
  profilePictureUrl?: string; // The '?' makes this field optional/nullable
  internal_id:string;
}
