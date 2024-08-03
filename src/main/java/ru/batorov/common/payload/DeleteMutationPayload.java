package ru.batorov.common.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class DeleteMutationPayload {
  private final Long id;
}
