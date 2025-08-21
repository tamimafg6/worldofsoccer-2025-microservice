package com.worldofsoccer.match.presentationlayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worldofsoccer.match.dataAccessLayer.Match;
import com.worldofsoccer.match.dataAccessLayer.MatchRepository;
import com.worldofsoccer.match.dataAccessLayer.MatchStatus;
import com.worldofsoccer.match.dataAccessLayer.ResultsType;
import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.league.LeagueServiceClient;
import com.worldofsoccer.match.domainclientLayer.location.VenueModel;
import com.worldofsoccer.match.domainclientLayer.location.VenueServiceClient;
import com.worldofsoccer.match.domainclientLayer.teams.TeamModel;
import com.worldofsoccer.match.domainclientLayer.teams.TeamServiceClient;
import com.worldofsoccer.match.utils.HttpErrorInfo;
import com.worldofsoccer.match.utils.exceptions.InvalidInputException;
import com.worldofsoccer.match.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MatchControllerIntegrationTest {

    @Autowired
    private WebTestClient    webClient;

    @Autowired
    private RestTemplate     restTemplate;

    @Autowired
    private MatchRepository  matchRepository;

    private MockRestServiceServer mockServer;
    private ObjectMapper          mapper = new ObjectMapper();

    private static final String API_PREFIX          = "/api/v1/leagues";
    private static final String FOUND_LEAGUE_ID     = "11111111-1111-1111-1111-111111111111";
    private static final String NOT_FOUND_LEAGUE_ID = "22222222-2222-2222-2222-222222222222";

    // **correct ports**
    private static final String LEAGUE_BASE = "http://localhost:7002/api/v1/leagues";
    private static final String TEAMS_BASE  = "http://localhost:7001/api/v1/teams";
    private static final String VENUES_BASE = "http://localhost:7003/api/v1/venues";

    @BeforeEach
    void init() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (var converter : restTemplate.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                jacksonConverter.setObjectMapper(mapper);
            }
        }

        mockServer = MockRestServiceServer.createServer(restTemplate);
        assertTrue(matchRepository.count() > 0, "Expected some matches pre-loaded");
    }

    @Test
    void whenLeagueExists_thenReturnAllLeagueMatches() throws Exception {
        var league = LeagueModel.builder()
                .leagueId(FOUND_LEAGUE_ID)
                .leagueName("Premier League")
                .leagueFormat("Round Robin")
                .build();

        mockServer.expect(once(),
                        requestTo(new URI(LEAGUE_BASE + "/" + FOUND_LEAGUE_ID)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(league), MediaType.APPLICATION_JSON));

        webClient.get()
                .uri(API_PREFIX + "/" + FOUND_LEAGUE_ID + "/matches")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Match.class)
                .value(list -> {
                    assertNotNull(list);
                    assertTrue(list.size() > 0, "Should have at least the preloaded matches");
                });
    }

    @Test
    void whenLeagueNotFound_thenReturnNotFound() throws Exception {
        mockServer.expect(once(),
                        requestTo(new URI(LEAGUE_BASE + "/" + NOT_FOUND_LEAGUE_ID)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        webClient.get()
                .uri(API_PREFIX + "/" + NOT_FOUND_LEAGUE_ID + "/matches")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenPostValid_thenCreateAndReturnMatch() throws Exception {
        var league = LeagueModel.builder()
                .leagueId(FOUND_LEAGUE_ID)
                .leagueName("Premier League")
                .leagueFormat("LEAGUE")
                .build();
        mockServer.expect(ExpectedCount.times(2),
                        requestTo(new URI(LEAGUE_BASE + "/" + FOUND_LEAGUE_ID)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(league), MediaType.APPLICATION_JSON));

        var team = TeamModel.builder()
                .teamId("team-123")
                .teamName("Some FC")
                .coach("Coach X")
                .teamFoundingYear(1900)
                .teamBudget(new BigDecimal("1000000.00"))
                .build();
        mockServer.expect(once(),
                        requestTo(new URI(TEAMS_BASE + "/" + team.getTeamId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(team), MediaType.APPLICATION_JSON));

        var venue = VenueModel.builder()
                .venueId("venue-123")
                .venueName("Stadium Y")
                .venueCity("City Z")
                .venueCapacity(50000)
                .venueState("UPCOMING")
                .build();
        String venueJson = String.format(
                "{\"venueId\":\"%s\",\"name\":\"%s\",\"city\":\"%s\",\"capacity\":%d,\"venueState\":\"%s\"}",
                venue.getVenueId(), venue.getVenueName(), venue.getVenueCity(), venue.getVenueCapacity(), venue.getVenueState());
        mockServer.expect(once(),
                        requestTo(new URI(VENUES_BASE + "/" + venue.getVenueId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(venueJson, MediaType.APPLICATION_JSON));

        mockServer.expect(once(),
                        requestTo(new URI(VENUES_BASE + "/" + venue.getVenueId() + "/state")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(venueJson, MediaType.APPLICATION_JSON));

        var req = MatchRequestModel.builder()
                .leagueId(FOUND_LEAGUE_ID)
                .teamId(team.getTeamId())
                .venueId(venue.getVenueId())
                .matchScore("0-0")
                .matchStatus(MatchStatus.SCHEDULED)
                .matchTime(LocalTime.of(15, 0))
                .matchDate(LocalDate.now().plusDays(1))
                .matchDuration(LocalTime.of(1, 30))
                .resultsType(ResultsType.DRAW)
                .matchMinute(0)
                .build();

        webClient.post()
                .uri(API_PREFIX + "/" + FOUND_LEAGUE_ID + "/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.matchId").isNotEmpty()
                .jsonPath("$.leagueId").isEqualTo(FOUND_LEAGUE_ID)
                .jsonPath("$.teamId").isEqualTo(req.getTeamId())
                .jsonPath("$.venueId").isEqualTo(req.getVenueId())
                .jsonPath("$.matchScore").isEqualTo("0-0");
    }

    @Test
    void whenPostLeagueNotFound_thenReturnNotFound() throws Exception {
        mockServer.expect(once(),
                        requestTo(new URI(LEAGUE_BASE + "/" + NOT_FOUND_LEAGUE_ID)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var badReq = new MatchRequestModel();
        badReq.setLeagueId(NOT_FOUND_LEAGUE_ID);

        webClient.post()
                .uri(API_PREFIX + "/" + NOT_FOUND_LEAGUE_ID + "/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badReq)
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    void whenPostMatchWithInvalidDuration_thenReturnUnprocessableEntity() throws Exception {
        var league = LeagueModel.builder()
                .leagueId(FOUND_LEAGUE_ID)
                .leagueName("Premier League")
                .leagueFormat("LEAGUE")
                .build();
        mockServer.expect(ExpectedCount.times(2),
                        requestTo(new URI(LEAGUE_BASE + "/" + FOUND_LEAGUE_ID)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(league), MediaType.APPLICATION_JSON));

        var team = TeamModel.builder()
                .teamId("team-123")
                .teamName("Some FC")
                .coach("Coach X")
                .teamFoundingYear(1900)
                .teamBudget(new BigDecimal("1000000.00"))
                .build();
        mockServer.expect(once(),
                        requestTo(new URI(TEAMS_BASE + "/" + team.getTeamId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(team), MediaType.APPLICATION_JSON));

        var venue = VenueModel.builder()
                .venueId("venue-123")
                .venueName("Stadium Y")
                .venueCity("City Z")
                .venueCapacity(50000)
                .venueState("UPCOMING")
                .build();
        String venueJson = String.format(
                "{\"venueId\":\"%s\",\"name\":\"%s\",\"city\":\"%s\",\"capacity\":%d,\"venueState\":\"%s\"}",
                venue.getVenueId(), venue.getVenueName(), venue.getVenueCity(), venue.getVenueCapacity(), venue.getVenueState());
        mockServer.expect(once(),
                        requestTo(new URI(VENUES_BASE + "/" + venue.getVenueId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(venueJson, MediaType.APPLICATION_JSON));

        var req = MatchRequestModel.builder()
                .leagueId(FOUND_LEAGUE_ID)
                .teamId(team.getTeamId())
                .venueId(venue.getVenueId())
                .matchScore("0-0")
                .matchStatus(MatchStatus.SCHEDULED)
                .matchTime(LocalTime.of(15, 0))
                .matchDate(LocalDate.now().plusDays(1))
                .matchDuration(LocalTime.of(1, 0))
                .resultsType(ResultsType.DRAW)
                .matchMinute(0)
                .build();

        webClient.post()
                .uri(API_PREFIX + "/" + FOUND_LEAGUE_ID + "/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Match duration must be between 1:30 and 3:00 hours");
    }

    @Test
    void whenVenuePatchReturnsBadJson_thenPutUpdateReturns500() throws Exception {
        String M = matchRepository.findAll().get(0).getMatchIdentifier().getMatchId();

        var league = LeagueModel.builder()
                .leagueId(FOUND_LEAGUE_ID)
                .leagueName("Premier League")
                .leagueFormat("LEAGUE")
                .build();
        mockServer.expect(ExpectedCount.times(2),
                        requestTo(new URI(LEAGUE_BASE + "/" + FOUND_LEAGUE_ID)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(league), MediaType.APPLICATION_JSON));

        var team = TeamModel.builder()
                .teamId("team-123")
                .teamName("Test Team")
                .coach("Test Coach")
                .teamFoundingYear(2000)
                .teamBudget(new BigDecimal("1000000"))
                .build();
        mockServer.expect(once(),
                        requestTo(new URI(TEAMS_BASE + "/" + team.getTeamId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(team), MediaType.APPLICATION_JSON));

        var venue = VenueModel.builder()
                .venueId("venue-123")
                .venueName("X")
                .venueCity("Y")
                .venueCapacity(1)
                .venueState("UPCOMING")
                .build();
        mockServer.expect(once(),
                        requestTo(new URI(VENUES_BASE + "/" + venue.getVenueId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(venue), MediaType.APPLICATION_JSON));

        mockServer.expect(once(),
                        requestTo(new URI(VENUES_BASE + "/" + venue.getVenueId() + "/state")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ invalid json"));

        var req = MatchRequestModel.builder()
                .leagueId(FOUND_LEAGUE_ID)
                .teamId(team.getTeamId())
                .venueId(venue.getVenueId())
                .matchDuration(LocalTime.of(1, 45))
                .matchStatus(MatchStatus.SCHEDULED)
                .matchDate(LocalDate.now())
                .matchTime(LocalTime.now())
                .matchScore("0-0")
                .resultsType(ResultsType.DRAW)
                .matchMinute(0)
                .build();

        webClient.put()
                .uri(API_PREFIX + "/" + FOUND_LEAGUE_ID + "/matches/" + M)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().is5xxServerError();
    }
    @Test
    void whenGetAllLeaguesClient_thenReturnsList() throws Exception {
        LeagueModel[] stub = new LeagueModel[]{
                LeagueModel.builder()
                        .leagueId("L1")
                        .leagueName("Champions")
                        .leagueFormat("LEAGUE")
                        .build()
        };
        String json = mapper.writeValueAsString(stub);

        mockServer.expect(once(),
                        requestTo(new URI(LEAGUE_BASE)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        LeagueServiceClient client = new LeagueServiceClient(restTemplate, mapper, "localhost", "7002");
        List<LeagueModel> result = client.getAllLeagues();

        assertEquals(1, result.size());
        assertEquals("L1", result.get(0).getLeagueId());
        assertEquals("Champions", result.get(0).getLeagueName());

        mockServer.verify();
    }

    @Test
    void whenGetLeagueByIdNotFound_thenThrowsNotFoundException() throws Exception {
        String badId = "nope";
        mockServer.expect(once(),
                        requestTo(new URI(LEAGUE_BASE + "/" + badId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"not found\"}"));

        LeagueServiceClient client = new LeagueServiceClient(restTemplate, mapper, "localhost", "7002");
        assertThrows(NotFoundException.class, () -> client.getLeagueById(badId));
    }
    @Test
    void whenGetAllTeamsClient_thenReturnsList() throws Exception {
        TeamModel[] stub = new TeamModel[]{
                TeamModel.builder()
                        .teamId("T1")
                        .teamName("Tigers")
                        .coach("Coach Z")
                        .teamFoundingYear(2000)
                        .teamBudget(new BigDecimal("12345.67"))
                        .build()
        };
        String json = mapper.writeValueAsString(stub);

        mockServer.expect(once(),
                        requestTo(new URI(TEAMS_BASE)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TeamServiceClient client = new TeamServiceClient(restTemplate, mapper, "localhost", "7001");
        List<TeamModel> result = client.getAllTeams();

        assertEquals(1, result.size());
        assertEquals("T1", result.get(0).getTeamId());
        assertEquals("Tigers", result.get(0).getTeamName());

        mockServer.verify();
    }

    @Test
    void whenGetTeamByIdUnprocessable_thenThrowsInvalidInputException() throws Exception {
        String badId = "bad";
        mockServer.expect(once(),
                        requestTo(new URI(TEAMS_BASE + "/" + badId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"bad input\"}"));

        TeamServiceClient client = new TeamServiceClient(restTemplate, mapper, "localhost", "7001");
        assertThrows(InvalidInputException.class, () -> client.getTeamById(badId));
    }
    @Test
    void whenGetAllVenuesClient_thenReturnsList() throws Exception {
        VenueModel[] stub = new VenueModel[]{
                VenueModel.builder()
                        .venueId("V1")
                        .venueName("Stadium A")
                        .venueCity("City X")
                        .venueCapacity(10000)
                        .venueState("OPEN")
                        .build()
        };
        String json = mapper.writeValueAsString(stub);

        mockServer.expect(once(),
                        requestTo(new URI(VENUES_BASE)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        VenueServiceClient client = new VenueServiceClient(restTemplate, mapper, "localhost", "7003");
        List<VenueModel> result = client.getAllVenues();

        assertEquals(1, result.size());
        assertEquals("V1", result.get(0).getVenueId());
        assertEquals("Stadium A", result.get(0).getVenueName());

        mockServer.verify();
    }

    @Test
    void whenPatchVenueStateNotFound_thenThrowsNotFoundException() throws Exception {
        String vid = "V1";
        mockServer.expect(once(),
                        requestTo(new URI(VENUES_BASE + "/" + vid + "/state")))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"no venue\"}"));

        VenueServiceClient client = new VenueServiceClient(restTemplate, mapper, "localhost", "7003");
        assertThrows(NotFoundException.class, () -> client.patchVenueState(vid, MatchStatus.SCHEDULED));

        mockServer.verify();
    }
}
