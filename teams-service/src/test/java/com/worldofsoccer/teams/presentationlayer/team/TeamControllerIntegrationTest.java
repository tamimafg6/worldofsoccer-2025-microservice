package com.worldofsoccer.teams.presentationlayer.team;


import com.worldofsoccer.teams.dataaccesslayer.team.Team;
import com.worldofsoccer.teams.dataaccesslayer.team.TeamRepository;
import com.worldofsoccer.teams.presentationlayer.team.TeamRequestModel;
import com.worldofsoccer.teams.presentationlayer.team.TeamResponseModel;
import com.worldofsoccer.teams.presentationlayer.player.PlayerRequestModel;
import com.worldofsoccer.teams.presentationlayer.player.PlayerResponseModel;
import com.worldofsoccer.teams.dataaccesslayer.player.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data-h2.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TeamControllerIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private TeamRepository teamRepository;

    private final String BASE_URI_TEAMS = "/api/v1/teams";
    private String validTeamId;
    private final String invalidTeamId = "1234";

    @BeforeEach
    public void setupDb() {
        teamRepository.deleteAll();
        Team team = new Team("Team Alpha", "Coach Alpha", 2000, BigDecimal.valueOf(1000000));
        teamRepository.save(team);
        validTeamId = team.getTeamIdentifier().getTeamId();
    }


    @Test
    public void whenValidTeamId_thenReturnTeam() {
        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TeamResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(validTeamId, response.getTeamId());
                });
    }

    @Test
    public void whenInvalidTeamIdLength_thenReturnInvalidInputException() {
        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + invalidTeamId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void whenTeamNotFound_thenReturnNotFoundException() {
        String nonExistentTeamId = "00000000-0000-0000-0000-000000000000";
        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + nonExistentTeamId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void whenValidTeamRequest_thenCreateTeam() {
        TeamRequestModel request = new TeamRequestModel("Team Y", "Coach Y", 2000, 750000.0, "IS_PLAYING");

        webClient.post()
                .uri(BASE_URI_TEAMS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TeamResponseModel.class)
                .value(response -> {
                    assertNotNull(response.getTeamId());
                    assertEquals("Team Y", response.getName());
                    assertEquals("Coach Y", response.getCoach());
                });
    }

    @Test
    public void whenDuplicateTeamName_thenReturnTeamAlreadyExistsException() {
        TeamRequestModel request = new TeamRequestModel("Team Alpha", "Coach New", 1995, 600000.0, "IS_PLAYING");

        webClient.post()
                .uri(BASE_URI_TEAMS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void whenValidUpdate_thenUpdateTeam() {
        TeamRequestModel updateRequest = new TeamRequestModel("Team Alpha Updated", "Coach Alpha", 2000, 550000.0, "IS_PLAYING");

        webClient.put()
                .uri(BASE_URI_TEAMS + "/" + validTeamId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TeamResponseModel.class)
                .value(response -> {
                    assertEquals("Team Alpha Updated", response.getName());
                });
    }

    @Test
    public void whenUpdateNonExistentTeam_thenReturnNotFoundException() {
        String nonExistentTeamId = "00000000-0000-0000-0000-000000000000";
        TeamRequestModel updateRequest = new TeamRequestModel("Non-existent", "Coach N", 2000, 1000000.0, "IS_PLAYING");

        webClient.put()
                .uri(BASE_URI_TEAMS + "/" + nonExistentTeamId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void whenValidTeamDeletion_thenReturnNoContent() {
        webClient.delete()
                .uri(BASE_URI_TEAMS + "/" + validTeamId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void whenDeleteNonExistentTeam_thenReturnNotFoundException() {
        String nonExistentTeamId = "00000000-0000-0000-0000-000000000000";
        webClient.delete()
                .uri(BASE_URI_TEAMS + "/" + nonExistentTeamId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void whenDeleteTeamWithInvalidId_thenReturnInvalidInputException() {
        webClient.delete()
                .uri(BASE_URI_TEAMS + "/" + invalidTeamId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }


    @Test
    public void whenValidPlayerRequest_thenCreatePlayerInTeam() {
        PlayerRequestModel request = new PlayerRequestModel("Sergio", "Ramos", 35, "Spain", 4, null);
        request.setPosition(Position.DEFENDER);

        webClient.post()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PlayerResponseModel.class)
                .value(response -> {
                    assertNotNull(response.getPlayerId());
                    assertEquals("Sergio", response.getFirstName());
                    assertEquals("Ramos", response.getLastName());
                });
    }

    @Test
    public void whenInvalidTeamId_thenReturnInvalidInputExceptionForPlayer() {
        PlayerRequestModel request = new PlayerRequestModel("David", "De Gea", 30, "Spain", 1, Position.GOALKEEPER);

        webClient.post()
                .uri(BASE_URI_TEAMS + "/" + invalidTeamId + "/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    @Test
    public void whenPlayerExists_thenReturnPlayerById() {
        PlayerRequestModel createRequest = new PlayerRequestModel("Kylian", "Mbappe", 22, "France", 7, Position.FORWARD);
        PlayerResponseModel createdPlayer = webClient.post()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PlayerResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdPlayer);
        String playerId = createdPlayer.getPlayerId();

        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PlayerResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(playerId, response.getPlayerId());
                    assertEquals("Kylian", response.getFirstName());
                });
    }

    @Test
    public void whenUpdatePlayerInTeam_thenReturnUpdatedPlayer() {
        PlayerRequestModel createRequest = new PlayerRequestModel("Luka", "Modric", 35, "Croatia", 10, Position.MIDFIELDER);
        PlayerResponseModel createdPlayer = webClient.post()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PlayerResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdPlayer);
        String playerId = createdPlayer.getPlayerId();

        PlayerRequestModel updateRequest = new PlayerRequestModel("Luka", "Modrić", 36, "Croatia", 10, Position.MIDFIELDER);

        webClient.put()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PlayerResponseModel.class)
                .value(response -> {
                    assertEquals("Modrić", response.getLastName());
                    assertEquals(36, response.getAge());
                });
    }

    @Test
    public void whenDeletePlayerFromTeam_thenReturnNoContent() {
        PlayerRequestModel createRequest = new PlayerRequestModel("Eden", "Hazard", 30, "Belgium", 10, Position.MIDFIELDER);
        PlayerResponseModel createdPlayer = webClient.post()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PlayerResponseModel.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdPlayer);
        String playerId = createdPlayer.getPlayerId();

        webClient.delete()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    public void whenGetPlayersOnEmptyTeam_thenReturnEmptyList() {
        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PlayerResponseModel.class)
                .hasSize(0);
    }


    @Test
    public void whenGetPlayerWithBadIdLength_thenReturnUnprocessableEntity() {
        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players/" + "short-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
    }

    @Test
    public void whenGetNonExistentPlayer_thenReturnNotFound() {
        String fakePlayerId = "00000000-0000-0000-0000-000000000000";
        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players/" + fakePlayerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void whenPostTeamWithoutName_thenReturnUnprocessableEntity() {
        TeamRequestModel missingName = new TeamRequestModel(null, "Coach X", 1990, 500_000.0, "IS_PLAYING");

        webClient.post()
                .uri(BASE_URI_TEAMS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(missingName)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Team name is required.");
    }


    @Test
    public void whenUpdateNonExistentPlayer_thenReturnNotFound() {
        String fakePlayerId = "00000000-0000-0000-0000-000000000000";
        PlayerRequestModel update = new PlayerRequestModel("X", "Y", 25, "Z", 99, Position.FORWARD);

        webClient.put()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players/" + fakePlayerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void whenGetTeamWithNullId_thenReturnInvalidInputException() {
        webClient.get()
                .uri(BASE_URI_TEAMS + "/null")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid teamId provided: null");
    }

    @Test
    public void whenCreatePlayerInNonexistentTeam_thenReturnNotFound() {
        String fakeTeam = UUID.randomUUID().toString();
        PlayerRequestModel req = new PlayerRequestModel("Cleo", "Brown", 22, "AUS", 5, Position.DEFENDER);

        webClient.post()
                .uri(BASE_URI_TEAMS + "/" + fakeTeam + "/players")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").value(msg -> assertTrue(((String)msg).contains("Team not found with ID:")));
    }

    @Test
    public void whenUpdatePlayerInNonexistentTeam_thenReturnNotFound() {
        String fakeTeam = UUID.randomUUID().toString();
        String fakePlayer = UUID.randomUUID().toString();
        PlayerRequestModel update = new PlayerRequestModel("D", "E", 30, "BRA", 3, Position.GOALKEEPER);

        webClient.put()
                .uri(BASE_URI_TEAMS + "/" + fakeTeam + "/players/" + fakePlayer)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").value(msg -> assertTrue(((String)msg).contains("Team not found with ID:")));
    }


    @Test
    public void whenDeletePlayerInNonexistentTeam_thenReturnNotFound() {
        String fakeTeam = UUID.randomUUID().toString();
        String fakePlayer = UUID.randomUUID().toString();

        webClient.delete()
                .uri(BASE_URI_TEAMS + "/" + fakeTeam + "/players/" + fakePlayer)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").value(msg -> assertTrue(((String)msg).contains("Player not found")));
    }

    @Test
    public void whenUpdateTeamWithInvalidId_thenReturnInvalidInputException() {
        TeamRequestModel any = new TeamRequestModel("X", "Y", 2000, 10_000.0, "IS_PLAYING");

        webClient.put()
                .uri(BASE_URI_TEAMS + "/" + invalidTeamId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Invalid teamId provided: " + invalidTeamId);
    }



    @Test
    public void whenGetPlayersWithBadTeamId_thenReturnUnprocessableEntity() {
        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + invalidTeamId + "/players")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid teamId provided: " + invalidTeamId);
    }

    @Test
    public void whenUpdatePlayerWithBadIds_thenReturnUnprocessableEntity() {
        PlayerRequestModel req = new PlayerRequestModel("X","Y",20,"ZZ",99,Position.GOALKEEPER);

        webClient.put()
                .uri(BASE_URI_TEAMS + "/" + invalidTeamId + "/players/" + invalidTeamId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid teamId or playerId provided.");
    }

    @Test
    public void whenDeletePlayerWithBadIds_thenReturnUnprocessableEntity() {
        webClient.delete()
                .uri(BASE_URI_TEAMS + "/" + invalidTeamId + "/players/" + invalidTeamId)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid teamId or playerId provided.");
    }


    @Test
    public void whenGetAllTeams_thenReturnListOfOne() {
        webClient.get()
                .uri(BASE_URI_TEAMS)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TeamResponseModel.class)
                .hasSize(1)
                .value(list -> assertEquals(validTeamId, list.get(0).getTeamId()));
    }

    @Test
    public void whenGetPlayersAfterCreate_thenReturnListOfOne() {
        PlayerRequestModel create = new PlayerRequestModel("Loop", "Tester", 28, "Nowhere", 99, Position.MIDFIELDER);
        PlayerResponseModel saved = webClient.post()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(create)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PlayerResponseModel.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(saved);

        webClient.get()
                .uri(BASE_URI_TEAMS + "/" + validTeamId + "/players")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(PlayerResponseModel.class)
                .hasSize(1)
                .value(list -> assertEquals(saved.getPlayerId(), list.get(0).getPlayerId()));
    }



}
