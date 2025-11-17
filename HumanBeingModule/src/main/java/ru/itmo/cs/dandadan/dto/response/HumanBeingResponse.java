package ru.itmo.cs.dandadan.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.cs.dandadan.model.entity.Car;
import ru.itmo.cs.dandadan.model.entity.Coordinates;
import ru.itmo.cs.dandadan.model.entity.Mood;
import ru.itmo.cs.dandadan.model.entity.WeaponType;

import java.io.Serializable;
import java.time.ZonedDateTime;

@JsonPropertyOrder({
        "id",
        "creationDate",
        "name",
        "coordinates",
        "realHero",
        "hasToothpick",
        "impactSpeed",
        "weaponType",
        "teamId",
        "mood",
        "car"
})
@Getter
@Setter
@Builder
public class HumanBeingResponse implements Serializable {
    @NotNull
    private long id;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime creationDate;
    @NotNull
    private String name;
    @Valid
    @NotNull
    private Coordinates coordinates;
    @NotNull
    private boolean realHero;
    @NotNull
    private boolean hasToothpick;
    @NotNull
    private int impactSpeed;
    private WeaponType weaponType;
    private Long teamId;
    @NotNull
    private Mood mood;
    private Car car;
}
