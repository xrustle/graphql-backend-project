package ru.batorov.employee;

import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.batorov.common.helper.MapperHelper;
import ru.batorov.common.persistence.Base;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {
  private final EmployeeRepository employeeRepository;

  public Employee mapToEmployee(Map<String, Object> input, Employee employee) {
    if (input.containsKey(Employee.Fields.name)) {
      employee.setName((String) input.get(Employee.Fields.name));
    }

    if (input.containsKey(Employee.Fields.department + "Id")) {
      employee.setDepartment(
          MapperHelper.getDepartment(input.get(Employee.Fields.department + "Id")));
    }

    return employee;
  }

  public Employee mapToEmployee(Map<String, Object> input) {
    var id = input.get(Base.Fields.id);
    var employee =
        id != null
            ? employeeRepository
                .findById(MapperHelper.toLong(id))
                .orElseThrow(EntityNotFoundException::new)
            : new Employee();
    return mapToEmployee(input, employee);
  }
}
