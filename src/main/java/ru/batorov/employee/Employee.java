package ru.batorov.employee;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import ru.batorov.common.persistence.Base;
import ru.batorov.department.Department;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Employee extends Base {
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;
}
