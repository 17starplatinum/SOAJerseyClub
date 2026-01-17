package ru.itmo.cs.dandadan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

// IShowUniqueSpeeds
@Data
@Builder
@AllArgsConstructor
public class UniqueSpeedResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    int[] uniqueSpeeds;
}
