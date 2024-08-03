package ru.batorov.employee;

import org.springframework.stereotype.Service;
import ru.batorov.common.graphql.ListDataFetcher;

@Service
public class EmployeeDataFetcher extends ListDataFetcher<Employee, EmployeeRepository> {
  public EmployeeDataFetcher(EmployeeRepository employeeRepository) {
    super(employeeRepository, Employee.class.getName());
  }
}
