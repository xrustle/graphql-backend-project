package ru.batorov.common.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperHelper {
  public static Long toLong(Object value) {
    return value != null ? Long.parseLong((String) value) : null;
  }
}
