package com.worldofsoccer.location.presentationlayer;

import com.worldofsoccer.location.dataaccesslayer.MatchStatus;
import com.worldofsoccer.location.dataaccesslayer.Venue;
import com.worldofsoccer.location.dataaccesslayer.VenueRepository;
import com.worldofsoccer.location.dataaccesslayer.VenueState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data-h2.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VenueControllerIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private VenueRepository venueRepository;

    private final String BASE_URI       = "/api/v1/venues";
    private String       validVenueId;
    private final String invalidVenueId = "1234";

    @BeforeEach
    public void setupDb() {
        venueRepository.deleteAll();
        Venue v = new Venue( "TestVenue", 1000, "TestCity", 2000, VenueState.UPCOMING);
        venueRepository.save(v);
        validVenueId = v.getVenueIdentifier().getVenueId();
    }

    @Test
    public void whenGetAllVenues_thenReturnList() {
        webClient.get().uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(VenueResponseModel.class)
                .value(list -> {
                    assertNotNull(list);
                    assertTrue(list.size() > 0);
                });
    }

    @Test
    public void whenGetVenueById_thenReturnVenue() {
        webClient.get().uri(BASE_URI + "/" + validVenueId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(VenueResponseModel.class)
                .value(vr -> {
                    assertNotNull(vr);
                    assertEquals(validVenueId, vr.getVenueId());
                });
    }

    @Test
    public void whenGetVenueWithInvalidIdLength_thenReturnUnprocessableEntity() {
        webClient.get().uri(BASE_URI + "/" + invalidVenueId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid venueId provided: " + invalidVenueId);
    }

    @Test
    public void whenGetNonExistentVenue_thenReturnNotFound() {
        String randomId = UUID.randomUUID().toString();
        webClient.get().uri(BASE_URI + "/" + randomId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenCreateValidVenue_thenReturnCreated() {
        VenueRequestModel req = new VenueRequestModel(null, "NewVenue", 500, "CityX", 2021, null);
        webClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VenueResponseModel.class)
                .value(vr -> {
                    assertNotNull(vr.getVenueId());
                    assertEquals("NewVenue", vr.getName());
                    assertEquals(500, vr.getCapacity());
                });
    }

    @Test
    public void whenCreateWithoutName_thenReturnUnprocessableEntity() {
        VenueRequestModel req = new VenueRequestModel(null, null, 500, "CityX", 2021, null);
        webClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Venue name is required.");
    }

    @Test
    public void whenCreateWithLowCapacity_thenReturnUnprocessableEntity() {
        VenueRequestModel req = new VenueRequestModel(null, "Tiny", 50, "CityY", 1999, null);
        webClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Venue capacity must be at least 100. Provided: 50");
    }

    @Test
    public void whenUpdateValid_thenReturnCreatedVenue() {
        VenueRequestModel update = new VenueRequestModel(null, "Updated", 1500, "CityZ", 2022, null);
        webClient.put().uri(BASE_URI + "/" + validVenueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()            // now expecting 200 OK
                .expectBody(VenueResponseModel.class)
                .value(vr -> {
                    assertEquals("Updated", vr.getName());
                    assertEquals(1500, vr.getCapacity());
                });
    }

    @Test
    public void whenUpdateInvalidId_thenReturnUnprocessableEntity() {
        VenueRequestModel update = new VenueRequestModel(null, "X", 200, "City", 2000, null);
        webClient.put().uri(BASE_URI + "/" + invalidVenueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid venueId provided: " + invalidVenueId);
    }

    @Test
    public void whenUpdateNonExistent_thenReturnNotFound() {
        VenueRequestModel update = new VenueRequestModel(null, "X", 200, "City", 2000, null);
        String randomId = UUID.randomUUID().toString();
        webClient.put().uri(BASE_URI + "/" + randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenDeleteValid_thenReturnNoContent() {
        webClient.delete().uri(BASE_URI + "/" + validVenueId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void whenDeleteWithInvalidId_thenReturnUnprocessableEntity() {
        webClient.delete().uri(BASE_URI + "/" + invalidVenueId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid venueId provided: " + invalidVenueId);
    }

    @Test
    public void whenDeleteNonExistent_thenReturnNotFound() {
        String randomId = UUID.randomUUID().toString();
        webClient.delete().uri(BASE_URI + "/" + randomId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    public void whenUpdateStateToScheduled_thenStateIsUpcoming() {
        // First save a venue with initial state
        Venue venue = new Venue("TestVenue", 1000, "TestCity", 2000, VenueState.UPCOMING);
        venue = venueRepository.save(venue);
        String testVenueId = venue.getVenueIdentifier().getVenueId();

        webClient.patch()
                .uri(BASE_URI + "/" + testVenueId + "/state")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(MatchStatus.SCHEDULED.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(VenueResponseModel.class)
                .value(vr -> {
                    assertEquals(VenueState.UPCOMING, vr.getVenueState());
                    assertEquals(testVenueId, vr.getVenueId());
                });
    }

    @Test
    public void whenUpdateStateToInProgress_thenStateIsLive() {
        // First save a venue with initial state
        Venue venue = new Venue("TestVenue", 1000, "TestCity", 2000, VenueState.UPCOMING);
        venue = venueRepository.save(venue);
        String testVenueId = venue.getVenueIdentifier().getVenueId();

        webClient.patch()
                .uri(BASE_URI + "/" + testVenueId + "/state")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(MatchStatus.IN_PROGRESS.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(VenueResponseModel.class)
                .value(vr -> {
                    assertEquals(VenueState.LIVE, vr.getVenueState());
                    assertEquals(testVenueId, vr.getVenueId());
                });
    }

    @Test
    public void whenUpdateStateToCanceled_thenStateIsCanceled() {
        // First save a venue with initial state
        Venue venue = new Venue("TestVenue", 1000, "TestCity", 2000, VenueState.UPCOMING);
        venue = venueRepository.save(venue);
        String testVenueId = venue.getVenueIdentifier().getVenueId();

        webClient.patch()
                .uri(BASE_URI + "/" + testVenueId + "/state")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(MatchStatus.CANCELED.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(VenueResponseModel.class)
                .value(vr -> {
                    assertEquals(VenueState.CANCELED, vr.getVenueState());
                    assertEquals(testVenueId, vr.getVenueId());
                });
    }
    @Test
    public void whenUpdateStateWithInvalidIdFormat_thenReturnUnprocessableEntity() {
        webClient.patch().uri(BASE_URI + "/" + invalidVenueId + "/state")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(MatchStatus.SCHEDULED)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid venueId provided: " + invalidVenueId);
    }
}
