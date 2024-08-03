package ru.batorov.controller.extension;

import org.testcontainers.containers.PostgreSQLContainer;

public class AppPostgreSqlContainer extends PostgreSQLContainer<AppPostgreSqlContainer> {
  public static final AppPostgreSqlContainer CONTAINER = new AppPostgreSqlContainer();

  public AppPostgreSqlContainer() {
    super("postgres:13");
  }
}
