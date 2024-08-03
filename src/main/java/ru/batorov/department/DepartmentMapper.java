package ru.batorov.department;

import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.batorov.common.helper.MapperHelper;
import ru.batorov.common.persistence.Base;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {
  private final DepartmentRepository departmentRepository;

  public Department mapToDepartment(Map<String, Object> input, Department department) {
    if (input.containsKey(Department.Fields.name)) {
      department.setName((String) input.get(Department.Fields.name));
    }
    return department;
  }

  public Department mapToDepartment(Map<String, Object> input) {
    var id = input.get(Base.Fields.id);
    var department =
        id != null
            ? departmentRepository
                .findById(MapperHelper.toLong(id))
                .orElseThrow(EntityNotFoundException::new)
            : new Department();
    return mapToDepartment(input, department);
  }
}
