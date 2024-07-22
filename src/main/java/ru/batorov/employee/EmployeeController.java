package ru.batorov.employee;

import graphql.relay.Connection;
import graphql.schema.DataFetchingEnvironment;
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
  private final EmployeeDataFetcher employeeDataFetcher;
  private final EmployeeMapper employeeMapper;

  @QueryMapping
  public Connection<Employee> employees(DataFetchingEnvironment environment) {
    return employeeDataFetcher.get(environment);
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
