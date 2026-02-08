package ru.itmo.cs.dandadan.soap.exception;

import jakarta.xml.ws.WebFault;
import lombok.Getter;

@WebFault(name = "humanBeingServiceFault", targetNamespace = "https://itmo.ru/humanbeings/service")
public class HumanBeingServiceFault extends Exception {
    @Getter
    private final HumanBeingServiceFaultInfo faultInfo;

    public HumanBeingServiceFault(String message, HumanBeingServiceFaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public HumanBeingServiceFault(String message, HumanBeingServiceFaultInfo faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }
}
