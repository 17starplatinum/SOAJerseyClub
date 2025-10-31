package ru.itmo.cs.parsifal.heromodule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itmo.cs.parsifal.heromodule.model.*;
import ru.itmo.cs.parsifal.heromodule.repository.TeamRepository;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final HumanBeingServiceClient humanBeingServiceClient;

    public TeamPaginatedResponse getTeams(List<String> sort, List<String> filter, Integer page, Integer pageSize) {
        Specification<Team> specification = buildSpecification(filter);
        Pageable pageable = buildPageable(sort, page, pageSize);

        if (pageable != null) {
            Page<Team> teamPage = teamRepository.findAll(specification, pageable);
            return convertToPaginatedResponse(teamPage);
        } else {
            List<Team> teams = teamRepository.findAll(specification);
            return convertToPaginatedResponse(teams);
        }
    }

    public TeamResponse getTeamById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Team ID must be a positive integer");
        }
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id=" + id));
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
                .orElseThrow(() -> new RuntimeException("Team not found with id=" + id));
        team.setName(teamDTO.getName());
        Team updatedTeam = teamRepository.save(team);
        return convertToResponse(updatedTeam);
    }

    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id=" + id));

        List<HumanBeingFullResponse> teamMembers = humanBeingServiceClient.getHumanBeingsByTeamId(id);
        for (HumanBeingFullResponse human : teamMembers) {
            human.setTeamId(null);
            humanBeingServiceClient.updateHumanBeing(human.getId(), human);
        }

        teamRepository.delete(team);
    }

    public List<HumanBeingFullResponse> manipulateTeamMembers(TeamPatchRequest request) {
        if (request.getOperation() == OperationType.ADD || request.getOperation() == OperationType.TRANSFER) {
            if (request.getTeamId() == null) {
                throw new IllegalArgumentException("Team ID is required for ADD and TRANSFER operations");
            }
            if (request.getTeamId() <= 0) {
                throw new IllegalArgumentException("Team ID must be a positive integer");
            }
        }

        if (request.getOperation() == OperationType.REMOVE && request.getTeamId() != null) {
            throw new IllegalArgumentException("Team ID must be null for REMOVE operation");
        }

        if (request.getHumanId() == null || request.getHumanId() <= 0) {
            throw new IllegalArgumentException("Human ID must be a positive integer");
        }
        HumanBeingFullResponse human = humanBeingServiceClient.getHumanBeings(null).stream()
                .filter(h -> h.getId().equals(request.getHumanId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Human being not found with id=" + request.getHumanId()));

        Long oldTeamId = human.getTeamId();

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
                .orElseThrow(() -> new RuntimeException("Team not found with id=" + teamId));

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
                String[] parts = filterItem.split("\\[|\\]=");
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
                        throw new RuntimeException("Unsupported filter field: size (not stored in DB)");
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
                    throw new IllegalArgumentException("Sorting by size is not supported (computed field)");
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
}