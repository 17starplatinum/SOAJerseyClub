package ru.itmo.cs.dandadan.soap.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "humanBeingServiceFaultInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class HumanBeingServiceFaultInfo {

    @XmlElement(name = "code")
    private int code;

    @XmlElement(name = "message")
    private String message;

    @XmlElement(name = "timestamp")
    private String timestamp;

    public HumanBeingServiceFaultInfo(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }
}
