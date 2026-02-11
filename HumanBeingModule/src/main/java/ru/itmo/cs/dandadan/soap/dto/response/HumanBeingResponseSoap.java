package ru.itmo.cs.dandadan.soap.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;
import ru.itmo.cs.dandadan.model.entity.Car;
import ru.itmo.cs.dandadan.model.entity.Coordinates;
import ru.itmo.cs.dandadan.model.entity.Mood;
import ru.itmo.cs.dandadan.model.entity.WeaponType;
import ru.itmo.cs.dandadan.soap.adapter.ZonedDateTimeAdapter;
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapHumanBeingResponseType", propOrder = {
        "id", "creationDate", "name", "coordinates",
        "realHero", "hasToothpick", "impactSpeed",
        "weaponType", "teamId", "mood", "car"
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumanBeingResponseSoap implements Serializable {

    @Positive
    @NotNull
    @XmlElement(name = "id", required = true)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @XmlElement(name = "creationDate", nillable = true)
    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
    private ZonedDateTime creationDate;

    @NotBlank
    @XmlElement(name = "name", required = true)
    private String name;

    @Valid
    @NotNull
    @XmlElement(name = "coordinates", required = true)
    private Coordinates coordinates;

    @XmlElement(name = "realHero")
    private boolean realHero;

    @XmlElement(name = "hasToothpick")
    private boolean hasToothpick;

    @ValidImpactSpeed
    @XmlElement(name = "impactSpeed")
    private int impactSpeed;

    @XmlElement(name = "weaponType")
    private WeaponType weaponType;

    @Positive
    @XmlElement(name = "teamId")
    private Long teamId;

    @NotNull
    @XmlElement(name = "mood", required = true)
    private Mood mood;

    @Valid
    @XmlElement(name = "car")
    private Car car;
}
