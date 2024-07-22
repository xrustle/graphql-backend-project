package ru.batorov.controller.common;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import ru.batorov.common.persistence.Base;
import ru.batorov.controller.extension.AppPostgreSqlExtension;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(AppPostgreSqlExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
public class GraphQlApiTests {
  protected static Long itemId;
  protected String typeName;
  @Autowired private HttpGraphQlTester httpGraphQlTester;

  protected GraphQlTester.Response execute(
      Function<HttpGraphQlTester, GraphQlTester.Request<?>> executor) {
    return executor.apply(getGraphQlTester()).execute();
  }

  private HttpGraphQlTester getGraphQlTester() {
    return httpGraphQlTester.mutate().build();
  }

  protected GraphQlTester.Response create(Map<String, Object> input) {
    return execute(createExecutor(input));
  }

  protected GraphQlTester.Response read(Long id) {
    return execute(readExecutor(id));
  }

  protected GraphQlTester.Response update(Map<String, Object> input) {
    return execute(updateExecutor(input));
  }

  protected GraphQlTester.Response delete(Long id) {
    return execute(deleteExecutor(id));
  }

  protected GraphQlTester.Response list() {
    return execute(listExecutor(Collections.emptyMap()));
  }

  private Function<HttpGraphQlTester, GraphQlTester.Request<?>> createExecutor(
      Map<String, Object> input) {
    return operationExecutor("create", toInput(input));
  }

  private Function<HttpGraphQlTester, GraphQlTester.Request<?>> readExecutor(Long id) {
    return operationExecutor("read", Map.of(Base.Fields.id, id));
  }

  private Function<HttpGraphQlTester, GraphQlTester.Request<?>> updateExecutor(
      Map<String, Object> input) {
    return operationExecutor("update", toInput(input));
  }

  private Function<HttpGraphQlTester, GraphQlTester.Request<?>> deleteExecutor(Long id) {
    return operationExecutor("delete", Map.of(Base.Fields.id, id));
  }

  private Function<HttpGraphQlTester, GraphQlTester.Request<?>> listExecutor(
      Map<String, Object> variables) {
    return operationExecutor("list", variables);
  }

  private Function<HttpGraphQlTester, GraphQlTester.Request<?>> operationExecutor(
      String operation, Map<String, Object> variables) {
    var documentName = MessageFormat.format("{0}/{1}", typeName, operation);
    return graphQlExecutorByDocumentName(documentName, variables);
  }

  private Map<String, Object> toInput(Map<String, Object> input) {
    return Map.of("input", input);
  }

  protected Function<HttpGraphQlTester, GraphQlTester.Request<?>> graphQlExecutorByDocumentName(
      String documentName, Map<String, Object> variables) {
    return (graphQlTester) -> {
      var request = graphQlTester.documentName(documentName);
      variables.forEach(request::variable);
      return request;
    };
  }
}
