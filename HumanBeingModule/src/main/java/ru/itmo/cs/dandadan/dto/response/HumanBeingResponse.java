package ru.itmo.cs.dandadan.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.cs.dandadan.model.entity.Car;
import ru.itmo.cs.dandadan.model.entity.Coordinates;
import ru.itmo.cs.dandadan.model.entity.Mood;
import ru.itmo.cs.dandadan.model.entity.WeaponType;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
public class HumanBeingResponse {
    private long id;
    private ZonedDateTime creationDate;
    private String name;
    private Coordinates coordinates;
    private boolean realHero;
    private boolean hasToothpick;
    private int impactSpeed;
    private Long teamId;
    private WeaponType weaponType;
    private Mood mood;
    private Car car;
}
