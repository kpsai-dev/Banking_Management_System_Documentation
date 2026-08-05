package com.Tns.BankingManagementSystem.service;

import com.Tns.BankingManagementSystem.dto.EmployeeRequest;
import com.Tns.BankingManagementSystem.dto.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Page<EmployeeResponse> getAllEmployees(Pageable pageable);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    void deleteEmployee(Long id);
}
