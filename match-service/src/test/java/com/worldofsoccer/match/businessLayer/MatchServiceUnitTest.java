package com.worldofsoccer.match.businessLayer;

import com.worldofsoccer.match.dataAccessLayer.Match;
import com.worldofsoccer.match.dataAccessLayer.MatchIdentifier;
import com.worldofsoccer.match.dataAccessLayer.MatchRepository;
import com.worldofsoccer.match.dataAccessLayer.MatchStatus;
import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.league.LeagueServiceClient;
import com.worldofsoccer.match.domainclientLayer.location.VenueModel;
import com.worldofsoccer.match.domainclientLayer.location.VenueState;
import com.worldofsoccer.match.domainclientLayer.location.VenueServiceClient;
import com.worldofsoccer.match.domainclientLayer.teams.TeamModel;
import com.worldofsoccer.match.domainclientLayer.teams.TeamServiceClient;
import com.worldofsoccer.match.mappingLayer.MatchRequestMapper;
import com.worldofsoccer.match.mappingLayer.MatchResponseMapper;
import com.worldofsoccer.match.presentationlayer.MatchRequestModel;
import com.worldofsoccer.match.presentationlayer.MatchResponseModel;
import com.worldofsoccer.match.utils.exceptions.InvalidInputException;
import com.worldofsoccer.match.utils.exceptions.InvalidMatchDurationException;
import com.worldofsoccer.match.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration")
@ActiveProfiles("test")
class MatchServiceUnitTest {

    @Autowired
    private MatchService matchService;

    @MockitoBean
    private MatchRepository matchRepository;
    @MockitoBean
    private TeamServiceClient teamServiceClient;
    @MockitoBean
    private VenueServiceClient venueServiceClient;
    @MockitoBean
    private LeagueServiceClient leagueServiceClient;

    @MockitoSpyBean
    private MatchResponseMapper matchResponseMapper;
    @MockitoSpyBean
    private MatchRequestMapper matchRequestMapper;

    @Test
    void whenValidRequest_thenCreateMatch() {
        String leagueId = "11111111-1111-1111-1111-111111111111";
        TeamModel team = TeamModel.builder()
                .teamId("3fa85f64-5717-4562-b3fc-2c963f66afa6")
                .teamName("MUFC")
                .coach("Erik ten Hag")
                .teamFoundingYear(1878)
                .teamBudget(new BigDecimal("550000000.00"))
                .build();
        VenueModel venue = VenueModel.builder()
                .venueId("12345678-1234-1234-1234-123456789012")
                .venueName("Old Trafford")
                .venueCity("Manchester")
                .venueCapacity(76000)
                .venueState(VenueState.UPCOMING.name())
                .build();
        LeagueModel league = LeagueModel.builder()
                .leagueId(leagueId)
                .leagueName("Premier League")
                .leagueFormat("Round Robin")
                .build();

        MatchRequestModel req = MatchRequestModel.builder()
                .teamId(team.getTeamId())
                .venueId(venue.getVenueId())
                .leagueId(leagueId)
                .matchScore("1-0")
                .matchStatus(MatchStatus.SCHEDULED)
                .matchTime(LocalTime.of(15, 0))
                .matchDate(LocalDate.of(2025, 5, 10))
                .matchDuration(LocalTime.of(1, 30))
                .resultsType(null)
                .matchMinute(null)
                .build();

        when(teamServiceClient.getTeamById(team.getTeamId())).thenReturn(team);
        when(venueServiceClient.getVenueById(venue.getVenueId())).thenReturn(venue);
        when(leagueServiceClient.getLeagueById(leagueId)).thenReturn(league);
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.getMatchIdentifier().getMatchId();
            return m;
        });

        // act
        MatchResponseModel res = matchService.createMatch(req, leagueId);

        // assert
        assertNotNull(res.getMatchId());
        assertEquals(team.getTeamId(), res.getTeamId());
        assertEquals(venue.getVenueId(), res.getVenueId());
        assertEquals(leagueId, res.getLeagueId());
        assertEquals("1-0", res.getMatchScore());
        verify(matchResponseMapper, times(1)).entityToResponseModel(any(Match.class));
    }

    @Test
    void whenTeamNotFound_thenThrow() {
        String leagueId = UUID.randomUUID().toString();
        MatchRequestModel req = new MatchRequestModel();
        req.setTeamId("bad");
        req.setVenueId("v");
        req.setLeagueId(leagueId);
        req.setMatchDuration(LocalTime.of(2,0));
        when(leagueServiceClient.getLeagueById(leagueId)).thenReturn(new LeagueModel());
        when(teamServiceClient.getTeamById("bad")).thenReturn(null);
        assertThrows(InvalidInputException.class, () ->
                matchService.createMatch(req, leagueId));
    }

    @Test
    void whenDurationInvalid_thenThrow() {
        String leagueId = UUID.randomUUID().toString();
        MatchRequestModel req = new MatchRequestModel();
        req.setTeamId("t");
        req.setVenueId("v");
        req.setLeagueId(leagueId);
        req.setMatchDuration(LocalTime.of(0,59));
        when(leagueServiceClient.getLeagueById(leagueId)).thenReturn(new LeagueModel());
        when(teamServiceClient.getTeamById(any())).thenReturn(new TeamModel());
        when(venueServiceClient.getVenueById(any())).thenReturn(new VenueModel());
        assertThrows(InvalidMatchDurationException.class, () ->
                matchService.createMatch(req, leagueId));
    }

    @Test
    void whenVenueNotAvailable_thenThrow() {
        String leagueId = UUID.randomUUID().toString();
        MatchRequestModel req = new MatchRequestModel();
        req.setTeamId("t");
        req.setVenueId("v");
        req.setLeagueId(leagueId);
        req.setMatchDuration(LocalTime.of(2,0));
        when(leagueServiceClient.getLeagueById(leagueId)).thenReturn(new LeagueModel());
        when(teamServiceClient.getTeamById(any())).thenReturn(new TeamModel());
        VenueModel venue = new VenueModel();
        venue.setVenueState(VenueState.LIVE.name());
        when(venueServiceClient.getVenueById(any())).thenReturn(venue);
        assertThrows(InvalidInputException.class, () ->
                matchService.createMatch(req, leagueId));
    }

    @Test
    void whenLeagueNotFound_thenThrow() {
        String leagueId = "nope";
        MatchRequestModel req = new MatchRequestModel();
        req.setMatchDuration(LocalTime.of(2,0));
        when(leagueServiceClient.getLeagueById(leagueId)).thenReturn(null);
        assertThrows(NotFoundException.class, () ->
                matchService.createMatch(req, leagueId));
    }

    @Test
    void whenLeagueNotFound_getMatchByMatchId_throwsNotFound() {
        when(leagueServiceClient.getLeagueById("L")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> matchService.getMatchByMatchId("L", "M"));
    }

    @Test
    void whenMatchNotFound_getMatchByMatchId_throwsNotFound() {
        String L = "11111111-1111-1111-1111-111111111111";

        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, "M"))
                .thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> matchService.getMatchByMatchId(L, "M"));
    }

    @Test
    void whenValid_getMatchByMatchId_returnsModel() {
        String L = "11111111-1111-1111-1111-111111111111";

        Match stored = Match.builder()
                .matchIdentifier(new MatchIdentifier())
                .build();
        String M = stored.getMatchIdentifier().getMatchId();

        MatchResponseModel dto = new MatchResponseModel();
        dto.setMatchId(M);

        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, M))
                .thenReturn(stored);

        doReturn(dto)
                .when(matchResponseMapper)
                .entityToResponseModel(stored);

        MatchResponseModel result = matchService.getMatchByMatchId(L, M);

        assertNotNull(result);
        assertEquals(M, result.getMatchId());
        verify(matchResponseMapper).entityToResponseModel(stored);
    }

    @Test
    void whenLeagueNotFound_updateMatch_throwsNotFound() {
        when(leagueServiceClient.getLeagueById("L")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> matchService.updateMatch("M", new MatchRequestModel(), "L"));
    }

    @Test
    void whenMatchNotFound_updateMatch_throwsNotFound() {
        when(leagueServiceClient.getLeagueById("L")).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId("L", "M"))
                .thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> matchService.updateMatch("M", new MatchRequestModel(), "L"));
    }

    @Test
    void whenDurationInvalid_updateMatch_throwsInvalidMatchDuration() {
        String L = "11111111-1111-1111-1111-111111111111";
        MatchRequestModel req = new MatchRequestModel();
        req.setMatchDuration(LocalTime.of(1, 0)); // too short

        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, "M"))
                .thenReturn(new Match());

        assertThrows(InvalidMatchDurationException.class,
                () -> matchService.updateMatch("M", req, L));
    }
    @Test
    void whenMatchCompleted_updateMatch_throwsInvalidInput() {
        String L = "11111111-1111-1111-1111-111111111111";
        String M = "22222222-2222-2222-2222-222222222222";

        Match existing = new Match();
        existing.setMatchStatus(MatchStatus.COMPLETED);
        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, M))
                .thenReturn(existing);

        MatchRequestModel req = new MatchRequestModel();
        req.setMatchDuration(LocalTime.of(2, 0));

        assertThrows(InvalidInputException.class,
                () -> matchService.updateMatch(M, req, L));
    }


    @Test
    void whenVenueChanged_updateMatch_patchesOldAndNewVenue() {
        String L = "11111111-1111-1111-1111-111111111111";

        // existing match with old venue and scheduled status
        Match existing = Match.builder()
                .matchIdentifier(new MatchIdentifier())
                .venueModel(VenueModel.builder().venueId("oldV").build())
                .matchStatus(MatchStatus.SCHEDULED)
                .build();
        String M = existing.getMatchIdentifier().getMatchId();

        MatchRequestModel req = new MatchRequestModel();
        req.setMatchDuration(LocalTime.of(1, 30));
        req.setMatchStatus(MatchStatus.IN_PROGRESS);
        req.setVenueId("newV");
        req.setTeamId("t1");

        VenueModel newVenue = VenueModel.builder()
                .venueId("newV")
                .venueState(VenueState.UPCOMING.name())
                .build();

        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, M))
                .thenReturn(existing);
        when(venueServiceClient.getVenueById("newV")).thenReturn(newVenue);
        when(teamServiceClient.getTeamById("t1")).thenReturn(new TeamModel());
        when(matchRequestMapper.requestModelToEntity(any(), eq(existing.getMatchIdentifier()), any(), any(), any()))
                .thenReturn(existing);
        when(matchRepository.save(existing)).thenReturn(existing);

        // stub the mapper so we never invoke real code
        doReturn(new MatchResponseModel())
                .when(matchResponseMapper)
                .entityToResponseModel(existing);

        matchService.updateMatch(M, req, L);

        verify(venueServiceClient).patchVenueState("oldV", MatchStatus.CANCELED);
        verify(venueServiceClient).patchVenueState("newV", MatchStatus.IN_PROGRESS);
    }
    @Test
    void whenValid_updateMatch_returnsResponse() {
        String L = "11111111-1111-1111-1111-111111111111";

        Match existing = Match.builder()
                .matchIdentifier(new MatchIdentifier())
                .venueModel(VenueModel.builder().venueId("v").build())
                .matchStatus(MatchStatus.SCHEDULED)
                .build();
        String M = existing.getMatchIdentifier().getMatchId();

        MatchRequestModel req = new MatchRequestModel();
        req.setMatchDuration(LocalTime.of(1, 45));
        req.setMatchStatus(MatchStatus.SCHEDULED);
        req.setVenueId("v");
        req.setTeamId("t");

        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, M))
                .thenReturn(existing);
        when(teamServiceClient.getTeamById("t")).thenReturn(new TeamModel());
        when(venueServiceClient.getVenueById("v")).thenReturn(VenueModel.builder()
                .venueId("v")
                .venueState(VenueState.UPCOMING.name())
                .build());
        when(matchRequestMapper.requestModelToEntity(any(), eq(existing.getMatchIdentifier()), any(), any(), any()))
                .thenReturn(existing);
        when(matchRepository.save(existing)).thenReturn(existing);

        // stub mapper
        doReturn(new MatchResponseModel())
                .when(matchResponseMapper)
                .entityToResponseModel(existing);

        MatchResponseModel res = matchService.updateMatch(M, req, L);
        assertNotNull(res);
        verify(matchResponseMapper).entityToResponseModel(existing);
    }

    @Test
    void whenLeagueNotFound_deleteMatch_throwsNotFound() {
        when(leagueServiceClient.getLeagueById("L")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> matchService.deleteMatch("M", "L"));
    }

    @Test
    void whenMatchNotFound_deleteMatch_throwsNotFound() {
        when(leagueServiceClient.getLeagueById("L")).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId("L", "M"))
                .thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> matchService.deleteMatch("M", "L"));
    }

    @Test
    void whenInProgress_deleteMatch_throwsInvalidInput() {
        Match inProg = new Match();
        inProg.setMatchStatus(MatchStatus.IN_PROGRESS);

        when(leagueServiceClient.getLeagueById("L")).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId("L", "M"))
                .thenReturn(inProg);

        assertThrows(InvalidInputException.class,
                () -> matchService.deleteMatch("M", "L"));
    }

    @Test
    void whenValid_deleteMatch_patchesVenueAndDeletes() {
        Match toDelete = new Match();
        toDelete.setMatchStatus(MatchStatus.SCHEDULED);
        toDelete.setVenueModel(VenueModel.builder().venueId("V1").build());

        when(leagueServiceClient.getLeagueById("L")).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId("L", "M"))
                .thenReturn(toDelete);

        matchService.deleteMatch("M", "L");

        verify(venueServiceClient).patchVenueState("V1", MatchStatus.CANCELED);
        verify(matchRepository).delete(toDelete);
    }

    @Test
    void whenNewVenueNull_updateMatch_throwsInvalidInput() {
        String L = "11111111-1111-1111-1111-111111111111";
        String M = "22222222-2222-2222-2222-222222222222";

        Match existing = Match.builder()
                .matchIdentifier(new MatchIdentifier())
                .venueModel(VenueModel.builder().venueId("oldV").build())
                .matchStatus(MatchStatus.SCHEDULED)
                .build();

        MatchRequestModel req = new MatchRequestModel();
        req.setMatchDuration(LocalTime.of(1, 30));
        req.setMatchStatus(MatchStatus.SCHEDULED);
        req.setVenueId("nonexistentV");
        req.setTeamId("t1");

        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, M))
                .thenReturn(existing);
        when(venueServiceClient.getVenueById("nonexistentV")).thenReturn(null);

        InvalidInputException ex = assertThrows(
                InvalidInputException.class,
                () -> matchService.updateMatch(M, req, L)
        );
        assertEquals("Venue not found with ID: nonexistentV", ex.getMessage());
    }

    @Test
    void whenNewVenueNotAvailable_updateMatch_throwsInvalidInput() {
        String L = "11111111-1111-1111-1111-111111111111";
        String M = "22222222-2222-2222-2222-222222222222";

        Match existing = Match.builder()
                .matchIdentifier(new MatchIdentifier())
                .venueModel(VenueModel.builder().venueId("oldV").build())
                .matchStatus(MatchStatus.SCHEDULED)
                .build();

        MatchRequestModel req = new MatchRequestModel();
        req.setMatchDuration(LocalTime.of(1, 30));
        req.setMatchStatus(MatchStatus.SCHEDULED);
        req.setVenueId("busyV");
        req.setTeamId("t1");

        VenueModel busyVenue = VenueModel.builder()
                .venueId("busyV")
                .venueState(VenueState.LIVE.name())
                .build();

        when(leagueServiceClient.getLeagueById(L)).thenReturn(new LeagueModel());
        when(matchRepository
                .findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(L, M))
                .thenReturn(existing);
        when(venueServiceClient.getVenueById("busyV")).thenReturn(busyVenue);

        InvalidInputException ex = assertThrows(
                InvalidInputException.class,
                () -> matchService.updateMatch(M, req, L)
        );
        assertEquals("New venue busyV is not available", ex.getMessage());
    }


}
