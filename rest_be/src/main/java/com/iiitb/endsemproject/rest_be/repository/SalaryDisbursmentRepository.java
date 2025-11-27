package com.iiitb.endsemproject.rest_be.repository;

import com.iiitb.endsemproject.rest_be.entity.SalaryDisbursment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SalaryDisbursmentRepository extends JpaRepository<SalaryDisbursment, Long> {

/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */
   /**
    * Retrieves a salary disbursement by its ID and the email of the faculty member
    * associated with it.
    *
    * @param id the ID of the salary disbursement
    * @param email the email of the faculty member
    * @return an optional containing the salary disbursement if found, otherwise an empty optional
    */
/* <<<<<<<<<<  c7d167ae-861c-42f0-996a-0dc7a7a01be6  >>>>>>>>>>> */
   Optional<SalaryDisbursment> findByIdAndFaculty_Email(Long id, String email);

   List<SalaryDisbursment> findByFaculty_Email(String email);

   List<SalaryDisbursment> findByFaculty_Email(String email, Sort sort);


}
