package ru.batorov.department;

import org.springframework.stereotype.Service;
import ru.batorov.common.graphql.ListDataFetcher;

@Service
public class DepartmentDataFetcher extends ListDataFetcher<Department, DepartmentRepository> {
  public DepartmentDataFetcher(DepartmentRepository departmentRepository) {
    super(departmentRepository);
  }
}
