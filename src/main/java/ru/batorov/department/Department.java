package ru.batorov.department;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import ru.batorov.common.persistence.Base;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Department extends Base {
  private String name;
}
