package ru.batorov.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.domain.Sort;

public class OrderByConverter {
  private final List<Map<String, Object>> orderBy;

  private OrderByConverter(List<Map<String, Object>> orderBy) {
    this.orderBy = Objects.requireNonNullElseGet(orderBy, ArrayList::new);
  }

  public static OrderByConverter of(List<Map<String, Object>> inputOrders) {
    return new OrderByConverter(inputOrders);
  }

  private List<Sort.Order> getSortOrderList(Map<String, Object> orderByItem, String prefix) {
    List<Sort.Order> orders = new ArrayList<>();

    for (Map.Entry<String, Object> entry : orderByItem.entrySet()) {
      orders.addAll(processOrderByItemEntry(entry, prefix));
    }

    return orders;
  }

  private List<Sort.Order> processOrderByItemEntry(Map.Entry<String, Object> entry, String prefix) {
    List<Sort.Order> orders = new ArrayList<>();
    var key = entry.getKey();
    var value = entry.getValue();

    if (value instanceof String stringValue) {
      orders.add(new Sort.Order(Sort.Direction.fromString(stringValue), prefix + key));
    } else if (value instanceof Map) {
      orders.addAll(getSortOrderList((Map<String, Object>) value, prefix + key + "."));
    }

    return orders;
  }

  public Sort toSort() {
    List<Sort.Order> sortOrders = new ArrayList<>();

    for (Map<String, Object> orderByItem : orderBy) {
      sortOrders.addAll(getSortOrderList(orderByItem, ""));
    }

    return sortOrders.isEmpty() ? Sort.by("id") : Sort.by(sortOrders);
  }
}
