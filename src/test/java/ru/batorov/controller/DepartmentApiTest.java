package ru.batorov.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.graphql.test.tester.GraphQlTester;
import ru.batorov.common.persistence.Base;
import ru.batorov.controller.common.GraphQlApiTests;
import ru.batorov.department.Department;

@DisplayName("Departments API")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class DepartmentApiTest extends GraphQlApiTests {
  protected static Long itemId;

  DepartmentApiTest() {
    this.typeName = "department";
  }

  private Department getNewDepartmentPattern() {
    return Department.builder().name("departmentName").build();
  }

  private Department getUpdateDepartmentPattern() {
    return Department.builder().name("updatedDepartmentName").build();
  }

  private void assertDepartmentPayload(
      GraphQlTester.Response response, String basePath, Department pattern) {
    assertPayload(response, basePath, pattern);
    response
        .path(basePath + Department.Fields.name)
        .entity(String.class)
        .isEqualTo(pattern.getName());
  }

  private Map<String, Object> toCreateInput(Department department) {
    var input = new HashMap<String, Object>();
    input.put(Department.Fields.name, department.getName());
    return input;
  }

  private Map<String, Object> toUpdateInput(Department department) {
    var input = toCreateInput(department);
    input.put(Base.Fields.id, itemId);
    return input;
  }

  protected <T extends Department> void assertPayload(
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
      var basePath = "createDepartment.node.";
      var pattern = getNewDepartmentPattern();
      var response = create(toCreateInput(pattern));

      assertDepartmentPayload(response, basePath, pattern);
      itemId = response.path(basePath + Base.Fields.id).entity(Long.class).get();
    }
  }

  @Order(2)
  @Nested
  class Update {
    @Test
    void Successful() {
      var basePath = "updateDepartment.node.";
      var pattern = getUpdateDepartmentPattern();
      var response = update(toUpdateInput(pattern));

      assertDepartmentPayload(response, basePath, pattern);
    }
  }

  @Order(3)
  @Nested
  class List {
    @Test
    void Successful() {
      var response = list();
      assertDepartmentPayload(response, "departments.edges[2].node.", getUpdateDepartmentPattern());
      response.path("departments.edges").entityList(Object.class).hasSize(3);
    }
  }

  @Order(4)
  @Nested
  class Delete {
    @Test
    void Successful() {
      delete(itemId)
          .path("deleteDepartment.id")
          .entity(Long.class)
          .satisfies(id -> assertEquals(itemId, id));
    }
  }
}
