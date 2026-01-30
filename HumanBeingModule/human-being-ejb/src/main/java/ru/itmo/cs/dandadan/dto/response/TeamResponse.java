package ru.itmo.cs.dandadan.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private int size;
}
