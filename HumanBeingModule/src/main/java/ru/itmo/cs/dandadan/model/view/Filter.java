package ru.itmo.cs.dandadan.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private String fieldName;
    private String nestedName;
    private FilterCode filterCode;
    private String fieldValue;
}
