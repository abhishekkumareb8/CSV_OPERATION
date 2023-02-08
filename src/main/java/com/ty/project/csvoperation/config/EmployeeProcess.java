package com.ty.project.csvoperation.config;

import org.springframework.batch.item.ItemProcessor;

import com.ty.project.csvoperation.dto.Employee;

public class EmployeeProcess implements ItemProcessor<Employee, Employee>{

	@Override
	public Employee process(Employee employee) throws Exception {
		
		return employee;
	}

}
