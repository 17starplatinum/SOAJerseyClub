package ru.itmo.cs.parsifal.heromodule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.cs.parsifal.heromodule.model.HumanBeingFullResponse;
import ru.itmo.cs.parsifal.heromodule.service.HumanBeingServiceClient;
import ru.itmo.cs.parsifal.heromodule.service.TeamService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@RequestMapping("/api/v1/heroes")
@RequiredArgsConstructor
public class HeroController {

    private final HumanBeingServiceClient humanBeingServiceClient;
    private final TeamService teamService;

    @GetMapping("/search/{real-hero-only}")
    public ResponseEntity<List<HumanBeingFullResponse>> searchHeroes(
            @PathVariable("real-hero-only") @NotNull Boolean realHeroOnly) {

        List<HumanBeingFullResponse> heroes = humanBeingServiceClient.getHumanBeings(realHeroOnly);
        return ResponseEntity.ok(heroes);
    }

    @PatchMapping("/team/{team-id}/car/add")
    public ResponseEntity<List<HumanBeingFullResponse>> assignCarsToTeam(
            @PathVariable("team-id") @NotNull @Positive Long teamId) {

        List<HumanBeingFullResponse> updatedHumans = teamService.assignCarsToTeam(teamId);
        return ResponseEntity.ok(updatedHumans);
    }
}