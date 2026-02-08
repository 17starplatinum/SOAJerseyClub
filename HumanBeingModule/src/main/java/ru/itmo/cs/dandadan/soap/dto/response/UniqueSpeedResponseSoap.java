package ru.itmo.cs.dandadan.soap.dto.response;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UniqueSpeedResponseType")
public class UniqueSpeedResponseSoap {
    @XmlElement(required = true)
    private int[] uniqueImpactSpeeds;
}
