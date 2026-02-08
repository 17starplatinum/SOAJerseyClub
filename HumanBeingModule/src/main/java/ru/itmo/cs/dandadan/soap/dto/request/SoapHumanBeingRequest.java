package ru.itmo.cs.dandadan.soap.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapHumanBeingRequest {

    @XmlElement(name = "name", required = true)
    private String name;

    @XmlElement(name = "coordinates", required = true)
    private CoordinatesRequest coordinates;

    @XmlElement(name = "realHero", required = true)
    private Boolean realHero;

    @XmlElement(name = "hasToothpick", required = true)
    private Boolean hasToothpick;

    @XmlElement(name = "impactSpeed", required = true)
    private Integer impactSpeed;

    @XmlElement(name = "weaponType")
    private String weaponType;

    @XmlElement(name = "teamId")
    private Long teamId;

    @XmlElement(name = "mood", required = true)
    private String mood;

    @XmlElement(name = "car")
    private CarRequest car;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CoordinatesRequest {
        @XmlElement(name = "x", required = true)
        private Integer x;

        @XmlElement(name = "y", required = true)
        private Double y;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CarRequest {
        @XmlElement(name = "cool")
        private Boolean cool;

        @XmlElement(name = "color", required = true)
        private String color;

        @XmlElement(name = "model")
        private String model;
    }
}
