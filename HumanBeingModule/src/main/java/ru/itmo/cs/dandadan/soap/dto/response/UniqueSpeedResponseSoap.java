package ru.itmo.cs.dandadan.soap.dto.response;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UniqueSpeedResponseType")
public class UniqueSpeedResponseSoap {
    @XmlElement(name = "impactSpeed", required = true)
    @XmlElementWrapper(name = "uniqueImpactSpeeds")
    private List<Integer> uniqueImpactSpeeds;
}
