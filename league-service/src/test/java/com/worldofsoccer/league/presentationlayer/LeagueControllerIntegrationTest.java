package com.worldofsoccer.league.presentationlayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldofsoccer.league.dataaccesslayer.FormatType;
import com.worldofsoccer.league.utils.GlobalControllerExceptionHandler;
import com.worldofsoccer.league.utils.HttpErrorInfo;
import com.worldofsoccer.league.utils.exceptions.InvalidNumberOfTeamsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data-h2.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LeagueControllerIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private GlobalControllerExceptionHandler leagueExceptionHandler;

    private final String BASE_URI_LEAGUES = "/api/v1/leagues";
    private final int UUID_LENGTH = 36;

    @BeforeEach
    public void setupDb(){
    }

    @Test
    public void whenLeaguesExist_thenReturnAllLeagues(){
        webClient.get()
                .uri(BASE_URI_LEAGUES)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(LeagueResponseModel.class)
                .value(list -> {
                    assertNotNull(list);
                    assertTrue(list.size() > 0);
                });
    }

    @Test
    public void whenValidLeagueRequest_thenCreateAndReturnLeague(){
        LeagueRequestModel newLeague = new LeagueRequestModel();
        newLeague.setName("Premier League");
        newLeague.setCountry("England");
        newLeague.setFormat(FormatType.LEAGUE);
        newLeague.setNumberOfTeams(18);
        newLeague.setLeagueDifficulty("High");
        newLeague.setSeasonYear(2021);
        newLeague.setSeasonStartDate(LocalDate.of(2021, 8, 1));
        newLeague.setSeasonEndDate(LocalDate.of(2022, 5, 15));
        newLeague.setCompetitionFormatType("HomeAway");
        newLeague.setCompetitionFormatGroupStage(false);
        newLeague.setCompetitionFormatKnockout(false);

        LeagueResponseModel response = webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(newLeague)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertNotNull(response.getLeagueId());
        assertEquals(UUID_LENGTH, response.getLeagueId().length());
        assertEquals("Premier League", response.getName());
    }

    @Test
    public void whenUpdatingLeague_thenReturnUpdatedLeague(){
        LeagueRequestModel createRequest = new LeagueRequestModel();
        createRequest.setName("Bundesliga");
        createRequest.setCountry("Germany");
        createRequest.setFormat(FormatType.LEAGUE);
        createRequest.setNumberOfTeams(18);
        createRequest.setLeagueDifficulty("High");
        createRequest.setSeasonYear(2021);
        createRequest.setSeasonStartDate(LocalDate.of(2021, 9, 1));
        createRequest.setSeasonEndDate(LocalDate.of(2022, 6, 30));
        createRequest.setCompetitionFormatType("HomeAway");
        createRequest.setCompetitionFormatGroupStage(false);
        createRequest.setCompetitionFormatKnockout(false);

        LeagueResponseModel created = webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        String leagueId = created.getLeagueId();
        assertNotNull(leagueId);

        LeagueRequestModel updateRequest = new LeagueRequestModel();
        updateRequest.setName("Updated Bundesliga");
        updateRequest.setCountry("Updated Germany");
        updateRequest.setFormat(FormatType.LEAGUE);
        updateRequest.setNumberOfTeams(18);
        updateRequest.setLeagueDifficulty("High");
        updateRequest.setSeasonYear(2021);
        updateRequest.setSeasonStartDate(LocalDate.of(2021, 9, 1));
        updateRequest.setSeasonEndDate(LocalDate.of(2022, 6, 30));
        updateRequest.setCompetitionFormatType("HomeAway");
        updateRequest.setCompetitionFormatGroupStage(false);
        updateRequest.setCompetitionFormatKnockout(false);

        LeagueResponseModel updated = webClient.put()
                .uri(BASE_URI_LEAGUES + "/" + leagueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updated);
        assertEquals("Updated Bundesliga", updated.getName());
        assertEquals("Updated Germany", updated.getCountry());
    }

    @Test
    public void whenDeletingLeague_thenLeagueIsNotFound(){
        LeagueRequestModel league = new LeagueRequestModel();
        league.setName("Eredivisie");
        league.setCountry("Netherlands");
        league.setFormat(FormatType.LEAGUE);
        league.setNumberOfTeams(18);
        league.setLeagueDifficulty("Medium");
        league.setSeasonYear(2021);
        league.setSeasonStartDate(LocalDate.of(2021, 8, 20));
        league.setSeasonEndDate(LocalDate.of(2022, 5, 10));
        league.setCompetitionFormatType("HomeAway");
        league.setCompetitionFormatGroupStage(false);
        league.setCompetitionFormatKnockout(false);

        LeagueResponseModel created = webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(league)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        String leagueId = created.getLeagueId();
        assertNotNull(leagueId);

        webClient.delete()
                .uri(BASE_URI_LEAGUES + "/" + leagueId)
                .exchange()
                .expectStatus().isNoContent();

        webClient.get()
                .uri(BASE_URI_LEAGUES + "/" + leagueId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void whenCreatingLeagueWithInvalidTeamCount_thenReturnUnprocessableEntity() {
        LeagueRequestModel invalidLeague = new LeagueRequestModel();
        invalidLeague.setName("Invalid League");
        invalidLeague.setCountry("Nowhere");
        invalidLeague.setFormat(FormatType.LEAGUE);
        invalidLeague.setNumberOfTeams(16);
        invalidLeague.setLeagueDifficulty("Low");
        invalidLeague.setSeasonYear(2021);
        invalidLeague.setSeasonStartDate(LocalDate.of(2021, 7, 1));
        invalidLeague.setSeasonEndDate(LocalDate.of(2022, 4, 1));
        invalidLeague.setCompetitionFormatType("HomeAway");
        invalidLeague.setCompetitionFormatGroupStage(false);
        invalidLeague.setCompetitionFormatKnockout(false);

        webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidLeague)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains("For LEAGUE format, number of teams must be 18, 20, or 27")));
    }

    @Test
    public void whenCreatingCupLeagueWithInvalidTeamCount_thenReturnUnprocessableEntity() {
        LeagueRequestModel invalidCupLeague = new LeagueRequestModel();
        invalidCupLeague.setName("Invalid CUP League");
        invalidCupLeague.setCountry("Nowhere");
        invalidCupLeague.setFormat(FormatType.CUP);
        invalidCupLeague.setNumberOfTeams(30);
        invalidCupLeague.setLeagueDifficulty("Low");
        invalidCupLeague.setSeasonYear(2021);
        invalidCupLeague.setSeasonStartDate(LocalDate.of(2021, 7, 1));
        invalidCupLeague.setSeasonEndDate(LocalDate.of(2022, 4, 1));
        invalidCupLeague.setCompetitionFormatType("KnockoutOnly");
        invalidCupLeague.setCompetitionFormatGroupStage(false);
        invalidCupLeague.setCompetitionFormatKnockout(true);

        webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidCupLeague)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains("For CUP format, number of teams must be 32")));
    }

    //then return new cup league
    @Test
    public void whenCreatingCupLeagueWithValidTeamCount_thenReturnNewCupLeague() {
        LeagueRequestModel validCupLeague = new LeagueRequestModel();
        validCupLeague.setName("Valid CUP League");
        validCupLeague.setCountry("Nation");
        validCupLeague.setFormat(FormatType.CUP);
        validCupLeague.setNumberOfTeams(32);
        validCupLeague.setLeagueDifficulty("Medium");
        validCupLeague.setSeasonYear(2022);
        validCupLeague.setSeasonStartDate(LocalDate.of(2022, 1, 1));
        validCupLeague.setSeasonEndDate(LocalDate.of(2022, 12, 31));
        validCupLeague.setCompetitionFormatType("KnockoutOnly");
        validCupLeague.setCompetitionFormatGroupStage(false);
        validCupLeague.setCompetitionFormatKnockout(true);

        LeagueResponseModel response = webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validCupLeague)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertNotNull(response.getLeagueId());
        assertEquals(32, response.getNumberOfTeams());
    }


    //then return unprocessable entity
    @Test
    public void whenGetLeagueWithInvalidId_thenReturnUnprocessableEntity() {
        String invalidLeagueId = "INVALID_ID";
        webClient.get()
                .uri(BASE_URI_LEAGUES + "/" + invalidLeagueId)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains("Invalid leagueId provided")));
    }
    @Test
    public void whenUpdateLeagueWithInvalidId_thenReturnUnprocessableEntity(){
        String invalidLeagueId = "123";
        LeagueRequestModel updateRequest = new LeagueRequestModel();
        updateRequest.setName("Fake League");
        updateRequest.setCountry("Nowhere");
        updateRequest.setFormat(FormatType.LEAGUE);
        updateRequest.setNumberOfTeams(18);
        updateRequest.setLeagueDifficulty("Low");
        updateRequest.setSeasonYear(2021);
        updateRequest.setSeasonStartDate(LocalDate.of(2021, 1, 1));
        updateRequest.setSeasonEndDate(LocalDate.of(2021, 12, 31));
        updateRequest.setCompetitionFormatType("HomeAway");
        updateRequest.setCompetitionFormatGroupStage(false);
        updateRequest.setCompetitionFormatKnockout(false);

        webClient.put()
                .uri(BASE_URI_LEAGUES + "/" + invalidLeagueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains("Invalid leagueId provided")));
    }
    //then return unprocessable entity
    @Test
    public void whenDeleteLeagueWithInvalidId_thenReturnUnprocessableEntity() {
        String invalidLeagueId = "123";
        webClient.delete()
                .uri(BASE_URI_LEAGUES + "/" + invalidLeagueId)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains("Invalid leagueId provided")));
    }

    @Test
    public void whenAccessingNonExistentLeague_thenReturnNotFound() {
        String nonExistentLeagueId = UUID.randomUUID().toString();

        webClient.get()
                .uri(BASE_URI_LEAGUES + "/" + nonExistentLeagueId)
                .exchange()
                .expectStatus().isNotFound();

        LeagueRequestModel updateRequest = new LeagueRequestModel();
        updateRequest.setName("Non-existent League");
        updateRequest.setCountry("None");
        updateRequest.setFormat(FormatType.LEAGUE);
        updateRequest.setNumberOfTeams(18);
        updateRequest.setLeagueDifficulty("Low");
        updateRequest.setSeasonYear(2021);
        updateRequest.setSeasonStartDate(LocalDate.of(2021, 1, 1));
        updateRequest.setSeasonEndDate(LocalDate.of(2021, 12, 31));
        updateRequest.setCompetitionFormatType("HomeAway");
        updateRequest.setCompetitionFormatGroupStage(false);
        updateRequest.setCompetitionFormatKnockout(false);

        webClient.put()
                .uri(BASE_URI_LEAGUES + "/" + nonExistentLeagueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound();

        webClient.delete()
                .uri(BASE_URI_LEAGUES + "/" + nonExistentLeagueId)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void whenCreatingLeagueWithInvalidPayload_thenReturnUnprocessableEntity() {
        String invalidJson = "{}";
        webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isEqualTo(422);
    }


    @Test
    public void whenCreatingLeagueWithoutName_thenReturnInvalidInputError() {
        LeagueRequestModel leagueRequest = new LeagueRequestModel();
        leagueRequest.setName(null);
        leagueRequest.setCountry("TestCountry");
        leagueRequest.setFormat(FormatType.LEAGUE);
        leagueRequest.setNumberOfTeams(18);
        leagueRequest.setLeagueDifficulty("High");
        leagueRequest.setSeasonYear(2021);
        leagueRequest.setSeasonStartDate(LocalDate.of(2021, 8, 1));
        leagueRequest.setSeasonEndDate(LocalDate.of(2022, 5, 15));
        leagueRequest.setCompetitionFormatType("HomeAway");
        leagueRequest.setCompetitionFormatGroupStage(false);
        leagueRequest.setCompetitionFormatKnockout(false);

        webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(leagueRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains("League name is required")));
    }
    @Test
    public void whenUpdatingLeagueWithoutName_thenReturnUnprocessableEntity() {
        // first create a valid league
        LeagueRequestModel createdReq = new LeagueRequestModel();
        createdReq.setName("Temp League");
        createdReq.setCountry("X");
        createdReq.setFormat(FormatType.LEAGUE);
        createdReq.setNumberOfTeams(18);
        createdReq.setLeagueDifficulty("Low");
        createdReq.setSeasonYear(2025);
        createdReq.setSeasonStartDate(LocalDate.of(2025,1,1));
        createdReq.setSeasonEndDate(LocalDate.of(2025,12,31));
        createdReq.setCompetitionFormatType("LEAGUE");
        createdReq.setCompetitionFormatGroupStage(false);
        createdReq.setCompetitionFormatKnockout(false);

        LeagueResponseModel created = webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createdReq)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        String id = created.getLeagueId();

        // now attempt update with null name
        LeagueRequestModel missingName = new LeagueRequestModel();
        missingName.setName(null);
        missingName.setCountry("X");
        missingName.setFormat(FormatType.LEAGUE);
        missingName.setNumberOfTeams(18);
        missingName.setLeagueDifficulty("Low");
        missingName.setSeasonYear(2025);
        missingName.setSeasonStartDate(LocalDate.of(2025,1,1));
        missingName.setSeasonEndDate(LocalDate.of(2025,12,31));
        missingName.setCompetitionFormatType("LEAGUE");
        missingName.setCompetitionFormatGroupStage(false);
        missingName.setCompetitionFormatKnockout(false);

        webClient.put()
                .uri(BASE_URI_LEAGUES + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(missingName)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains("League name is required")));
    }

    @Test
    public void whenUpdatingLeagueWithInvalidTeamCount_thenReturnUnprocessableEntity() {
        // create a valid LEAGUE-format league
        LeagueRequestModel valid = new LeagueRequestModel();
        valid.setName("Foo");
        valid.setCountry("Y");
        valid.setFormat(FormatType.LEAGUE);
        valid.setNumberOfTeams(20);
        valid.setLeagueDifficulty("Low");
        valid.setSeasonYear(2025);
        valid.setSeasonStartDate(LocalDate.of(2025,2,1));
        valid.setSeasonEndDate(LocalDate.of(2025,11,30));
        valid.setCompetitionFormatType("LEAGUE");
        valid.setCompetitionFormatGroupStage(false);
        valid.setCompetitionFormatKnockout(false);

        LeagueResponseModel created = webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(valid)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        String id = created.getLeagueId();

        // now attempt update with an invalid number of teams
        LeagueRequestModel badCount = new LeagueRequestModel();
        badCount.setName("Foo");
        badCount.setCountry("Y");
        badCount.setFormat(FormatType.LEAGUE);
        badCount.setNumberOfTeams(19);  // invalid for LEAGUE
        badCount.setLeagueDifficulty("Low");
        badCount.setSeasonYear(2025);
        badCount.setSeasonStartDate(LocalDate.of(2025,2,1));
        badCount.setSeasonEndDate(LocalDate.of(2025,11,30));
        badCount.setCompetitionFormatType("LEAGUE");
        badCount.setCompetitionFormatGroupStage(false);
        badCount.setCompetitionFormatKnockout(false);

        webClient.put()
                .uri(BASE_URI_LEAGUES + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badCount)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains(
                        "For LEAGUE format, number of teams must be 18, 20, or 27")));
    }

    @Test
    public void whenUpdatingCupLeagueWithInvalidTeamCount_thenReturnUnprocessableEntity() {
        // create a valid CUP-format league
        LeagueRequestModel validCup = new LeagueRequestModel();
        validCup.setName("Cup Foo");
        validCup.setCountry("Z");
        validCup.setFormat(FormatType.CUP);
        validCup.setNumberOfTeams(32);
        validCup.setLeagueDifficulty("Low");
        validCup.setSeasonYear(2025);
        validCup.setSeasonStartDate(LocalDate.of(2025,3,1));
        validCup.setSeasonEndDate(LocalDate.of(2025,10,31));
        validCup.setCompetitionFormatType("CUP");
        validCup.setCompetitionFormatGroupStage(false);
        validCup.setCompetitionFormatKnockout(true);

        LeagueResponseModel createdCup = webClient.post()
                .uri(BASE_URI_LEAGUES)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validCup)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LeagueResponseModel.class)
                .returnResult()
                .getResponseBody();

        String cupId = createdCup.getLeagueId();

        // now attempt update with wrong team count
        LeagueRequestModel badCupCount = new LeagueRequestModel();
        badCupCount.setName("Cup Foo");
        badCupCount.setCountry("Z");
        badCupCount.setFormat(FormatType.CUP);
        badCupCount.setNumberOfTeams(30);  // invalid for CUP
        badCupCount.setLeagueDifficulty("Low");
        badCupCount.setSeasonYear(2025);
        badCupCount.setSeasonStartDate(LocalDate.of(2025,3,1));
        badCupCount.setSeasonEndDate(LocalDate.of(2025,10,31));
        badCupCount.setCompetitionFormatType("CUP");
        badCupCount.setCompetitionFormatGroupStage(false);
        badCupCount.setCompetitionFormatKnockout(true);

        webClient.put()
                .uri(BASE_URI_LEAGUES + "/" + cupId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badCupCount)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .value(msg -> assertTrue(msg.contains(
                        "For CUP format, number of teams must be 32")));
    }

}
