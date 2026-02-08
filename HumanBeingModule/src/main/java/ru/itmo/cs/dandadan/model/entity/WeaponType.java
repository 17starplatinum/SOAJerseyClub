package ru.itmo.cs.dandadan.model.entity;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@XmlType(name = "weaponType")
@XmlEnum
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
        if (Objects.isNull(value)) return null;
        return Arrays.stream(WeaponType.values())
                .filter(s -> Objects.equals(s.value, value.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
