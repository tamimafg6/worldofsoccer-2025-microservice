package com.worldofsoccer.apigateway.presentationlayer.league;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldofsoccer.apigateway.domainclientlayer.league.FormatTypeEnum;
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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class LeagueControllerIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    private MockRestServiceServer mockServer;

    private static final String API_BASE       = "/api/v1/leagues";
    private static final String DOWNSTREAM_URL = "http://localhost:7002/api/v1/leagues";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenGetLeagueById_exists_thenReturns200WithBody() throws Exception {
        String id = "11111111-1111-1111-1111-111111111111";
        LeagueResponseModel dto = LeagueResponseModel.builder()
                .leagueId(id)
                .name("Premier League")
                .country("England")
                .format(FormatTypeEnum.LEAGUE)
                .numberOfTeams(20)
                .leagueDifficulty("High")
                .seasonYear(2025)
                .seasonStartDate(LocalDate.of(2025, 8, 1))
                .seasonEndDate(LocalDate.of(2026, 5, 31))
                .competitionFormatType("Round Robin")
                .competitionFormatGroupStage(true)
                .competitionFormatKnockout(false)
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(dto),
                        MediaType.APPLICATION_JSON
                ));

        webClient.get()
                .uri(API_BASE + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.parseMediaType("application/hal+json"))
                .expectBody()
                .jsonPath("$.leagueId").isEqualTo(id)
                .jsonPath("$.name").isEqualTo("Premier League")
                .jsonPath("$._links.self.href").isNotEmpty()
                .jsonPath("$._links.allLeagues.href").isNotEmpty();
    }

    @Test
    void whenGetLeagueById_notFound_thenReturns404() throws Exception {
        String id = "22222222-2222-2222-2222-222222222222";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        webClient.get()
                .uri(API_BASE + "/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetAllLeagues_thenReturns200List() throws Exception {
        LeagueResponseModel a = LeagueResponseModel.builder()
                .leagueId("aaa11111-1111-1111-1111-111111111111")
                .name("A")
                .build();
        LeagueResponseModel b = LeagueResponseModel.builder()
                .leagueId("bbb22222-2222-2222-2222-222222222222")
                .name("B")
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(new LeagueResponseModel[]{a, b}),
                        MediaType.APPLICATION_JSON
                ));

        webClient.get()
                .uri(API_BASE)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LeagueResponseModel.class)
                .value(list -> {
                    assert list.size() == 2;
                    assert list.get(0).getName().equals("A");
                    assert list.get(1).getName().equals("B");
                });
    }

    @Test
    void whenCreateLeague_thenReturns201() throws Exception {
        LeagueRequestModel req = LeagueRequestModel.builder()
                .name("La Liga")
                .country("Spain")
                .format(FormatTypeEnum.LEAGUE)
                .build();
        LeagueResponseModel created = LeagueResponseModel.builder()
                .leagueId("ccc33333-3333-3333-3333-333333333333")
                .name("La Liga")
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(created),
                        MediaType.APPLICATION_JSON
                ));

        webClient.post()
                .uri(API_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.leagueId").isEqualTo("ccc33333-3333-3333-3333-333333333333");
    }

    @Test
    void whenUpdateLeague_thenReturns200() throws Exception {
        String id = "ddd44444-4444-4444-4444-444444444444";
        LeagueRequestModel req = new LeagueRequestModel();

        // stub PUT
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + id)))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        // stub GET for updated resource
        LeagueResponseModel updated = LeagueResponseModel.builder()
                .leagueId(id)
                .name("Bundesliga")
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(updated),
                        MediaType.APPLICATION_JSON
                ));

        webClient.put()
                .uri(API_BASE + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Bundesliga");
    }

    @Test
    void whenDeleteLeague_thenReturns204() throws Exception {
        String id = "eee55555-5555-5555-5555-555555555555";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + id)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        webClient.delete()
                .uri(API_BASE + "/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
