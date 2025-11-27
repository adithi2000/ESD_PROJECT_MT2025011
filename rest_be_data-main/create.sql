-- ===========================
-- CREATE TABLE: faculty
-- ===========================
CREATE TABLE faculty (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    profile_picture_url VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    designation VARCHAR(255),
    internal_id VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_faculty_email (email)
);

-- ===========================
-- CREATE TABLE: salary_disbursement
-- ===========================
CREATE TABLE salary_disbursement (
    id BIGINT NOT NULL AUTO_INCREMENT,
    deductions DECIMAL(10,2) NOT NULL,
    disbursement_date DATE NOT NULL,
    gross_salary DECIMAL(10,2) NOT NULL,
    net_salary DECIMAL(10,2) NOT NULL,
    faculty_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_faculty_id (faculty_id),
    CONSTRAINT fk_salary_faculty
        FOREIGN KEY (faculty_id) REFERENCES faculty(id)
);
