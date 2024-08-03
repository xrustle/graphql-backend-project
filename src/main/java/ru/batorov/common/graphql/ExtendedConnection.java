package ru.batorov.common.graphql;

import graphql.relay.DefaultConnection;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ExtendedConnection<T> extends DefaultConnection<T> {
  private final long totalCount;

  public ExtendedConnection(List<Edge<T>> edges, PageInfo pageInfo, long totalCount) {
    super(edges, pageInfo);
    this.totalCount = totalCount;
  }
}
