package ru.batorov.common.helper;

import lombok.experimental.UtilityClass;
import ru.batorov.department.Department;

@UtilityClass
public class MapperHelper {
  public static Long toLong(Object value) {
    return value != null ? Long.parseLong((String) value) : null;
  }

  public static Department getDepartment(Object value) {
    return value != null ? Department.builder().id(toLong(value)).build() : null;
  }
}
