package ru.itmo.cs.dandadan.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import ru.itmo.cs.dandadan.validation.annotation.ValidEnum;
import ru.itmo.cs.dandadan.validation.annotation.ValidImpactSpeed;

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

    @NotBlank(message = "'name' cannot be empty")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Valid
    @Embedded
    @NotNull(message = "'coordinates' cannot be null")
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime creationDate;

    @Column(name = "real_hero", nullable = false)
    private boolean realHero;

    @NotNull(message = "'hasToothpick' cannot be null")
    @Column(name = "has_toothpick", nullable = false)
    private Boolean hasToothpick;

    @ValidImpactSpeed
    @Column(name = "impact_speed", nullable = false)
    private int impactSpeed;

    @Column(name = "team_id")
    @Positive(message = "expected a positive integer, got ${validatedValue}")
    private Long teamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type")
    @ValidEnum(nullable = true, message = "should be one of the following: ${validValues}", enumClass = WeaponType.class)
    private WeaponType weaponType;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood", nullable = false)
    @NotNull(message = "'mood' cannot be null")
    @ValidEnum(message = "should be one of the following: ${validValues}", enumClass = Mood.class)
    private Mood mood;

    @Valid
    @Embedded
    private Car car;
}
