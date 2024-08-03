package ru.batorov.department;

import graphql.relay.Connection;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import ru.batorov.common.helper.ControllerHelper;
import ru.batorov.common.helper.MapperHelper;
import ru.batorov.common.helper.SchemaMappingHelper;
import ru.batorov.common.payload.DeleteMutationPayload;
import ru.batorov.common.payload.SaveMutationPayload;
import ru.batorov.employee.Employee;

@Controller
@RequiredArgsConstructor
public class DepartmentController {
  private final DepartmentService departmentService;
  private final DepartmentDataFetcher departmentDataFetcher;
  private final SchemaMappingHelper schemaMappingHelper;
  private final DepartmentMapper departmentMapper;

  @QueryMapping
  public Connection<Department> departments(DataFetchingEnvironment environment) {
    return departmentDataFetcher.get(environment);
  }

  @SchemaMapping
  public Connection<Employee> employees(
      Department department, DataFetchingEnvironment environment) {
    return schemaMappingHelper.getNestedConnection(department, environment, Employee.class);
  }

  @MutationMapping
  public SaveMutationPayload<Department> createDepartment(DataFetchingEnvironment environment) {
    return updateDepartment(environment);
  }

  @MutationMapping
  public SaveMutationPayload<Department> updateDepartment(DataFetchingEnvironment environment) {
    var department =
        departmentMapper.mapToDepartment(ControllerHelper.getInputArgument(environment));
    return new SaveMutationPayload<>(departmentService.saveDepartment(department));
  }

  @MutationMapping
  public DeleteMutationPayload deleteDepartment(DataFetchingEnvironment environment) {
    var id = MapperHelper.toLong(ControllerHelper.getInputArgument(environment).get("id"));
    departmentService.deleteDepartment(id);
    return new DeleteMutationPayload(id);
  }
}
