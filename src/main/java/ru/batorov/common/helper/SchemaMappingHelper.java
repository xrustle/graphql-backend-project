package ru.batorov.common.helper;

import graphql.relay.Connection;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.batorov.common.graphql.ListDataFetcher;
import ru.batorov.common.persistence.Base;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchemaMappingHelper {
  private static final String FILTER_ARGUMENT = "filter";
  private final RepositoryHelper repositoryHelper;

  public <S extends Base, N extends Base> Connection<N> getNestedConnection(
      S sourceRecord, DataFetchingEnvironment environment, Class<N> nestedRecordsClass) {
    Map<String, Object> arguments = new HashMap<>(environment.getArguments());
    var filter = getFilterMap(sourceRecord, environment, arguments);

    arguments.put(FILTER_ARGUMENT, filter);

    var environmentBuilder =
        DataFetchingEnvironmentImpl.newDataFetchingEnvironment(environment).arguments(arguments);

    var repository = repositoryHelper.getRepository(nestedRecordsClass);
    return new ListDataFetcher<>(repository, nestedRecordsClass.getName())
        .get(environmentBuilder.build());
  }

  private <S extends Base> Map<String, Object> getFilterMap(
      S sourceRecord, DataFetchingEnvironment environment, Map<String, Object> arguments) {
    Map<String, Object> filter =
        arguments.containsKey(FILTER_ARGUMENT)
            ? environment.getArgument(FILTER_ARGUMENT)
            : new HashMap<>();

    var parentClassName = sourceRecord.getClass().getSimpleName();
    var childFilterRootFieldName =
        parentClassName.substring(0, 1).toLowerCase() + parentClassName.substring(1);

    filter.put(
        childFilterRootFieldName, Map.of("id", Map.of("eq", sourceRecord.getId().toString())));

    return filter;
  }
}
