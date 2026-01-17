package ru.itmo.cs.dandadan.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sort implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private boolean isDescendingOrder;
    private String fieldName;
    private String nestedName;
}
