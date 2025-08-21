package com.worldofsoccer.apigateway.presentationlayer.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MatchControllerIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    private MockRestServiceServer mockServer;

    private static final String API_BASE       = "/api/v1/leagues";
    private static final String DOWNSTREAM_URL = "http://localhost:7004/api/v1/leagues";

    private final String LEAGUE_ID = "11111111-1111-1111-1111-111111111111";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenGetAllMatches_thenReturns200List() throws Exception {
        MatchResponseModel m1 = MatchResponseModel.builder()
                .matchId("aaa11111-1111-1111-1111-111111111111")
                .matchScore("2-1")
                .matchStatus(null)
                .build();
        MatchResponseModel m2 = MatchResponseModel.builder()
                .matchId("bbb22222-2222-2222-2222-222222222222")
                .matchScore("3-0")
                .matchStatus(null)
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + LEAGUE_ID + "/matches")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(new MatchResponseModel[]{m1, m2}),
                        MediaType.APPLICATION_JSON
                ));

        webClient.get()
                .uri(API_BASE + "/" + LEAGUE_ID + "/matches")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MatchResponseModel.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    assertEquals("2-1", list.get(0).getMatchScore());
                    assertEquals("3-0", list.get(1).getMatchScore());
                });
    }

    @Test
    void whenGetMatchById_exists_thenReturns200() throws Exception {
        String matchId = "22222222-2222-2222-2222-222222222222";
        MatchResponseModel dto = MatchResponseModel.builder()
                .matchId(matchId)
                .matchScore("1-0")
                .matchDate(LocalDate.of(2025,5,10))
                .matchTime(LocalTime.of(15,0))
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + LEAGUE_ID + "/matches/" + matchId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(dto),
                        MediaType.APPLICATION_JSON
                ));

        webClient.get()
                .uri(API_BASE + "/" + LEAGUE_ID + "/matches/" + matchId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.matchId").isEqualTo(matchId)
                .jsonPath("$.matchScore").isEqualTo("1-0");
    }

    @Test
    void whenCreateMatch_thenReturns201() throws Exception {
        MatchRequestModel req = new MatchRequestModel();
        req.setTeamId("team-1");
        req.setVenueId("venue-1");
        req.setMatchScore("0-0");
        req.setMatchDate(LocalDate.now().plusDays(1));
        req.setMatchTime(LocalTime.of(18,0));
        req.setMatchDuration(LocalTime.of(1,30));

        MatchResponseModel created = MatchResponseModel.builder()
                .matchId("ccc33333-3333-3333-3333-333333333333")
                .matchScore("0-0")
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + LEAGUE_ID + "/matches")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(created),
                        MediaType.APPLICATION_JSON
                ));

        webClient.post()
                .uri(API_BASE + "/" + LEAGUE_ID + "/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.matchId").isEqualTo("ccc33333-3333-3333-3333-333333333333");
    }

    @Test
    void whenUpdateMatch_thenReturns200() throws Exception {
        String matchId = "ddd44444-4444-4444-4444-444444444444";
        MatchRequestModel req = new MatchRequestModel();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + LEAGUE_ID + "/matches/" + matchId)))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        MatchResponseModel updated = MatchResponseModel.builder()
                .matchId(matchId)
                .matchScore("2-2")
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + LEAGUE_ID + "/matches/" + matchId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(updated),
                        MediaType.APPLICATION_JSON
                ));

        webClient.put()
                .uri(API_BASE + "/" + LEAGUE_ID + "/matches/" + matchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.matchScore").isEqualTo("2-2");
    }

    @Test
    void whenDeleteMatch_thenReturns204() throws Exception {
        String matchId = "eee55555-5555-5555-5555-555555555555";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + LEAGUE_ID + "/matches/" + matchId)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        webClient.delete()
                .uri(API_BASE + "/" + LEAGUE_ID + "/matches/" + matchId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
