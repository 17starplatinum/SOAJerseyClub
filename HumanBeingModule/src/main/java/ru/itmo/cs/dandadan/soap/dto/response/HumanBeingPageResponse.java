package ru.itmo.cs.dandadan.soap.dto.response;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "humanBeingPageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class HumanBeingPageResponse {

    @XmlElement(name = "humanBeingGetResponseDto")
    @XmlElementWrapper(name = "humanBeingGetResponseDtos")
    private List<HumanBeingResponse> humanBeingGetResponseDtos;

    @XmlElement(name = "page")
    private Integer page;

    @XmlElement(name = "pageSize")
    private Integer pageSize;

    @XmlElement(name = "totalPages")
    private Integer totalPages;

    @XmlElement(name = "totalCount")
    private Integer totalCount;
}
