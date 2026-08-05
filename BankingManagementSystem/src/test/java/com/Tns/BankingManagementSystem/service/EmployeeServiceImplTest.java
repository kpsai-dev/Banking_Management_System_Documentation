package com.Tns.BankingManagementSystem.service;


import com.Tns.BankingManagementSystem.dto.EmployeeRequest;
import com.Tns.BankingManagementSystem.dto.EmployeeResponse;
import com.Tns.BankingManagementSystem.entity.Employee;
import com.Tns.BankingManagementSystem.exception.DuplicateResourceException;
import com.Tns.BankingManagementSystem.exception.ResourceNotFoundException;
import com.Tns.BankingManagementSystem.repository.EmployeeRepository;
import com.Tns.BankingManagementSystem.service.EmployeeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest{

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    @DisplayName("Should return employee when id exists")
    void shouldReturnEmployeeWhenIdExists() {

        // Arrange

        Employee employee = Employee.builder()
                .id(1L)
                .name("Sai")
                .email("sai@gmail.com")
                .department("IT")
                .salary(50000.0)
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act

        EmployeeResponse response =
                employeeService.getEmployeeById(1L);

        // Assert

        assertNotNull(response);

        assertEquals("Sai", response.getName());

        assertEquals("sai@gmail.com", response.getEmail());

        verify(employeeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee does not exist")
    void shouldThrowExceptionWhenEmployeeNotFound() {

        when(employeeRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(100L));

        verify(employeeRepository).findById(100L);
    }

    @Test
    @DisplayName("Should create employee successfully")
    void shouldCreateEmployeeSuccessfully() {

        EmployeeRequest request = EmployeeRequest.builder()
                .name("Sai")
                .email("sai@gmail.com")
                .department("IT")
                .salary(50000.0)
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .name(request.getName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .salary(request.getSalary())
                .build();

        when(employeeRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(employee);

        EmployeeResponse response =
                employeeService.createEmployee(request);

        assertEquals("Sai", response.getName());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should delete employee successfully")
    void shouldDeleteEmployeeSuccessfully() {

        Employee employee = Employee.builder()
                .id(1L)
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    @DisplayName("Should update employee successfully")
    void shouldUpdateEmployeeSuccessfully() {

        Employee employee = Employee.builder()
                .id(1L)
                .name("Old")
                .email("old@gmail.com")
                .department("HR")
                .salary(30000.0)
                .build();

        EmployeeRequest request = EmployeeRequest.builder()
                .name("New")
                .email("new@gmail.com")
                .department("IT")
                .salary(60000.0)
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.existsByEmail("new@gmail.com"))
                .thenReturn(false);

        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeService.updateEmployee(1L, request);

        assertEquals("New", response.getName());
        assertEquals("new@gmail.com", response.getEmail());

        verify(employeeRepository).save(any(Employee.class));
    }

}