package ru.itmo.cs.dandadan.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sort {
    private boolean isDescendingOrder;
    private String fieldName;
    private String nestedName;
}
