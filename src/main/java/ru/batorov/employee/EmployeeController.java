package ru.batorov.employee;

import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import ru.batorov.common.helper.ControllerHelper;
import ru.batorov.common.helper.MapperHelper;
import ru.batorov.common.payload.DeleteMutationPayload;
import ru.batorov.common.payload.SaveMutationPayload;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
  private final EmployeeService employeeService;
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  @QueryMapping
  public List<SaveMutationPayload<Employee>> employees() {
    return employeeRepository.findAll().stream().map(SaveMutationPayload::new).toList();
  }

  @MutationMapping
  public SaveMutationPayload<Employee> createEmployee(DataFetchingEnvironment environment) {
    return updateEmployee(environment);
  }

  @MutationMapping
  public SaveMutationPayload<Employee> updateEmployee(DataFetchingEnvironment environment) {
    var employee = employeeMapper.mapToEmployee(ControllerHelper.getInputArgument(environment));
    return new SaveMutationPayload<>(employeeService.saveEmployee(employee));
  }

  @MutationMapping
  public DeleteMutationPayload deleteEmployee(DataFetchingEnvironment environment) {
    var id = MapperHelper.toLong(ControllerHelper.getInputArgument(environment).get("id"));
    employeeService.deleteEmployee(id);
    return new DeleteMutationPayload(id);
  }
}
