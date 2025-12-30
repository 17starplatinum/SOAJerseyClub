package ru.itmo.cs.parsifal.heroservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HumanBeingFullResponse {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private Boolean realHero;
    private Boolean hasToothpick;
    private Integer impactSpeed;
    private WeaponType weaponType;
    private Long teamId;
    private Mood mood;
    private Car car;
}