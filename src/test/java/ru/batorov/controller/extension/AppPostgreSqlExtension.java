package ru.batorov.controller.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AppPostgreSqlExtension implements BeforeAllCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    AppPostgreSqlContainer.CONTAINER.start();
    updateAppProps();
  }

  private void updateAppProps() {
    System.setProperty("spring.datasource.url", AppPostgreSqlContainer.CONTAINER.getJdbcUrl());
    System.setProperty(
        "spring.datasource.username", AppPostgreSqlContainer.CONTAINER.getUsername());
    System.setProperty(
        "spring.datasource.password", AppPostgreSqlContainer.CONTAINER.getPassword());
  }
}
