package ru.batorov.employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeService {
  private final EmployeeRepository employeeRepository;

  public Employee saveEmployee(Employee employee) {
    return employeeRepository.save(employee);
  }

  public void deleteEmployee(Long id) {
    employeeRepository.deleteById(id);
  }
}
