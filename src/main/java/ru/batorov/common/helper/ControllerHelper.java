package ru.batorov.common.helper;

import graphql.schema.DataFetchingEnvironment;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ControllerHelper {
  public static Map<String, Object> getInputArgument(DataFetchingEnvironment environment) {
    return environment.getArgument("input");
  }
}
