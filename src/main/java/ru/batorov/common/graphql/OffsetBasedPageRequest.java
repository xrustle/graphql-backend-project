package ru.batorov.common.graphql;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest implements Pageable {
  private final int limit;
  private final long offset;
  private final Sort sort;

  private OffsetBasedPageRequest(long offset, int limit, Sort sort) {
    boolean isOffsetNegative = offset < 0;
    boolean isLimitLessThanMinimum = limit < 1;

    if (isOffsetNegative) {
      throw new IllegalArgumentException(
          "Offset value is negative. It must be a zero or any positive number.");
    }
    if (isLimitLessThanMinimum) {
      throw new IllegalArgumentException(
          "Limit value is less than minimum defined size. It should be greater than or equal to one.");
    }

    this.limit = limit;
    this.offset = offset;
    this.sort = sort;
  }

  public static OffsetBasedPageRequest of(long offset, int size, Sort sort) {
    return new OffsetBasedPageRequest(offset, size, sort);
  }

  @Override
  public int getPageNumber() {
    return (int) offset / limit;
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public Pageable next() {
    return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
  }

  public OffsetBasedPageRequest previous() {
    return hasPrevious() ? of(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
  }

  @Override
  public Pageable previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  @Override
  public Pageable first() {
    return of(0, getPageSize(), getSort());
  }

  @Override
  public boolean hasPrevious() {
    return offset > limit;
  }

  @Override
  public OffsetBasedPageRequest withPage(int pageNumber) {
    return of((long) pageNumber * getPageSize(), getPageSize(), getSort());
  }

  @Override
  public String toString() {
    return String.format(
        "Page request [offset: %d, limit %d, sort: %s]", getOffset(), getPageSize(), getSort());
  }
}
