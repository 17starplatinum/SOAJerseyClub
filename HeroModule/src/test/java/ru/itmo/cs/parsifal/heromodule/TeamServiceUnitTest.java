package ru.itmo.cs.parsifal.heromodule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import ru.itmo.cs.parsifal.heromodule.model.HumanBeingFullResponse;
import ru.itmo.cs.parsifal.heromodule.model.Team;
import ru.itmo.cs.parsifal.heromodule.model.TeamPaginatedResponse;
import ru.itmo.cs.parsifal.heromodule.model.TeamResponse;
import ru.itmo.cs.parsifal.heromodule.repository.TeamRepository;
import ru.itmo.cs.parsifal.heromodule.service.HumanBeingServiceClient;
import ru.itmo.cs.parsifal.heromodule.service.TeamService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceUnitTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private HumanBeingServiceClient humanBeingServiceClient;

    @InjectMocks
    private TeamService teamService;

    // получение команды по корректному ID
    @Test
    void whenGetTeamById_thenReturnTeam() {
        Team team = new Team();
        team.setId(1L);
        team.setName("Test Team");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of());

        TeamResponse response = teamService.getTeamById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Team", response.getName());
        assertEquals(0, response.getSize());
    }

    // фильтрация команд по размеру (in-memory обработка)
    @Test
    void whenGetTeamsWithSizeFilter_thenApplyInMemoryProcessing() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team B");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team1, team2));

        HumanBeingFullResponse human1 = new HumanBeingFullResponse();
        human1.setId(1L);
        HumanBeingFullResponse human2 = new HumanBeingFullResponse();
        human2.setId(2L);
        HumanBeingFullResponse human3 = new HumanBeingFullResponse();
        human3.setId(3L);

        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of(human1, human2));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(2L)).thenReturn(List.of(human3));

        List<String> filter = List.of("size[eq]=2");
        TeamPaginatedResponse response = teamService.getTeams(null, filter, null, null);

        assertEquals(1, response.getTeams().size());
        assertEquals("Team A", response.getTeams().get(0).getName());
        assertEquals(2, response.getTeams().get(0).getSize());
    }

    // сортировка команд по размеру (in-memory сортировка)
    @Test
    void whenGetTeamsWithSizeSort_thenApplyInMemorySorting() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team Small");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team Large");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team1, team2));

        HumanBeingFullResponse human1 = new HumanBeingFullResponse();
        human1.setId(1L);

        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of(human1));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(2L)).thenReturn(List.of(human1, human1, human1));

        List<String> sort = List.of("-size");
        TeamPaginatedResponse response = teamService.getTeams(sort, null, null, null);

        assertEquals(2, response.getTeams().size());
        assertEquals("Team Large", response.getTeams().get(0).getName());
        assertEquals(3, response.getTeams().get(0).getSize());
        assertEquals("Team Small", response.getTeams().get(1).getName());
        assertEquals(1, response.getTeams().get(1).getSize());
    }

    // сортировка команд по имени
    @Test
    void whenGetTeamsWithDatabaseSort_thenUseDatabaseSorting() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Beta Team");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Alpha Team");

        Page<Team> teamPage = new PageImpl<>(List.of(team2, team1));
        when(teamRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(teamPage);

        when(humanBeingServiceClient.getHumanBeingsByTeamId(anyLong())).thenReturn(List.of());

        List<String> sort = List.of("name");
        TeamPaginatedResponse response = teamService.getTeams(sort, null, 1, 10);

        assertEquals(2, response.getTeams().size());
        assertEquals("Alpha Team", response.getTeams().get(0).getName());
        assertEquals("Beta Team", response.getTeams().get(1).getName());
    }

    // фильтрация команд по имени
    @Test
    void whenGetTeamsWithNameFilter_thenApplyDatabaseFiltering() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Alpha Team");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Beta Team");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team1));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of());

        List<String> filter = List.of("name[eq]=Alpha Team");
        TeamPaginatedResponse response = teamService.getTeams(null, filter, null, null);

        assertEquals(1, response.getTeams().size());
        assertEquals("Alpha Team", response.getTeams().get(0).getName());
    }

    @Test
    void whenGetTeamsWithNameLikeFilter_thenApplyDatabaseFiltering() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Alpha Team");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team1));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of());

        List<String> filter = List.of("name[like]=lph");
        TeamPaginatedResponse response = teamService.getTeams(null, filter, null, null);

        assertEquals(1, response.getTeams().size());
        assertEquals("Alpha Team", response.getTeams().get(0).getName());
    }

    @Test
    void whenGetTeamsWithNameNotEqualFilter_thenApplyDatabaseFiltering() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Beta Team");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team1));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of());

        List<String> filter = List.of("name[neq]=Alpha Team");
        TeamPaginatedResponse response = teamService.getTeams(null, filter, null, null);

        assertEquals(1, response.getTeams().size());
        assertEquals("Beta Team", response.getTeams().get(0).getName());
    }

    // фильтрация команд по id
    @Test
    void whenGetTeamsWithIdFilter_thenApplyDatabaseFiltering() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team1));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of());

        List<String> filter = List.of("id[eq]=1");
        TeamPaginatedResponse response = teamService.getTeams(null, filter, null, null);

        assertEquals(1, response.getTeams().size());
        assertEquals(1L, response.getTeams().get(0).getId());
    }

    @Test
    void whenGetTeamsWithIdGreaterThanFilter_thenApplyDatabaseFiltering() {
        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team B");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team2));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(2L)).thenReturn(List.of());

        List<String> filter = List.of("id[gt]=1");
        TeamPaginatedResponse response = teamService.getTeams(null, filter, null, null);

        assertEquals(1, response.getTeams().size());
        assertEquals(2L, response.getTeams().get(0).getId());
    }

    @Test
    void whenGetTeamsWithIdLessThanFilter_thenApplyDatabaseFiltering() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");

        when(teamRepository.findAll(any(Specification.class))).thenReturn(List.of(team1));
        when(humanBeingServiceClient.getHumanBeingsByTeamId(1L)).thenReturn(List.of());

        List<String> filter = List.of("id[lt]=2");
        TeamPaginatedResponse response = teamService.getTeams(null, filter, null, null);

        assertEquals(1, response.getTeams().size());
        assertEquals(1L, response.getTeams().get(0).getId());
    }

    // сортировка команд по id
    @Test
    void whenGetTeamsWithIdSortAsc_thenApplyDatabaseSorting() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team B");

        Page<Team> teamPage = new PageImpl<>(List.of(team1, team2));
        when(teamRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(teamPage);
        when(humanBeingServiceClient.getHumanBeingsByTeamId(anyLong())).thenReturn(List.of());

        List<String> sort = List.of("id");
        TeamPaginatedResponse response = teamService.getTeams(sort, null, 1, 10);

        assertEquals(2, response.getTeams().size());
        assertEquals(1L, response.getTeams().get(0).getId());
        assertEquals(2L, response.getTeams().get(1).getId());
    }

    @Test
    void whenGetTeamsWithIdSortDesc_thenApplyDatabaseSorting() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team B");

        Page<Team> teamPage = new PageImpl<>(List.of(team2, team1));
        when(teamRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(teamPage);
        when(humanBeingServiceClient.getHumanBeingsByTeamId(anyLong())).thenReturn(List.of());

        List<String> sort = List.of("-id");
        TeamPaginatedResponse response = teamService.getTeams(sort, null, 1, 10);

        assertEquals(2, response.getTeams().size());
        assertEquals(2L, response.getTeams().get(0).getId());
        assertEquals(1L, response.getTeams().get(1).getId());
    }

    // Тест для комбинированной сортировки (имя и ID)
    @Test
    void whenGetTeamsWithMultipleSort_thenApplyDatabaseSorting() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team A");

        Page<Team> teamPage = new PageImpl<>(List.of(team1, team2));
        when(teamRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(teamPage);
        when(humanBeingServiceClient.getHumanBeingsByTeamId(anyLong())).thenReturn(List.of());

        List<String> sort = List.of("name", "id");
        TeamPaginatedResponse response = teamService.getTeams(sort, null, 1, 10);

        assertEquals(2, response.getTeams().size());
        assertEquals(1L, response.getTeams().get(0).getId());
        assertEquals(2L, response.getTeams().get(1).getId());
    }

    // Тест для проверки конфликтующих направлений сортировки
    @Test
    void whenGetTeamsWithConflictingSort_thenThrowException() {
        List<String> sort = List.of("name", "-name");

        assertThrows(IllegalArgumentException.class, () ->
                teamService.getTeams(sort, null, 1, 10));
    }
}