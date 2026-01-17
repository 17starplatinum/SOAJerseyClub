package ru.itmo.cs.dandadan.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum WeaponType implements Serializable {
    AXE("axe"),
    SHOTGUN("shotgun"),
    MACHINE_GUN("machine_gun");

    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final String value;

    @Override
    public String toString() {
        return this.value;
    }

    public static WeaponType fromValue(final String value) {
        if (Objects.isNull(value)) return null;
        return Arrays.stream(WeaponType.values())
                .filter(s -> Objects.equals(s.value, value.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
