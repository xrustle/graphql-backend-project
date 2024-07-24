package ru.batorov.common.graphql;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import ru.batorov.common.persistence.Base;

@Slf4j
public class FilterSpecification<T extends Base> implements Specification<T> {
  private static final String EQ = "eq";
  private static final String IN = "in";
  private static final String LIKE = "like";
  private final transient Map<String, Object> filter;

  private FilterSpecification(Map<String, Object> filter) {
    this.filter = Objects.requireNonNullElseGet(filter, HashMap::new);
  }

  public static <T extends Base> FilterSpecification<T> of(Map<String, Object> filter) {
    return new FilterSpecification<>(filter);
  }

  private boolean isExactOperator(String operator) {
    return List.of(EQ, IN, LIKE).contains(operator);
  }

  private <X, Y extends Comparable<? super Y>> Predicate getPredicateByOperator(
      Expression<Y> expression,
      String operator,
      Object value,
      CriteriaBuilder criteriaBuilder,
      Function<X, Y> valueGetter) {
    if (!operator.equals(EQ) && value == null) {
      throw new IllegalArgumentException("unsupported value null for operator " + operator);
    }

    return switch (operator) {
      case EQ ->
          value == null
              ? criteriaBuilder.isNull(expression)
              : criteriaBuilder.equal(expression, valueGetter.apply((X) value));
      case LIKE ->
          criteriaBuilder.like(
              (Expression<String>) expression, "%" + valueGetter.apply((X) value) + "%");
      case IN -> {
        var inValues = (List<X>) value;
        var predicate = criteriaBuilder.in(expression);
        inValues.forEach(inValue -> predicate.value(valueGetter.apply(inValue)));
        yield predicate;
      }
      default -> throw new IllegalArgumentException("unsupported operator " + operator);
    };
  }

  private Predicate getPredicateByType(
      Path<?> path, String operator, Object value, CriteriaBuilder criteriaBuilder) {
    Class<?> pathClass = path.getJavaType();

    if (pathClass.isAssignableFrom(String.class)) {
      Expression<String> expression = criteriaBuilder.lower(path.as(String.class));
      UnaryOperator<String> valueGetter = String::toLowerCase;
      return getPredicateByOperator(expression, operator, value, criteriaBuilder, valueGetter);
    } else if (pathClass.isAssignableFrom(Long.class)) {
      return getPredicateByOperator(
          path.as(Long.class), operator, value, criteriaBuilder, Function.identity());
    } else {
      throw new IllegalArgumentException(
          "unsupported type " + pathClass.getName() + " for operator '" + operator + "'");
    }
  }

  private List<Predicate> getPredicates(
      Map<String, Object> filter, CriteriaBuilder criteriaBuilder, Path<?> parentPath) {
    var predicates = new ArrayList<Predicate>();

    for (Map.Entry<String, Object> entry : filter.entrySet()) {
      var key = entry.getKey();
      var value = entry.getValue();

      if (value instanceof Map) {
        if (isExactOperator(key)) {
          predicates.add(getPredicateByType(parentPath, key, value, criteriaBuilder));
        } else {
          predicates.addAll(
              getPredicates((Map<String, Object>) value, criteriaBuilder, parentPath.get(key)));
        }
      } else {
        if (isExactOperator(key)) {
          predicates.add(getPredicateByType(parentPath, key, value, criteriaBuilder));
        } else {
          predicates.add(getPredicateByType(parentPath.get(key), EQ, value, criteriaBuilder));
        }
      }
    }

    return predicates;
  }

  @Override
  public Predicate toPredicate(
      @NonNull Root<T> root,
      @NonNull CriteriaQuery<?> query,
      @NonNull CriteriaBuilder criteriaBuilder) {
    List<Predicate> predicates = getPredicates(filter, criteriaBuilder, root);
    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }
}
