package ru.itmo.cs.dandadan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.model.entity.Color;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.entity.Mood;
import ru.itmo.cs.dandadan.model.entity.WeaponType;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface HumanBeingMapper {
    HumanBeingResponse toHumanBeingResponse(HumanBeing humanBeing);

    @Mapping(target = "car.color", qualifiedByName = "colorConverter")
    @Mapping(target = "mood", qualifiedByName = "moodConverter")
    @Mapping(target = "weaponType", qualifiedByName = "weaponTypeConverter")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    HumanBeing fromHumanBeingRequest(HumanBeingRequest humanBeingRequest);

    List<HumanBeingResponse> fromEntityList(List<HumanBeing> humanBeings);

    @Named(value = "colorConverter")
    default Color convertColor(String color){
        return Color.fromValue(color);
    }
    @Named(value = "moodConverter")
    default Mood convertMood(String mood){
        return Mood.fromValue(mood);
    }
    @Named(value = "weaponTypeConverter")
    default WeaponType convertWeaponType(String weaponType) {
        return WeaponType.fromValue(weaponType);
    }
}
