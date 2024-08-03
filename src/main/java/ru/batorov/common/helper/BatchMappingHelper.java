package ru.batorov.common.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.batorov.common.persistence.Base;

@Component
@RequiredArgsConstructor
public class BatchMappingHelper {
  private final RepositoryHelper repositoryHelper;

  private static <S extends Base, N extends Base>
      BiConsumer<Map<S, N>, Map<S, N>> constructCombiner2() {
    return Map::putAll;
  }

  private static BinaryOperator<Set<Long>> constructCombiner() {
    return (result, ids) -> {
      result.addAll(ids);
      return result;
    };
  }

  public <S extends Base, N extends Base> Map<S, N> toSourceNestedMap(
      List<S> sourceRecords, Function<S, N> nestedGetter, Class<N> nestedClass) {
    var repository = repositoryHelper.getRepository(nestedClass);

    if (repository == null) {
      return new HashMap<>();
    }

    var nestedRecords = repository.findAllById(getNestedIds(sourceRecords, nestedGetter));

    return getSourceNestedMap(sourceRecords, nestedRecords, nestedGetter);
  }

  private <S extends Base, N extends Base> Iterable<Long> getNestedIds(
      List<S> sourceRecords, Function<S, N> nestedGetter) {
    var identity = new HashSet<Long>();

    return sourceRecords.stream()
        .reduce(identity, constructAccumulator(nestedGetter), constructCombiner());
  }

  private <S extends Base, N extends Base> Map<S, N> getSourceNestedMap(
      List<S> sourceRecords, List<N> nestedRecords, Function<S, N> nestedGetter) {
    var nestedMap =
        nestedRecords.stream().collect(Collectors.toMap(Base::getId, Function.identity()));

    return sourceRecords.stream()
        .collect(HashMap::new, constructAccumulator(nestedMap, nestedGetter), constructCombiner2());
  }

  private <S extends Base, N extends Base> BiFunction<Set<Long>, S, Set<Long>> constructAccumulator(
      Function<S, N> nestedGetter) {
    return (ids, sourceRecord) -> {
      Optional.ofNullable(nestedGetter.apply(sourceRecord)).map(Base::getId).ifPresent(ids::add);
      return ids;
    };
  }

  private <S extends Base, N extends Base> BiConsumer<Map<S, N>, S> constructAccumulator(
      Map<Long, N> nestedMap, Function<S, N> nestedGetter) {
    return (resultMap, sourceRecord) ->
        resultMap.put(
            sourceRecord,
            Optional.ofNullable(nestedGetter.apply(sourceRecord))
                .map(nestedRecord -> nestedMap.get(nestedRecord.getId()))
                .orElse(null));
  }
}
