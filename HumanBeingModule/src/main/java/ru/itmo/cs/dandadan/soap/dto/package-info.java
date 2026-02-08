@XmlSchema(
        namespace = "https://itmo.ru/humanbeings/service",
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(prefix = "tns", namespaceURI = "https://itmo.ru/humanbeings/service")
        }
)
package ru.itmo.cs.dandadan.soap.dto;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
