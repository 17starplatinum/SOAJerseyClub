package ru.itmo.cs.dandadan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// IShowUniqueSpeeds
@Data
@Builder
@AllArgsConstructor
public class UniqueSpeedResponse {
    int[] uniqueSpeeds;
}
