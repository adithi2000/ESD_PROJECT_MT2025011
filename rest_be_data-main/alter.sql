-- Add foreign key (if not created earlier)
ALTER TABLE salary_disbursement
ADD CONSTRAINT fk_salary_faculty
FOREIGN KEY (faculty_id) REFERENCES faculty(id);

-- Example: Add new column to faculty table
-- ALTER TABLE faculty ADD COLUMN phone VARCHAR(20);

-- Example: Modify column type
-- ALTER TABLE faculty MODIFY COLUMN designation VARCHAR(255) NOT NULL;
