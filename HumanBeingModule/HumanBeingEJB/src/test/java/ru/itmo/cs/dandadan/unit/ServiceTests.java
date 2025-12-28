package ru.itmo.cs.dandadan.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.cs.dandadan.client.HeroServiceClient;
import ru.itmo.cs.dandadan.dto.response.HumanBeingResponse;
import ru.itmo.cs.dandadan.model.entity.*;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.service.api.HumanBeingService;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ServiceTests {

    @Mock
    private HumanBeingRepository humanBeingRepository;

    @Mock
    private HeroServiceClient heroServiceClient;

    @InjectMocks
    private HumanBeingService humanBeingService;

    @Test
    void whenGetByHumanBeingId_thenReturnHumanBeing() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        HumanBeing humanBeing = new HumanBeing(
                45L, "Сема Шибаев", new Coordinates(12, 56.121),
                now, true, false, 45,
                2L, WeaponType.SHOTGUN, Mood.APATHY, new Car(
                        true, Color.YELLOW, "McLaren P1"
                )
        );

        when(humanBeingRepository.getHumanBeing(45L)).thenReturn(humanBeing);
        when(heroServiceClient.manipulateHumanBeingToTeam(2L)).thenReturn(2L);

        HumanBeingResponse humanBeingResponse = humanBeingService.getHumanBeing(45L);

        assertNotNull(humanBeingResponse);
        assertEquals(45L, humanBeingResponse.getId());
        assertEquals("Сема Шибаев", humanBeingResponse.getName());
        assertEquals(12, humanBeingResponse.getCoordinates().getX());
        assertEquals(56.121, humanBeingResponse.getCoordinates().getY());
        assertEquals(now, humanBeingResponse.getCreationDate());
        assertTrue(humanBeingResponse.isRealHero());
        assertFalse(humanBeingResponse.isHasToothpick());
        assertEquals(45, humanBeingResponse.getImpactSpeed());
        assertEquals(WeaponType.SHOTGUN, humanBeingResponse.getWeaponType());
        assertEquals(Mood.APATHY, humanBeingResponse.getMood());
        assertTrue(humanBeingResponse.getCar().getCool());
        assertEquals(Color.YELLOW, humanBeingResponse.getCar().getColor());
        assertEquals("McLaren P1", humanBeingResponse.getCar().getModel());
    }

/*    @Test
    void whenGetHumanBeingsWithNameFilter_thenApplyInMemoryProcessing() {
        ZonedDateTime now = ZonedDateTime.now();
        HumanBeing human1 = new HumanBeing(
                1L, "Coralie", new Coordinates(
                        120, -123968.82
                ), now,
                false, true, 34, null, WeaponType.AXE, Mood.APATHY,
                new Car (
                        false, Color.BLUE, "Ford Transit"
                )
        );
        HumanBeing human2 = new HumanBeing(
                2L, "Coralie", new Coordinates(
                120, -123968.82
        ), now,
                false, true, 34, null, WeaponType.AXE, Mood.APATHY,
                new Car (
                        false, Color.BLUE, "Ford Transit"
                )
        );
        HumanBeing human3 = new HumanBeing(
                3L, "Lysanne", new Coordinates(
                120, -123968.82
        ), now,
                false, true, 34, null, WeaponType.AXE, Mood.APATHY,
                new Car (
                        false, Color.BLUE, "Ford Transit"
                )
        );
        List<Filter> filters = new ArrayList<>();
        when(humanBeingRepository.getSortedAndFilteredPage(null, filter, 1, 10).thenReturn(List.of(team1, team2));
        List<String> filter = List.of("name[eq]=Coralie");
        Page<HumanBeingResponse> responsePage = humanBeingService.getHumanBeings(null, filter, 1, 10);
        assertNotNull(responsePage);
        assertEquals(2, responsePage.getHumanBeingGetResponseDtos().size());
        assertEquals("Coralie", responsePage.getHumanBeingGetResponseDtos().get(0).getName());
        assertEquals("Coralie", responsePage.getHumanBeingGetResponseDtos().get(1).getName());
    }

    @Test
    void whenGetTeamsWithConflictingSort_thenThrowException() {
        List<String> sort = List.of("name", "-name");

        assertThrows(CustomBadRequestException.class, () ->
                humanBeingService.getHumanBeings(sort, null, 1, 10));
    }*/
}
