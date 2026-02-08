package ru.itmo.cs.dandadan.soap.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.model.entity.Car;
import ru.itmo.cs.dandadan.model.entity.Coordinates;
import ru.itmo.cs.dandadan.soap.dto.request.SoapHumanBeingRequest;
import ru.itmo.cs.dandadan.soap.dto.response.HumanBeingResponseSoap;

@ApplicationScoped
public class SoapDtoMapper {

    public HumanBeingRequest toInternalRequest(SoapHumanBeingRequest soapRequest) {
        if (soapRequest == null) {
            return null;
        }
        HumanBeingRequest internalRequest = new HumanBeingRequest();
        internalRequest.setName(soapRequest.getName());
        internalRequest.setRealHero(soapRequest.getRealHero());
        internalRequest.setHasToothpick(soapRequest.getHasToothpick());
        internalRequest.setImpactSpeed(soapRequest.getImpactSpeed());
        internalRequest.setWeaponType(soapRequest.getWeaponType());
        internalRequest.setTeamId(soapRequest.getTeamId());
        internalRequest.setMood(soapRequest.getMood());

        if (soapRequest.getCoordinates() != null) {
            HumanBeingRequest.CoordinatesRequest coords = new HumanBeingRequest.CoordinatesRequest(
                    soapRequest.getCoordinates().getX(),
                    soapRequest.getCoordinates().getY()
            );
            internalRequest.setCoordinates(coords);
        }

        if (soapRequest.getCar() != null) {
            HumanBeingRequest.CarRequest car = new HumanBeingRequest.CarRequest(
                    soapRequest.getCar().getCool(),
                    soapRequest.getCar().getColor(),
                    soapRequest.getCar().getModel()
            );
            internalRequest.setCar(car);
        }
        return internalRequest;
    }

    public HumanBeingResponseSoap fromInternalResponse(HumanBeingResponse internalResponse) {
        if (internalResponse == null) {
            return null;
        }
        HumanBeingResponseSoap response = new HumanBeingResponseSoap();
        response.setId(internalResponse.getId());
        response.setCreationDate(internalResponse.getCreationDate());
        response.setName(internalResponse.getName());
        response.setRealHero(internalResponse.isRealHero());
        response.setHasToothpick(internalResponse.isHasToothpick());
        response.setImpactSpeed(internalResponse.getImpactSpeed());
        response.setWeaponType(internalResponse.getWeaponType());
        response.setTeamId(internalResponse.getTeamId());
        response.setMood(internalResponse.getMood());

        if (response.getCoordinates() != null) {
            Coordinates coords = new Coordinates(
                    response.getCoordinates().getX(),
                    response.getCoordinates().getY()
            );
            response.setCoordinates(coords);
        }

        if (response.getCar() != null) {
            Car car = new Car(
                    response.getCar().getCool(),
                    response.getCar().getColor(),
                    response.getCar().getModel()
            );
            response.setCar(car);
        }
        return response;
    }
}
