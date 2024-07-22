package ru.batorov.common.payload;

import lombok.Getter;
import lombok.Setter;
import ru.batorov.common.helper.RelayHelper;
import ru.batorov.common.persistence.Base;

@Getter
@Setter
public class SaveMutationPayload<T extends Base> {
  private String cursor;
  private T node;

  public SaveMutationPayload(T node) {
    this.cursor = RelayHelper.toGlobalId(node.getClass().getName(), node.getId(), 0);
    this.node = node;
  }
}
