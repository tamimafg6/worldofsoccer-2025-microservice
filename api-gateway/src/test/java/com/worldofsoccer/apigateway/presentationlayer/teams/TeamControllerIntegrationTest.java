package com.worldofsoccer.apigateway.presentationlayer.teams;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class TeamControllerIntegrationTest {

    @Autowired private WebTestClient webClient;
    @Autowired private RestTemplate restTemplate;
    @Autowired private ObjectMapper mapper;

    private MockRestServiceServer mockServer;

    private static final String API_BASE       = "/api/v1/teams";
    private static final String DOWNSTREAM_URL = "http://localhost:7001/api/v1/teams";

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenGetAll_thenReturns200List() throws Exception {
        TeamResponseModel a = TeamResponseModel.builder()
                .teamId("aaa11111-1111-1111-1111-111111111111")
                .name("Alpha")
                .coach("Coach A")
                .foundingYear(1900)
                .budget("1000000")
                .teamStatus("ACTIVE")
                .build();

        TeamResponseModel b = TeamResponseModel.builder()
                .teamId("bbb22222-2222-2222-2222-222222222222")
                .name("Beta")
                .coach("Coach B")
                .foundingYear(1950)
                .budget("500000")
                .teamStatus("INACTIVE")
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(new TeamResponseModel[]{a, b}),
                        MediaType.APPLICATION_JSON
                ));

        webClient.get()
                .uri(API_BASE)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TeamResponseModel.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    assertEquals("Alpha", list.get(0).getName());
                    assertEquals("Beta", list.get(1).getName());
                });
    }

    @Test
    void whenGetById_exists_thenReturns200() throws Exception {
        String id = "11111111-1111-1111-1111-111111111111";
        TeamResponseModel dto = TeamResponseModel.builder()
                .teamId(id)
                .name("The FC")
                .coach("Legend")
                .foundingYear(2000)
                .budget("750000")
                .teamStatus("ACTIVE")
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
                .expectBody()
                .jsonPath("$.teamId").isEqualTo(id)
                .jsonPath("$.name").isEqualTo("The FC");
    }

    @Test
    void whenGetById_notFound_thenReturns404() throws Exception {
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
    void whenCreate_thenReturns201() throws Exception {
        TeamRequestModel req = TeamRequestModel.builder()
                .teamName("Gamma")
                .coach("Coach G")
                .foundingYear(2025)
                .budget(1234567.0)
                .teamStatus("UPCOMING")
                .build();

        TeamResponseModel created = TeamResponseModel.builder()
                .teamId("ccc33333-3333-3333-3333-333333333333")
                .name("Gamma")
                .coach("Coach G")
                .foundingYear(2025)
                .budget("1234567")
                .teamStatus("UPCOMING")
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
                .jsonPath("$.teamId").isEqualTo("ccc33333-3333-3333-3333-333333333333");
    }

    @Test
    void whenUpdate_thenReturns200() throws Exception {
        String id = "ddd44444-4444-4444-4444-444444444444";
        TeamRequestModel req = new TeamRequestModel();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + id)))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        TeamResponseModel updated = TeamResponseModel.builder()
                .teamId(id)
                .name("Delta")
                .coach("Coach D")
                .foundingYear(1999)
                .budget("333333")
                .teamStatus("ACTIVE")
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
                .jsonPath("$.name").isEqualTo("Delta");
    }

    @Test
    void whenDelete_thenReturns204() throws Exception {
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
