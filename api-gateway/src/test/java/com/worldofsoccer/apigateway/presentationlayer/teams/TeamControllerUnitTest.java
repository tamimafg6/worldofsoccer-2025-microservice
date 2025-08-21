package com.worldofsoccer.apigateway.presentationlayer.teams;

import com.worldofsoccer.apigateway.businesslayer.teams.TeamService;
import com.worldofsoccer.apigateway.domainclientlayer.teams.TeamServiceClient;
import com.worldofsoccer.apigateway.domainclientlayer.teams.TeamStatusEnum;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import com.worldofsoccer.apigateway.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class TeamControllerUnitTest {

    @Autowired
    private TeamController controller;

    @MockitoBean
    private TeamService service;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private TeamServiceClient client;

    private static final String VALID_ID   = "11111111-1111-1111-1111-111111111111";
    private static final String INVALID_ID = "bad-id";

    @Test
    void whenGetAll_thenReturnEmptyList() {
        when(service.getAllTeams()).thenReturn(Collections.emptyList());

        ResponseEntity<List<TeamResponseModel>> resp = controller.getAllTeams();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().isEmpty());
        verify(service).getAllTeams();
    }

    @Test
    void whenGetById_invalid_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.getTeamById(INVALID_ID)
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenGetById_valid_thenReturnOk() {
        TeamResponseModel dto = new TeamResponseModel();
        when(service.getTeamById(VALID_ID)).thenReturn(dto);

        ResponseEntity<TeamResponseModel> resp = controller.getTeamById(VALID_ID);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).getTeamById(VALID_ID);
    }

    @Test
    void whenCreate_thenReturnCreated() {
        TeamRequestModel req = new TeamRequestModel();
        TeamResponseModel dto = new TeamResponseModel();
        when(service.createTeam(req)).thenReturn(dto);

        ResponseEntity<TeamResponseModel> resp = controller.createTeam(req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).createTeam(req);
    }

    @Test
    void whenUpdate_invalid_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.updateTeam(INVALID_ID, new TeamRequestModel())
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenUpdate_valid_thenReturnOk() {
        TeamRequestModel req = new TeamRequestModel();
        TeamResponseModel dto = new TeamResponseModel();
        when(service.updateTeam(VALID_ID, req)).thenReturn(dto);

        ResponseEntity<TeamResponseModel> resp = controller.updateTeam(VALID_ID, req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).updateTeam(VALID_ID, req);
    }

    @Test
    void whenDelete_invalid_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.deleteTeam(INVALID_ID)
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenDelete_valid_thenReturnNoContent() {
        doNothing().when(service).deleteTeam(VALID_ID);

        ResponseEntity<Void> resp = controller.deleteTeam(VALID_ID);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(service).deleteTeam(VALID_ID);
    }

    @Test
    void getTeamById_notFound_throwsNotFoundException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "404", null,
                "{\"message\":\"no team\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(TeamResponseModel.class)))
                .thenThrow(ex);

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> client.getTeamById("abc"));
        assertEquals("no team", nfe.getMessage());
    }

    @Test
    void getTeamById_unprocessable_throwsInvalidInputException() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "422", null,
                "{\"message\":\"bad input\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(TeamResponseModel.class)))
                .thenThrow(ex);

        InvalidInputException iie = assertThrows(InvalidInputException.class,
                () -> client.getTeamById("abc"));
        assertEquals("bad input", iie.getMessage());
    }

    @Test
    void getAllTeams_nullArray_returnsEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(TeamResponseModel[].class)))
                .thenReturn(null);
        List<TeamResponseModel> all = client.getAllTeams();
        assertTrue(all.isEmpty());
    }

    @Test
    void teamStatusEnum_hasValues() {
        TeamStatusEnum[] vals = TeamStatusEnum.values();
        assertArrayEquals(new TeamStatusEnum[]{TeamStatusEnum.IS_PLAYING, TeamStatusEnum.RESTING}, vals);
    }
}
