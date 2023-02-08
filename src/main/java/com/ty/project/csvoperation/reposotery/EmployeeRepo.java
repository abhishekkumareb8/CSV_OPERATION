package com.ty.project.csvoperation.reposotery;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ty.project.csvoperation.dto.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Integer>{

}
