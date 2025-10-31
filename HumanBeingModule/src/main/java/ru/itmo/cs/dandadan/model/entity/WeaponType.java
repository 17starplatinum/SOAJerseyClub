package ru.itmo.cs.dandadan.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum WeaponType {
    AXE("axe"),
    SHOTGUN("shotgun"),
    MACHINE_GUN("machine_gun");

    @Getter
    private final String value;

    @Override
    public String toString() {
        return this.value;
    }

    public static WeaponType fromValue(final String value) {
        return Arrays.stream(WeaponType.values())
                .filter(s -> Objects.equals(s.value, value))
                .findFirst()
                .orElse(null);
    }
}
