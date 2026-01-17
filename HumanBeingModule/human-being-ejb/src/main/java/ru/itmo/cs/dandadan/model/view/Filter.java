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
public class Filter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String fieldName;
    private String nestedName;
    private FilterCode filterCode;
    private String fieldValue;
}
