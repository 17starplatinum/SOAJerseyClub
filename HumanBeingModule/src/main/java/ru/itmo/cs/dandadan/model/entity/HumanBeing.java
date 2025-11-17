package ru.itmo.cs.dandadan.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.ZoneId;
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
@Entity(name = "human_beings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumanBeing implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Поле 'name' не должно быть null")
    @NotBlank(message = "Поле 'name' не должно быть пустым")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Valid
    @Embedded
    @NotNull(message = "Поле 'coordinates' не должно быть null")
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private java.time.ZonedDateTime creationDate;

    @NotNull(message = "Поле 'realHero' не должно быть null")
    @Column(name = "real_hero", nullable = false)
    private boolean realHero;

    @NotNull(message = "Поле 'hasToothpick' не должно быть null")
    @Column(name = "has_toothpick", nullable = false)
    private Boolean hasToothpick;

    @Max(58)
    @NotNull(message = "Поле 'impactSpeed' не должно быть null")
    @Column(name = "impact_speed", nullable = false)
    private int impactSpeed;

    @Column(name = "team_id")
    private Long teamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type")
    private WeaponType weaponType;

    @NotNull(message = "Поле 'mood' не может быть null")
    @Enumerated(EnumType.STRING)
    @Column(name = "mood", nullable = false)
    private Mood mood;

    @Valid
    @Embedded
    private Car car;
}
