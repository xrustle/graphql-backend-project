package ru.batorov.common.graphql;

import graphql.relay.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.batorov.common.OrderByConverter;
import ru.batorov.common.helper.RelayHelper;
import ru.batorov.common.persistence.Base;
import ru.batorov.common.persistence.BaseRepository;

@Slf4j
public class ListDataFetcher<E extends Base, R extends BaseRepository<E>>
    implements DataFetcher<Connection<E>> {
  private final R repository;
  private final String cursorPrefix;

  public ListDataFetcher(R repository, String cursorPrefix) {
    this.repository = repository;
    this.cursorPrefix = cursorPrefix;
  }

  public Connection<E> get(DataFetchingEnvironment environment) {
    Integer first = environment.getArgument("first");
    Integer last = environment.getArgument("last");
    var sort = OrderByConverter.of(environment.getArgument("orderBy")).toSort();
    var pageRequest = getPageRequest(environment, first, last, sort);
    var page = repository.findAll(pageRequest);
    var edges = getEdges(page.getContent(), pageRequest.getOffset());
    var pageInfo =
        new DefaultPageInfo(
            !edges.isEmpty() ? edges.getFirst().getCursor() : null,
            !edges.isEmpty() ? edges.getLast().getCursor() : null,
            pageRequest.getOffset() > 0 && page.getTotalElements() > 0,
            page.hasNext());

    return new ExtendedConnection<>(edges, pageInfo, page.getTotalElements());
  }

  private Pageable getPageRequest(
      DataFetchingEnvironment environment, Integer first, Integer last, Sort sort) {
    Pageable pageRequest;

    if (first == null && last == null) {
      first = 100;
    }

    if (first != null) {
      pageRequest = getForwardPagingRequest(environment, first, sort);
    } else {
      pageRequest = getBackwardPagingRequest(environment, last, sort);
    }

    return pageRequest;
  }

  private Pageable getForwardPagingRequest(
      DataFetchingEnvironment environment, Integer first, Sort sort) {
    long offset = getOffsetFromCursor(environment.getArgument("after")) + 1;
    return OffsetBasedPageRequest.of(offset, first, sort);
  }

  private Pageable getBackwardPagingRequest(
      DataFetchingEnvironment environment, Integer last, Sort sort) {
    long offset = getOffsetFromCursor(environment.getArgument("before"));

    if (offset == -1) {
      long count = repository.count();
      return OffsetBasedPageRequest.of(count >= last ? count - last : 0, last, sort);
    } else if (offset == 0) {
      return null;
    } else {
      return offset > last
          ? OffsetBasedPageRequest.of(offset, last, sort).previous()
          : OffsetBasedPageRequest.of(0, (int) offset, sort);
    }
  }

  private long getOffsetFromCursor(String cursor) throws IllegalArgumentException {
    if (cursor == null) {
      return -1;
    }

    return RelayHelper.fromGlobalId(cursor).offset();
  }

  private List<Edge<E>> getEdges(List<E> nodes, long startPosition) {
    long offset = startPosition;
    List<Edge<E>> edges = new ArrayList<>();

    for (E node : nodes) {
      String cursor = RelayHelper.toGlobalId(cursorPrefix, node.getId(), offset++);
      edges.add(new DefaultEdge<>(node, new DefaultConnectionCursor(cursor)));
    }

    return edges;
  }
}
