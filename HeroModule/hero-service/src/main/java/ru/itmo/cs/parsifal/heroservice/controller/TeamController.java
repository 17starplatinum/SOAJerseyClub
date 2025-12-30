package ru.itmo.cs.parsifal.heroservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.cs.parsifal.heroservice.model.*;
import ru.itmo.cs.parsifal.heroservice.service.TeamService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/heroes")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/teams")
    public ResponseEntity<TeamPaginatedResponse> getTeams(
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) List<String> filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {

        TeamPaginatedResponse response = teamService.getTeams(sort, filter, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/teams")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        TeamResponse response = teamService.createTeam(teamDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        TeamResponse response = teamService.getTeamById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamDTO teamDTO) {
        TeamResponse response = teamService.updateTeam(id, teamDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/teams")
    public ResponseEntity<List<HumanBeingFullResponse>> manipulateTeamMembers(@Valid @RequestBody TeamPatchRequest request) {
        List<HumanBeingFullResponse> response = teamService.manipulateTeamMembers(request);
        return ResponseEntity.ok(response);
    }
}