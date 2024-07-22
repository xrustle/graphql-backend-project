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
  private final RepositoryHelper repositoryHelper;

  public <N extends Base> Connection<N> getNestedConnection(
      DataFetchingEnvironment environment, Class<N> nestedRecordsClass) {
    var environmentBuilder = DataFetchingEnvironmentImpl.newDataFetchingEnvironment(environment);

    Map<String, Object> arguments = new HashMap<>(environment.getArguments());
    environmentBuilder.arguments(arguments);
    var repository = repositoryHelper.getRepository(nestedRecordsClass);

    return new ListDataFetcher<>(repository).get(environmentBuilder.build());
  }
}
