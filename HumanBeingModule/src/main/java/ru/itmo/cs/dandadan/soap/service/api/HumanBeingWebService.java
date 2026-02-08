package ru.itmo.cs.dandadan.soap.service.api;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import ru.itmo.cs.dandadan.soap.dto.request.HumanBeingQueryRequest;
import ru.itmo.cs.dandadan.soap.dto.request.SoapHumanBeingRequest;
import ru.itmo.cs.dandadan.soap.dto.response.HumanBeingPageResponse;
import ru.itmo.cs.dandadan.soap.dto.response.HumanBeingResponseSoap;
import ru.itmo.cs.dandadan.soap.dto.response.UniqueSpeedResponseSoap;
import ru.itmo.cs.dandadan.soap.exception.HumanBeingServiceFault;

@WebService(
        name = "HumanBeingWebService",
        targetNamespace = "https://itmo.ru/humanbeings/service"
)
@SOAPBinding(
        style = SOAPBinding.Style.DOCUMENT,
        use = SOAPBinding.Use.LITERAL,
        parameterStyle = SOAPBinding.ParameterStyle.WRAPPED
)
public interface HumanBeingWebService {

    @WebMethod(operationName = "getHumanBeings")
    @WebResult(name = "humanBeingPageResponse")
    HumanBeingPageResponse getHumanBeings(
            @WebParam(name = "queryRequest") HumanBeingQueryRequest queryRequest
    ) throws HumanBeingServiceFault;

    @WebMethod(operationName = "getHumanBeing")
    @WebResult(name = "humanBeingResponse")
    HumanBeingResponseSoap getHumanBeing(
            @WebParam(name = "id") Long id
    ) throws HumanBeingServiceFault;

    @WebMethod(operationName = "addHumanBeing")
    @WebResult(name = "humanBeingResponse")
    HumanBeingResponseSoap addHumanBeing(
            @WebParam(name = "humanBeingRequest") SoapHumanBeingRequest humanBeingRequest
    ) throws HumanBeingServiceFault;

    @WebMethod(operationName = "updateHumanBeing")
    @WebResult(name = "humanBeingResponse")
    HumanBeingResponseSoap updateHumanBeing(
            @WebParam(name = "id") Long id,
            @WebParam(name = "humanBeingRequest") SoapHumanBeingRequest humanBeingRequest
    ) throws HumanBeingServiceFault;

    @WebMethod(operationName = "deleteHumanBeing")
    void deleteHumanBeing(
            @WebParam(name = "id") Long id
    ) throws HumanBeingServiceFault;

    @WebMethod(operationName = "getUniqueImpactSpeeds")
    @WebResult(name = "uniqueSpeedResponse")
    UniqueSpeedResponseSoap getUniqueImpactSpeeds() throws HumanBeingServiceFault;
}
