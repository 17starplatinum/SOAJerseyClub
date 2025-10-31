package ru.itmo.cs.dandadan.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.cs.dandadan.client.HeroServiceClient;
import ru.itmo.cs.dandadan.dto.request.HumanBeingRequest;
import ru.itmo.cs.dandadan.dto.response.Event;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.dto.response.TeamRequest;
import ru.itmo.cs.dandadan.dto.response.UniqueSpeedResponse;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.mapper.HumanBeingMapper;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.view.Filter;
import ru.itmo.cs.dandadan.model.view.FilterCode;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.model.view.Sort;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.service.api.HumanBeingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class HumanBeingServiceImpl implements HumanBeingService {
    private final Set<FilterCode> stringAllowedCodes = Set.of(FilterCode.EQ, FilterCode.NEQ, FilterCode.LIKE);
    private final Set<FilterCode> booleanAllowedCodes = Set.of(FilterCode.EQ, FilterCode.NEQ, FilterCode.GT, FilterCode.LT, FilterCode.LIKE);
    @Inject
    private HumanBeingRepository humanBeingRepository;

    @Inject
    private HumanBeingMapper humanBeingMapper;

    @Inject
    private HeroServiceClient heroServiceClient;

    @Override
    public Page<HumanBeingResponse> getHumanBeings(List<String> sortsList, List<String> filtersList, Integer page, Integer pageSize) {
        Pattern nestedNamePattern = Pattern.compile("(.*)\\.(.*)");
        Pattern filterPattern = Pattern.compile("(.*)\\[(.*)]=(.*)");

        List<Sort> sorts = new ArrayList<>();
        if(!sortsList.isEmpty()) {
            boolean containsOppositeSorts = sortsList.stream()
                    .allMatch(s1 -> sortsList.stream().allMatch(s2 -> Objects.equals(s1, "-" + s2)));
            if (containsOppositeSorts) {
                throw new CustomBadRequestException("sort", "opposite sorts are not allowed");
            }

            for (String sort : sortsList) {
                boolean desc = sort.startsWith("-");
                String sortFieldName = desc ? sort.split("-")[1] : sort;
                String nestedName = null;

                Matcher matcher = nestedNamePattern.matcher(sortFieldName);
                if (matcher.find()) {
                    String nestedField = matcher.group(2).substring(0, 1).toLowerCase() + matcher.group(2).substring(1);
                    sortFieldName = matcher.group(1);
                    nestedName = nestedField;
                }
                sorts.add(new Sort(
                        desc,
                        sortFieldName,
                        nestedName
                ));
            }
        }

        List<Filter> filters = new ArrayList<>();
        for(String filter : filtersList) {
            Matcher matcher = filterPattern.matcher(filter);
            String fieldName;
            String fieldValue;
            FilterCode filterCode;
            String nestedName = null;
            if(matcher.find()) {
                fieldName = matcher.group(1);

                Matcher nestedFieldMatcher = nestedNamePattern.matcher(fieldName);
                if(nestedFieldMatcher.find()) {
                    String nestedField = nestedFieldMatcher.group(2).substring(0, 1).toLowerCase() + nestedFieldMatcher.group(2).substring(1);
                    fieldName = nestedFieldMatcher.group(1);
                    nestedName = nestedField;
                }
                filterCode = FilterCode.fromValue(fieldName);
                handleFiltrationCases(fieldName, filterCode);
                if (Objects.equals(fieldName, "weaponType") || Objects.equals(fieldName, "mood") || Objects.equals(fieldName, "car.color")) {
                    fieldValue = matcher.group(3).toLowerCase();
                } else {
                    fieldValue = matcher.group(3);
                }

                if (fieldName == null) {
                    throw new CustomBadRequestException("filter", "field name should not be null or blank");
                }
                if (fieldValue == null) {
                    throw new CustomBadRequestException("filter", "field value should not be null or blank");
                }
                if(Objects.equals(filterCode, FilterCode.UNDEFINED)) {
                    throw new CustomBadRequestException("filter", "filter code should be one of the following: 'eq', 'neq', 'gt', 'lt', 'gte', 'lte', 'like'");
                }
                filters.add(new Filter(
                        fieldName,
                        nestedName,
                        filterCode,
                        fieldValue
                ));
            }
        }
        try {
            Page<HumanBeing> humanBeingResultPage = humanBeingRepository.getSortedAndFilteredPage(
                    sorts, filters, page, pageSize
            );
            return new Page<>(
                    humanBeingMapper.fromEntityList(humanBeingResultPage.getContent()),
                    humanBeingResultPage.getPage(),
                    humanBeingResultPage.getPageSize(),
                    humanBeingResultPage.getTotalPages(),
                    humanBeingResultPage.getTotalCount()
                    );
        } catch (NullPointerException e) {
            throw new CustomBadRequestException("An error occurred while retrieving a page. Please check the format of your query parameters");
        }
    }

    @Override
    public HumanBeingResponse getHumanBeing(Long id) {
        if (id == null || id < 1) {
            throw new CustomBadRequestException("id", "positive integer", String.valueOf(id));
        }
        return humanBeingMapper.toHumanBeingResponse(humanBeingRepository.getHumanBeing(id));
    }

    @Override
    @Transactional
    public HumanBeingResponse addHumanBeing(HumanBeingRequest requestDto) {
        HumanBeing humanBeing = humanBeingMapper.fromHumanBeingRequest(requestDto);
        Long potentialTeamId = requestDto.getTeamId();
        manipulateToTeam(humanBeing.getId(), potentialTeamId);
        humanBeing = humanBeingRepository.saveHumanBeing(humanBeing, potentialTeamId);
        return humanBeingMapper.toHumanBeingResponse(humanBeing);
    }

    @Override
    @Transactional
    public HumanBeingResponse updateHumanBeing(Long id, HumanBeingRequest requestDto) {
        if (id == null || id < 1) {
            throw new CustomBadRequestException("id", "positive integer", String.valueOf(id));
        }
        Long potentialTeamId = requestDto.getTeamId();
        Long oldTeamId = humanBeingRepository.getHumanBeing(id).getTeamId();
        HumanBeing incomingHumanBeing = humanBeingMapper.fromHumanBeingRequest(requestDto);
        manipulateToTeam(id, potentialTeamId);
        incomingHumanBeing.setTeamId(potentialTeamId);

        return humanBeingMapper.toHumanBeingResponse(humanBeingRepository.updateHumanBeing(id, incomingHumanBeing, oldTeamId));
    }

    @Override
    @Transactional
    public void deleteHumanBeing(Long id) {
        if (id == null || id < 1) {
            throw new CustomBadRequestException("id", "positive integer", String.valueOf(id));
        }
        Long oldTeamId = humanBeingRepository.getHumanBeing(id).getTeamId();
        if (oldTeamId != null) {
            manipulateToTeam(id, oldTeamId);
            humanBeingRepository.deleteHumanBeing(id);
            return;
        }

        humanBeingRepository.deleteHumanBeing(id);
    }

    @Override
    public UniqueSpeedResponse getUniqueImpactSpeeds() {
        int[] uniqueSpeeds = humanBeingRepository.getUniqueImpactSpeeds()
                .stream().mapToInt(Integer::intValue).toArray();
        return new UniqueSpeedResponse(uniqueSpeeds);
    }

    private void handleFiltrationCases(String fieldName, FilterCode filterCode) {
        if (Objects.equals(fieldName, "name") || Objects.equals(fieldName, "model")) {
            if (stringAllowedCodes.stream().noneMatch(filterCode::equals)) {
                throw new CustomBadRequestException("Invalid filter operation '" + filterCode + "': allowed operations for field '" + fieldName + "' are: 'eq', 'neq', 'like'");
            }
        }
        if (Objects.equals(fieldName, "realHero") || Objects.equals(fieldName, "hasToothpick") || Objects.equals(fieldName, "cool")) {
            if (booleanAllowedCodes.stream().noneMatch(filterCode::equals)) {
                throw new CustomBadRequestException("Invalid filter operation '" + filterCode + "': allowed operations for field '" + fieldName + "' are: 'eq', 'neq', 'gt', 'lt', 'like'");
            }
        }
    }

    private void manipulateToTeam(Long humanId, Long teamId) {
        Long.parseLong(
                heroServiceClient.manipulateHumanBeingToTeam(
                        new TeamRequest(
                                humanId, teamId, Event.ADD
                        )
                ).readEntity(String.class)
        );
    }
}
