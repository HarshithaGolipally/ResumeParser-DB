package com.resumeparser.repository;

//import com.resumeparser.model.Employee;
import com.resumeparser.model.Resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Resume, Long> {
}
