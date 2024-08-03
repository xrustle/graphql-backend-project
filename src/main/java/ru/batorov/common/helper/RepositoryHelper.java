package ru.batorov.common.helper;

import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ru.batorov.common.persistence.Base;
import ru.batorov.common.persistence.BaseRepository;

@Component
public class RepositoryHelper {
  private final Repositories repositories;

  public RepositoryHelper(WebApplicationContext applicationContext) {
    this.repositories = new Repositories(applicationContext);
  }

  @SuppressWarnings("unchecked")
  public <T extends Base> BaseRepository<T> getRepository(Class<T> domainClass) {
    return (BaseRepository<T>) repositories.getRepositoryFor(domainClass).orElse(null);
  }
}
