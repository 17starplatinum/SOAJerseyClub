package ru.itmo.cs.dandadan.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.model.entity.Color;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.entity.Mood;
import ru.itmo.cs.dandadan.model.entity.WeaponType;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "cdi")
public interface HumanBeingMapper {
    @Mapping(target = "car.color", source = "car.color", qualifiedByName = "colorConverter")
    @Mapping(target = "mood", source = "mood", qualifiedByName = "moodConverter")
    @Mapping(target = "weaponType", source = "weaponType", qualifiedByName = "weaponTypeConverter")
    HumanBeingResponse toHumanBeingResponse(HumanBeing humanBeing);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "car.color", source = "car.color", qualifiedByName = "colorConverter")
    @Mapping(target = "mood", source = "mood", qualifiedByName = "moodConverter")
    @Mapping(target = "weaponType", source = "weaponType", qualifiedByName = "weaponTypeConverter")
    HumanBeing fromHumanBeingRequest(HumanBeingRequest humanBeingRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "car.color", source = "car.color", qualifiedByName = "colorConverter")
    @Mapping(target = "mood", source = "mood", qualifiedByName = "moodConverter")
    @Mapping(target = "weaponType", source = "weaponType", qualifiedByName = "weaponTypeConverter")
    void updateFromHumanBeingRequest(HumanBeing incoming, @MappingTarget HumanBeing existing);

    List<HumanBeingResponse> fromEntityList(List<HumanBeing> humanBeings);

    @Named(value = "colorConverter")
    default Color convertColor(String color){
        return (color != null &&
                Stream.of(Color.values()).anyMatch(v -> v.name().equals(color)))
                ? Color.fromValue(color) : null;
    }

    @Named(value = "moodConverter")
    default Mood convertMood(String mood){
        return (mood != null &&
                Stream.of(Mood.values()).anyMatch(m -> m.name().equals(mood)))
                ? Mood.fromValue(mood) : null;
    }

    @Named(value = "weaponTypeConverter")
    default WeaponType convertWeaponType(String weaponType) {
        return WeaponType.fromValue(weaponType);
    }
}
