package ru.itmo.cs.dandadan.soap.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.cs.dandadan.soap.adapter.InstantDateTimeAdapter;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class HumanBeingServiceFaultInfo {

    @XmlElement(name = "code")
    private int code;

    @XmlElement(name = "message")
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @XmlElement(name = "timestamp", nillable = true)
    @XmlJavaTypeAdapter(InstantDateTimeAdapter.class)
    private Instant timestamp;

    public HumanBeingServiceFaultInfo(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
    }
}
