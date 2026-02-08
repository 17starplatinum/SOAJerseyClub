package ru.itmo.cs.dandadan.soap.dto.request;


import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class HumanBeingQueryRequest {

    @XmlElement(name = "sortParameter")
    @XmlElementWrapper(name = "sortParameters")
    private List<String> sortParameters;

    @XmlElement(name = "filterParameter")
    @XmlElementWrapper(name = "filterParameters")
    private List<String> filterParameters;

    @XmlElement(name = "page")
    private Integer page;

    @XmlElement(name = "pageSize")
    private Integer pageSize;
}
