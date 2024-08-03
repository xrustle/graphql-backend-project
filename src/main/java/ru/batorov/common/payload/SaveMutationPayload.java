package ru.batorov.common.payload;

import lombok.Getter;
import lombok.Setter;
import ru.batorov.common.persistence.Base;

@Getter
@Setter
public class SaveMutationPayload<T extends Base> {
  private T node;

  public SaveMutationPayload(T node) {
    this.node = node;
  }
}
