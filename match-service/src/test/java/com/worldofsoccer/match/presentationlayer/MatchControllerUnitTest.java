package com.worldofsoccer.match.presentationlayer;

import com.worldofsoccer.match.businessLayer.MatchService;
import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.league.LeagueServiceClient;
import com.worldofsoccer.match.utils.exceptions.InvalidInputException;
import com.worldofsoccer.match.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class MatchControllerUnitTest {

    @Autowired
    private MatchController matchController;

    @MockitoBean
    private MatchService matchService;

    @MockitoBean
    private LeagueServiceClient leagueServiceClient;

    private static final String FOUND_LEAGUE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String FOUND_MATCH_ID  = "22222222-2222-2222-2222-222222222222";
    private static final String INVALID_LEAGUE_ID = "1234";

    @Test
    void whenGetAll_thenReturnEmpty() {
        // stub league exists
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(new LeagueModel());
        when(matchService.getAllMatches(FOUND_LEAGUE_ID)).thenReturn(Collections.emptyList());

        ResponseEntity<List<MatchResponseModel>> response =
                matchController.getAllLeagueMatches(FOUND_LEAGUE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(matchService).getAllMatches(FOUND_LEAGUE_ID);
    }

    @Test
    void whenGetAll_withInvalidLeagueId_thenThrow() {
        InvalidInputException ex = assertThrows(
                InvalidInputException.class,
                () -> matchController.getAllLeagueMatches(INVALID_LEAGUE_ID)
        );
        assertEquals("Invalid leagueId provided: " + INVALID_LEAGUE_ID, ex.getMessage());
        verifyNoInteractions(matchService);
    }

    @Test
    void whenDelete_thenNoContent() {
        // stub league exists
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(new LeagueModel());
        doNothing().when(matchService).deleteMatch(FOUND_MATCH_ID, FOUND_LEAGUE_ID);

        ResponseEntity<Void> response =
                matchController.deleteLeagueMatch(FOUND_LEAGUE_ID, FOUND_MATCH_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // <— note we now verify in the same order that the controller actually calls:
        //      deleteMatch(matchId, leagueId)
        verify(matchService).deleteMatch(FOUND_MATCH_ID, FOUND_LEAGUE_ID);
    }
    @Test
    void whenGetById_invalidIds_throwsInvalidInput() {
        assertThrows(InvalidInputException.class,
                () -> matchController.getLeagueMatchById("bad", "id-too-short"));
    }
    @Test
    void whenGetById_leagueNotFound_returns404() {
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(null);

        ResponseEntity<MatchResponseModel> resp =
                matchController.getLeagueMatchById(FOUND_LEAGUE_ID, FOUND_MATCH_ID);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void whenGetById_matchNotFound_propagatesNotFound() {
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(new LeagueModel());
        when(matchService.getMatchByMatchId(FOUND_LEAGUE_ID, FOUND_MATCH_ID))
                .thenThrow(new NotFoundException("no match"));

        assertThrows(NotFoundException.class,
                () -> matchController.getLeagueMatchById(FOUND_LEAGUE_ID, FOUND_MATCH_ID));
    }

    @Test
    void whenGetById_valid_returnsOk() {
        MatchResponseModel dto = new MatchResponseModel();
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(new LeagueModel());
        when(matchService.getMatchByMatchId(FOUND_LEAGUE_ID, FOUND_MATCH_ID))
                .thenReturn(dto);

        ResponseEntity<MatchResponseModel> resp =
                matchController.getLeagueMatchById(FOUND_LEAGUE_ID, FOUND_MATCH_ID);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
    }


    @Test
    void whenUpdate_invalidIds_throwsInvalidInput() {
        assertThrows(InvalidInputException.class,
                () -> matchController.updateLeagueMatch(new MatchRequestModel(), "bad", "x"));
    }

    @Test
    void whenUpdate_leagueNotFound_returns404() {
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(null);

        ResponseEntity<MatchResponseModel> resp =
                matchController.updateLeagueMatch(new MatchRequestModel(), FOUND_LEAGUE_ID, FOUND_MATCH_ID);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void whenUpdate_valid_returnsOk() {
        MatchResponseModel dto = new MatchResponseModel();

        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID))
                .thenReturn(new LeagueModel());

        when(matchService.updateMatch(
                eq(FOUND_MATCH_ID),
                any(MatchRequestModel.class),
                eq(FOUND_LEAGUE_ID))
        ).thenReturn(dto);

        ResponseEntity<MatchResponseModel> resp =
                matchController.updateLeagueMatch(
                        new MatchRequestModel(),
                        FOUND_LEAGUE_ID,
                        FOUND_MATCH_ID
                );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
    }


    @Test
    void whenDelete_invalidIds_throwsInvalidInput() {
        assertThrows(InvalidInputException.class,
                () -> matchController.deleteLeagueMatch("bad", "x"));
    }

    @Test
    void whenDelete_leagueNotFound_returns404() {
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(null);

        ResponseEntity<Void> resp =
                matchController.deleteLeagueMatch(FOUND_LEAGUE_ID, FOUND_MATCH_ID);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void whenDelete_valid_returnsNoContent() {
        when(leagueServiceClient.getLeagueById(FOUND_LEAGUE_ID)).thenReturn(new LeagueModel());
        doNothing().when(matchService).deleteMatch(FOUND_MATCH_ID, FOUND_LEAGUE_ID);

        ResponseEntity<Void> resp =
                matchController.deleteLeagueMatch(FOUND_LEAGUE_ID, FOUND_MATCH_ID);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(matchService).deleteMatch(FOUND_MATCH_ID, FOUND_LEAGUE_ID);
    }

}
