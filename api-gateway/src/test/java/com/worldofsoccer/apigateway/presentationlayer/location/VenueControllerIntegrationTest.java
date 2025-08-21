package com.worldofsoccer.apigateway.presentationlayer.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldofsoccer.apigateway.domainclientlayer.location.VenueStateEnum;
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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class VenueControllerIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    private MockRestServiceServer mockServer;

    private static final String API_BASE       = "/api/v1/venues";
    private static final String DOWNSTREAM_URL = "http://localhost:7003/api/v1/venues";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenGetAllVenues_thenReturns200List() throws Exception {
        VenueResponseModel a = VenueResponseModel.builder()
                .venueId("aaa11111-1111-1111-1111-111111111111")
                .name("Stadium A")
                .city("City A")
                .capacity(10000)
                .yearBuilt(1990)
                .venueState(VenueStateEnum.UPCOMING)
                .build();

        VenueResponseModel b = VenueResponseModel.builder()
                .venueId("bbb22222-2222-2222-2222-222222222222")
                .name("Stadium B")
                .city("City B")
                .capacity(20000)
                .yearBuilt(1980)
                .venueState(VenueStateEnum.PAST)
                .build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        mapper.writeValueAsString(new VenueResponseModel[]{a, b}),
                        MediaType.APPLICATION_JSON
                ));

        webClient.get()
                .uri(API_BASE)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VenueResponseModel.class)
                .value(list -> {
                    assert list.size() == 2;
                    assert list.get(0).getVenueId().equals(a.getVenueId());
                    assert list.get(1).getVenueId().equals(b.getVenueId());
                });
    }

    @Test
    void whenGetVenueById_exists_thenReturns200WithBody() throws Exception {
        String id = "11111111-1111-1111-1111-111111111111";
        VenueResponseModel dto = VenueResponseModel.builder()
                .venueId(id)
                .name("Stadium X")
                .city("Metropolis")
                .capacity(50000)
                .yearBuilt(2000)
                .venueState(VenueStateEnum.LIVE)
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
                .jsonPath("$.venueId").isEqualTo(id)
                .jsonPath("$.name").isEqualTo("Stadium X");
    }

    @Test
    void whenGetVenueById_notFound_thenReturns404() throws Exception {
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
    void whenCreateVenue_thenReturns201() throws Exception {
        VenueRequestModel req = VenueRequestModel.builder()
                .name("New Venue")
                .city("New City")
                .capacity(12345)
                .yearBuilt(2025)
                .venueState(VenueStateEnum.UPCOMING)
                .build();

        VenueResponseModel created = VenueResponseModel.builder()
                .venueId("ccc33333-3333-3333-3333-333333333333")
                .name("New Venue")
                .city("New City")
                .capacity(12345)
                .yearBuilt(2025)
                .venueState(VenueStateEnum.UPCOMING)
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
                .jsonPath("$.venueId").isEqualTo("ccc33333-3333-3333-3333-333333333333");
    }

    @Test
    void whenUpdateVenue_thenReturns200() throws Exception {
        String id = "ddd44444-4444-4444-4444-444444444444";
        VenueRequestModel req = new VenueRequestModel();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(DOWNSTREAM_URL + "/" + id)))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK));

        VenueResponseModel updated = VenueResponseModel.builder()
                .venueId(id)
                .name("Updated Venue")
                .city("City U")
                .capacity(54321)
                .yearBuilt(2010)
                .venueState(VenueStateEnum.PAST)
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
                .jsonPath("$.name").isEqualTo("Updated Venue");
    }

    @Test
    void whenDeleteVenue_thenReturns204() throws Exception {
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
