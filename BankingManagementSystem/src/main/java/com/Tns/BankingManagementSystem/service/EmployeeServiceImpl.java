package com.Tns.BankingManagementSystem.service;

import com.Tns.BankingManagementSystem.dto.EmployeeRequest;
import com.Tns.BankingManagementSystem.dto.EmployeeResponse;
import com.Tns.BankingManagementSystem.entity.Employee;
import com.Tns.BankingManagementSystem.exception.DuplicateResourceException;
import com.Tns.BankingManagementSystem.exception.ResourceNotFoundException;
import com.Tns.BankingManagementSystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Page<EmployeeResponse> getAllEmployees( Pageable pageable) {

        return employeeRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Cacheable(value = "employees", key = "#id")
    @Override
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists");
        }

        Employee employee = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .salary(request.getSalary())
                .build();

        Employee saved = employeeRepository.save(employee);

        return mapToResponse(saved);
    }

    @CachePut(value = "employees", key = "#id")
    @Override
    public EmployeeResponse updateEmployee(Long id,
                                           EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        if (!employee.getEmail().equals(request.getEmail())
                && employeeRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists");
        }

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        Employee updated = employeeRepository.save(employee);

        return mapToResponse(updated);
    }

    @CacheEvict(value = "employees", key = "#id")
    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        employeeRepository.delete(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {

        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .build();
    }
}