package ru.batorov.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.graphql.test.tester.GraphQlTester;
import ru.batorov.common.persistence.Base;
import ru.batorov.controller.common.GraphQlApiTests;
import ru.batorov.employee.Employee;

@DisplayName("Employees API")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class EmployeeApiTest extends GraphQlApiTests {
  protected static Long itemId;

  EmployeeApiTest() {
    this.typeName = "employee";
  }

  private Employee getNewEmployeePattern() {
    return Employee.builder().name("employeeName").build();
  }

  private Employee getUpdateEmployeePattern() {
    return Employee.builder().name("updatedEmployeeName").build();
  }

  private void assertEmployeePayload(
      GraphQlTester.Response response, String basePath, Employee pattern) {
    assertPayload(response, basePath, pattern);
    response
        .path(basePath + Employee.Fields.name)
        .entity(String.class)
        .isEqualTo(pattern.getName());
  }

  private Map<String, Object> toCreateInput(Employee employee) {
    var input = new HashMap<String, Object>();
    input.put(Employee.Fields.name, employee.getName());
    return input;
  }

  private Map<String, Object> toUpdateInput(Employee employee) {
    var input = toCreateInput(employee);
    input.put(Base.Fields.id, itemId);
    return input;
  }

  protected <T extends Employee> void assertPayload(
      GraphQlTester.Response response, String basePath, T pattern) {
    assertBaseAttributes(response, basePath);
  }

  protected void assertBaseAttributes(GraphQlTester.Response response, String basePath) {
    response
        .path(basePath + Base.Fields.id)
        .entity(Long.class)
        .satisfies(Assertions::assertNotNull);
  }

  @Order(1)
  @Nested
  class Create {
    @Test
    void Successful() {
      var basePath = "createEmployee.node.";
      var pattern = getNewEmployeePattern();
      var response = create(toCreateInput(pattern));

      assertEmployeePayload(response, basePath, pattern);
      itemId = response.path(basePath + Base.Fields.id).entity(Long.class).get();
    }
  }

  @Order(2)
  @Nested
  class Update {
    @Test
    void Successful() {
      var basePath = "updateEmployee.node.";
      var pattern = getUpdateEmployeePattern();
      var response = update(toUpdateInput(pattern));

      assertEmployeePayload(response, basePath, pattern);
    }
  }

  @Order(3)
  @Nested
  class List {
    @Test
    void Successful() {
      var response = list();
      assertEmployeePayload(response, "employees.edges[5].node.", getUpdateEmployeePattern());
      response.path("employees.edges").entityList(Object.class).hasSize(6);
    }
  }

  @Order(4)
  @Nested
  class Delete {
    @Test
    void Successful() {
      delete(itemId)
          .path("deleteEmployee.id")
          .entity(Long.class)
          .satisfies(id -> assertEquals(itemId, id));
    }
  }
}
