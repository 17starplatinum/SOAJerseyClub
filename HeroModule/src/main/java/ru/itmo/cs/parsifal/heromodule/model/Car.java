package ru.itmo.cs.parsifal.heromodule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private Boolean cool;
    private Color color;
    private String model;
}