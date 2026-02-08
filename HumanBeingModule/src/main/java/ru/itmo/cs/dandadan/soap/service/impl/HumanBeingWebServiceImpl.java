package ru.itmo.cs.dandadan.soap.service.impl;

import jakarta.inject.Inject;
import jakarta.jws.WebService;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.exception.ConflictException;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.exception.NotFoundException;
import ru.itmo.cs.dandadan.exception.ValidationFailedException;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.service.api.HumanBeingService;
import ru.itmo.cs.dandadan.soap.dto.request.HumanBeingQueryRequest;
import ru.itmo.cs.dandadan.soap.dto.request.SoapHumanBeingRequest;
import ru.itmo.cs.dandadan.soap.dto.response.HumanBeingPageResponse;
import ru.itmo.cs.dandadan.soap.dto.response.HumanBeingResponseSoap;
import ru.itmo.cs.dandadan.soap.dto.response.UniqueSpeedResponseSoap;
import ru.itmo.cs.dandadan.soap.exception.HumanBeingServiceFault;
import ru.itmo.cs.dandadan.soap.exception.HumanBeingServiceFaultInfo;
import ru.itmo.cs.dandadan.soap.mapper.SoapDtoMapper;
import ru.itmo.cs.dandadan.soap.service.api.HumanBeingWebService;

import java.util.ArrayList;
import java.util.List;


@WebService(
        serviceName = "HumanBeingWebService",
        portName = "HumanBeingWebServicePort",
        targetNamespace = "https://itmo.ru/humanbeings/service",
        endpointInterface = "ru.itmo.cs.dandadan.soap.service.api.HumanBeingWebService"
)
public class HumanBeingWebServiceImpl implements HumanBeingWebService {

    @Inject
    private HumanBeingService humanBeingService;

    @Inject
    private SoapDtoMapper soapDtoMapper;

    @Override
    public HumanBeingPageResponse getHumanBeings(HumanBeingQueryRequest queryRequest)
            throws HumanBeingServiceFault {
        try {
            List<String> sortParams = queryRequest.getSortParameters() != null ?
                    queryRequest.getSortParameters() : new ArrayList<>();
            List<String> filterParams = queryRequest.getFilterParameters() != null ?
                    queryRequest.getFilterParameters() : new ArrayList<>();
            Integer page = queryRequest.getPage() != null ? queryRequest.getPage() : 1;
            Integer pageSize = queryRequest.getPageSize() != null ? queryRequest.getPageSize() : 10;
            if (sortParams.isEmpty()) {
                sortParams.add("id");
            }

            Page<HumanBeingResponse> resultPage =  humanBeingService.getHumanBeings(
                    sortParams, filterParams, page, pageSize
            );

            HumanBeingPageResponse humanBeingPageResponse = new HumanBeingPageResponse();
            humanBeingPageResponse.setHumanBeingGetResponseDtos(resultPage.getHumanBeingGetResponseDtos());
            humanBeingPageResponse.setPage(resultPage.getPage());
            humanBeingPageResponse.setPageSize(resultPage.getPageSize());
            humanBeingPageResponse.setTotalPages(resultPage.getTotalPages());
            humanBeingPageResponse.setTotalCount(resultPage.getTotalCount());
            return humanBeingPageResponse;
        } catch (CustomBadRequestException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(400, e.getMessage())
            );
        } catch (Exception e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(500, e.getMessage())
            );
        }
    }

    @Override
    public HumanBeingResponseSoap getHumanBeing(Long id) throws HumanBeingServiceFault {
        try {
            return soapDtoMapper.fromInternalResponse(humanBeingService.getHumanBeing(id));
        } catch (CustomBadRequestException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(400, e.getMessage())
            );
        } catch (NotFoundException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(404, e.getMessage())
            );
        } catch (Exception e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(500, e.getMessage())
            );
        }
    }

    @Override
    public HumanBeingResponseSoap addHumanBeing(SoapHumanBeingRequest request) throws HumanBeingServiceFault {
        try {
            HumanBeingRequest internalRequest = soapDtoMapper.toInternalRequest(request);
            return soapDtoMapper.fromInternalResponse(
                    humanBeingService.addHumanBeing(internalRequest)
            );
        } catch (ValidationFailedException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(422, e.getMessage())
            );
        } catch (CustomBadRequestException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(400, e.getMessage())
            );
        } catch (ConflictException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(409, e.getMessage())
            );
        } catch (Exception e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(500, e.getMessage())
            );
        }
    }

    @Override
    public HumanBeingResponseSoap updateHumanBeing(Long id, SoapHumanBeingRequest request)
            throws HumanBeingServiceFault {
        try {
            var internalRequest = soapDtoMapper.toInternalRequest(request);
            return soapDtoMapper.fromInternalResponse(
                    humanBeingService.updateHumanBeing(id, internalRequest)
            );
        } catch (NotFoundException e) {
            throw new HumanBeingServiceFault(
                  e.getMessage(),
                  new HumanBeingServiceFaultInfo(404, e.getMessage())
            );
        } catch (ValidationFailedException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(422, e.getMessage())
            );
        } catch (CustomBadRequestException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(400, e.getMessage())
            );
        } catch (ConflictException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(409, e.getMessage())
            );
        } catch (Exception e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(500, e.getMessage())
            );
        }
    }

    @Override
    public void deleteHumanBeing(Long id) throws HumanBeingServiceFault {
        try {
            humanBeingService.deleteHumanBeing(id);
        } catch (NotFoundException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(404, e.getMessage())
            );
        } catch (CustomBadRequestException e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(400, e.getMessage())
            );
        } catch (Exception e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(500, e.getMessage())
            );
        }
    }

    @Override
    public UniqueSpeedResponseSoap getUniqueImpactSpeeds() throws HumanBeingServiceFault {
        try {
            UniqueSpeedResponseSoap responseSoap = new UniqueSpeedResponseSoap();
            responseSoap.setUniqueImpactSpeeds(
                    humanBeingService.getUniqueImpactSpeeds().getUniqueSpeeds()
            );
            return responseSoap;
        } catch (Exception e) {
            throw new HumanBeingServiceFault(
                    e.getMessage(),
                    new HumanBeingServiceFaultInfo(500, e.getMessage())
            );
        }
    }
}
