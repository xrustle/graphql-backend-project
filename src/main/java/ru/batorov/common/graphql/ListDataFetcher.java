package ru.batorov.common.graphql;

import graphql.relay.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import ru.batorov.common.OrderByConverter;
import ru.batorov.common.helper.RelayHelper;
import ru.batorov.common.persistence.Base;
import ru.batorov.common.persistence.BaseRepository;

@Slf4j
public class ListDataFetcher<E extends Base, R extends BaseRepository<E>>
    implements DataFetcher<Connection<E>> {
  private final R repository;

  public ListDataFetcher(R repository) {
    this.repository = repository;
  }

  public Connection<E> get(DataFetchingEnvironment environment) {
    var sort = OrderByConverter.of(environment.getArgument("orderBy")).toSort();
    var nodes = repository.findAll(sort);
    var pageInfo = new DefaultPageInfo(null, null, false, false);
    var edges =
        nodes.stream()
            .map(
                node -> {
                  var globalId = RelayHelper.toGlobalId(node.getClass().getName(), node.getId(), 0);
                  new Relay().toGlobalId(node.getClass().getSimpleName(), node.getId().toString());
                  return (Edge<E>) new DefaultEdge<>(node, new DefaultConnectionCursor(globalId));
                })
            .toList();

    return new ExtendedConnection<>(edges, pageInfo, edges.size());
  }
}
