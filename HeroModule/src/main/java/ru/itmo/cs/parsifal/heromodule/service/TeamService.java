package ru.itmo.cs.parsifal.heromodule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itmo.cs.parsifal.heromodule.exception.NotFoundException;
import ru.itmo.cs.parsifal.heromodule.exception.ValidationException;
import ru.itmo.cs.parsifal.heromodule.model.*;
import ru.itmo.cs.parsifal.heromodule.repository.TeamRepository;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final HumanBeingServiceClient humanBeingServiceClient;

    public TeamPaginatedResponse getTeams(List<String> sort, List<String> filter, Integer page, Integer pageSize) {
        ParsedFilters parsed = parseFilters(filter);
        boolean sortBySize = hasSizeSorting(sort);
        boolean needInMemoryProcessing = sortBySize || !parsed.sizeFilters.isEmpty();

        if (!needInMemoryProcessing) {
            Specification<Team> specification = buildSpecification(parsed.dbFilters);
            Pageable pageable = buildPageable(sort, page, pageSize);
            if (pageable != null && pageable.isPaged()) {
                Page<Team> teamPage = teamRepository.findAll(specification, pageable);
                return convertToPaginatedResponse(teamPage);
            } else {
                List<Team> teams = teamRepository.findAll(specification);
                return convertToPaginatedResponse(teams);
            }
        } else {
            Specification<Team> dbSpec = buildSpecification(parsed.dbFilters);
            List<Team> baseTeams = teamRepository.findAll(dbSpec);

            List<TeamResponse> enriched = baseTeams.stream()
                    .map(this::convertToResponse)
                    .toList();

            List<TeamResponse> afterSizeFilter = applySizeFilters(enriched, parsed.sizeFilters);

            List<TeamResponse> sorted = applyInMemorySorting(afterSizeFilter, sort);

            return paginate(sorted, page, pageSize);
        }
    }

    public TeamResponse getTeamById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Team ID must be a positive integer");
        }
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team not found with id=" + id));
        return convertToResponse(team);
    }

    public TeamResponse createTeam(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        Team savedTeam = teamRepository.save(team);
        return convertToResponse(savedTeam);
    }

    public TeamResponse updateTeam(Long id, TeamDTO teamDTO) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team not found with id=" + id));
        team.setName(teamDTO.getName());
        Team updatedTeam = teamRepository.save(team);
        return convertToResponse(updatedTeam);
    }

    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team not found with id=" + id));

        try {
            List<HumanBeingFullResponse> teamMembers = humanBeingServiceClient.getHumanBeingsByTeamId(id);
            for (HumanBeingFullResponse human : teamMembers) {
                human.setTeamId(null);
                humanBeingServiceClient.updateHumanBeing(human.getId(), human);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Upstream service unavailable: " + e.getMessage(), e);
        }

        teamRepository.delete(team);
    }

    public List<HumanBeingFullResponse> manipulateTeamMembers(TeamPatchRequest request) {
        if (request.getOperation() == OperationType.ADD || request.getOperation() == OperationType.TRANSFER) {
            if (request.getTeamId() == null) {
                throw new ValidationException("Team ID is required for ADD and TRANSFER operations");
            }
            if (request.getTeamId() <= 0) {
                throw new ValidationException("Team ID must be a positive integer");
            }
        }

        if (request.getOperation() == OperationType.REMOVE && request.getTeamId() != null) {
            throw new ValidationException("Team ID must be null for REMOVE operation");
        }

        if (request.getHumanId() == null || request.getHumanId() <= 0) {
            throw new ValidationException("Human ID must be a positive integer");
        }

        HumanBeingFullResponse human = humanBeingServiceClient.getHumanBeings(null).stream()
                .filter(h -> h.getId().equals(request.getHumanId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Human being not found with id=" + request.getHumanId()));

        switch (request.getOperation()) {
            case ADD:
            case TRANSFER:
                if (request.getTeamId() == null) {
                    throw new RuntimeException("Team ID is required for ADD and TRANSFER operations");
                }
                teamRepository.findById(request.getTeamId())
                        .orElseThrow(() -> new RuntimeException("Team not found with id=" + request.getTeamId()));
                human.setTeamId(request.getTeamId());
                break;
            case REMOVE:
                human.setTeamId(null);
                break;
        }

        HumanBeingFullResponse updatedHuman = humanBeingServiceClient.updateHumanBeing(human.getId(), human);
        return List.of(updatedHuman);
    }

    public List<HumanBeingFullResponse> assignCarsToTeam(Long teamId) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found with id=" + teamId));

        List<HumanBeingFullResponse> teamMembers = humanBeingServiceClient.getHumanBeingsByTeamId(teamId);
        List<HumanBeingFullResponse> updatedHumans = new ArrayList<>();

        for (HumanBeingFullResponse human : teamMembers) {
            if (human.getCar() == null) {
                Car redLada = new Car(true, Color.RED, "Lada Kalina");
                human.setCar(redLada);
                HumanBeingFullResponse updatedHuman = humanBeingServiceClient.updateHumanBeing(human.getId(), human);
                updatedHumans.add(updatedHuman);
            }
        }

        return updatedHumans;
    }

    private Specification<Team> buildSpecification(List<String> filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null || filter.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (String filterItem : filter) {
                String[] parts = filterItem.split("\\[|]=");
                if (parts.length != 3) {
                    throw new RuntimeException("Invalid filter format: " + filterItem);
                }

                String field = parts[0];
                String operator = parts[1];
                String value = parts[2];

                switch (field) {
                    case "name":
                        predicates.add(buildStringPredicate(root, criteriaBuilder, field, operator, value));
                        break;
                    case "id":
                        predicates.add(buildNumericPredicate(root, criteriaBuilder, field, operator, value));
                        break;
                    case "size":
                        break;
                    default:
                        throw new RuntimeException("Unsupported filter field: " + field);
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private jakarta.persistence.criteria.Predicate buildStringPredicate(
            jakarta.persistence.criteria.Root<Team> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String field, String operator, String value) {
        return switch (operator) {
            case "eq" -> cb.equal(root.get(field), value);
            case "neq" -> cb.notEqual(root.get(field), value);
            case "like" -> cb.like(root.get(field), "%" + value + "%");
            default -> throw new RuntimeException("Unsupported operator for string field: " + operator);
        };
    }

    private jakarta.persistence.criteria.Predicate buildNumericPredicate(
            jakarta.persistence.criteria.Root<Team> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String field, String operator, String value) {
        Number numberValue = Long.parseLong(value);

        return switch (operator) {
            case "eq" -> cb.equal(root.get(field), numberValue);
            case "neq" -> cb.notEqual(root.get(field), numberValue);
            case "gt" -> cb.gt(root.get(field), numberValue.longValue());
            case "lt" -> cb.lt(root.get(field), numberValue.longValue());
            case "gte" -> cb.ge(root.get(field), numberValue.longValue());
            case "lte" -> cb.le(root.get(field), numberValue.longValue());
            case "like" -> cb.like(root.get(field).as(String.class), "%" + value + "%");
            default -> throw new RuntimeException("Unsupported operator for numeric field: " + operator);
        };
    }

    private Pageable buildPageable(List<String> sort, Integer page, Integer pageSize) {
        if (page == null && pageSize == null) {
            return Pageable.unpaged();
        }

        int pageNumber = page != null ? page - 1 : 0;
        int size = pageSize != null ? pageSize : 10;

        if (sort != null && !sort.isEmpty()) {
            Set<String> ascendingFields = new HashSet<>();
            Set<String> descendingFields = new HashSet<>();

            for (String sortField : sort) {
                String fieldName = sortField.startsWith("-") ? sortField.substring(1) : sortField;
                if ("size".equals(fieldName)) {
                    return PageRequest.of(pageNumber, size);
                }

                if (sortField.startsWith("-")) {
                    if (ascendingFields.contains(fieldName)) {
                        throw new IllegalArgumentException(
                                "Conflicting sort directions for field: " + fieldName
                        );
                    }
                    descendingFields.add(fieldName);
                } else {
                    if (descendingFields.contains(fieldName)) {
                        throw new IllegalArgumentException(
                                "Conflicting sort directions for field: " + fieldName
                        );
                    }
                    ascendingFields.add(fieldName);
                }
            }

            List<Sort.Order> orders = new ArrayList<>();
            for (String sortField : sort) {
                if (sortField.startsWith("-")) {
                    orders.add(Sort.Order.desc(sortField.substring(1)));
                } else {
                    orders.add(Sort.Order.asc(sortField));
                }
            }
            return PageRequest.of(pageNumber, size, Sort.by(orders));
        }

        return PageRequest.of(pageNumber, size);
    }

    private TeamPaginatedResponse convertToPaginatedResponse(Page<Team> page) {
        List<TeamResponse> teams = page.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new TeamPaginatedResponse(
                teams,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                (int) page.getTotalElements()
        );
    }

    private TeamPaginatedResponse convertToPaginatedResponse(List<Team> teams) {
        List<TeamResponse> teamResponses = teams.stream()
                .map(this::convertToResponse)
                .toList();

        return new TeamPaginatedResponse(teamResponses, null, null, null, null);
    }

    private TeamResponse convertToResponse(Team team) {
        Integer size;
        try {
            List<HumanBeingFullResponse> members = humanBeingServiceClient.getHumanBeingsByTeamId(team.getId());
            size = members != null ? members.size() : 0;
        } catch (Exception e) {
            size = null;
        }
        return new TeamResponse(team.getId(), team.getName(), size);
    }

    private static class ParsedFilters {
        List<String> dbFilters = new ArrayList<>();
        List<String> sizeFilters = new ArrayList<>();
    }

    private ParsedFilters parseFilters(List<String> filters) {
        ParsedFilters parsed = new ParsedFilters();
        if (filters == null) return parsed;

        for (String filterItem : filters) {
            String[] parts = filterItem.split("\\[|]=");
            if (parts.length != 3) {
                throw new RuntimeException("Invalid filter format: " + filterItem);
            }
            String field = parts[0];
            if ("size".equals(field)) {
                parsed.sizeFilters.add(filterItem);
            } else {
                parsed.dbFilters.add(filterItem);
            }
        }
        return parsed;
    }

    private boolean hasSizeSorting(List<String> sort) {
        if (sort == null) return false;
        for (String s : sort) {
            String fieldName = s.startsWith("-") ? s.substring(1) : s;
            if ("size".equals(fieldName)) return true;
        }
        return false;
    }

    private List<TeamResponse> applySizeFilters(List<TeamResponse> teams, List<String> sizeFilters) {
        if (sizeFilters == null || sizeFilters.isEmpty()) return teams;

        List<TeamResponse> result = new ArrayList<>(teams);
        for (String filterItem : sizeFilters) {
            String[] parts = filterItem.split("\\[|]=");
            if (parts.length != 3) {
                throw new RuntimeException("Invalid filter format: " + filterItem);
            }
            String operator = parts[1];
            String valueStr = parts[2];

            switch (operator) {
                case "eq" -> {
                    long v = Long.parseLong(valueStr);
                    result = result.stream()
                            .filter(t -> t.getSize() != null && t.getSize().longValue() == v)
                            .toList();
                }
                case "neq" -> {
                    long v = Long.parseLong(valueStr);
                    result = result.stream()
                            .filter(t -> t.getSize() == null || t.getSize().longValue() != v)
                            .toList();
                }
                case "gt" -> {
                    long v = Long.parseLong(valueStr);
                    result = result.stream()
                            .filter(t -> t.getSize() != null && t.getSize() > v)
                            .toList();
                }
                case "lt" -> {
                    long v = Long.parseLong(valueStr);
                    result = result.stream()
                            .filter(t -> t.getSize() != null && t.getSize() < v)
                            .toList();
                }
                case "gte" -> {
                    long v = Long.parseLong(valueStr);
                    result = result.stream()
                            .filter(t -> t.getSize() != null && t.getSize() >= v)
                            .toList();
                }
                case "lte" -> {
                    long v = Long.parseLong(valueStr);
                    result = result.stream()
                            .filter(t -> t.getSize() != null && t.getSize() <= v)
                            .toList();
                }
                case "like" -> {
                    result = result.stream()
                            .filter(t -> {
                                String s = t.getSize() == null ? null : String.valueOf(t.getSize());
                                return s != null && s.contains(valueStr);
                            })
                            .toList();
                }
                default -> throw new RuntimeException("Unsupported operator for size: " + operator);
            }
        }
        return result;
    }

    private List<TeamResponse> applyInMemorySorting(List<TeamResponse> teams, List<String> sort) {
        if (sort == null || sort.isEmpty()) return teams;

        Comparator<TeamResponse> comparator = null;

        for (String sortField : sort) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            Comparator<TeamResponse> next;
            switch (field) {
                case "size" -> {
                    next = Comparator.comparing(
                            TeamResponse::getSize,
                            Comparator.nullsFirst(Integer::compareTo)
                    );
                }
                case "name" -> {
                    next = Comparator.comparing(
                            TeamResponse::getName,
                            Comparator.nullsFirst(String::compareTo)
                    );
                }
                case "id" -> {
                    next = Comparator.comparing(
                            TeamResponse::getId,
                            Comparator.nullsFirst(Long::compareTo)
                    );
                }
                default -> throw new RuntimeException("Unsupported sort field: " + field);
            }

            if (desc) {
                next = next.reversed();
            }

            comparator = comparator == null ? next : comparator.thenComparing(next);
        }

        return teams.stream().sorted(comparator).toList();
    }

    private TeamPaginatedResponse paginate(List<TeamResponse> teams, Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            return new TeamPaginatedResponse(teams, null, null, null, teams.size());
        }
        int size = Math.max(pageSize, 1);
        int pageNumber = Math.max(page - 1, 0);

        int totalCount = teams.size();
        int totalPages = (int) Math.ceil(totalCount / (double) size);

        int fromIndex = Math.min(pageNumber * size, totalCount);
        int toIndex = Math.min(fromIndex + size, totalCount);

        List<TeamResponse> slice = teams.subList(fromIndex, toIndex);

        return new TeamPaginatedResponse(slice, pageNumber + 1, size, totalPages, totalCount);
    }
}