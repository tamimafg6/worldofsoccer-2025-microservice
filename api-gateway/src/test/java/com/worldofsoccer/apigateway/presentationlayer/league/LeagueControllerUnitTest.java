package com.worldofsoccer.apigateway.presentationlayer.league;

import com.worldofsoccer.apigateway.businesslayer.league.LeagueService;
import com.worldofsoccer.apigateway.domainclientlayer.league.FormatTypeEnum;
import com.worldofsoccer.apigateway.domainclientlayer.league.LeagueServiceClient;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueResponseModel;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import com.worldofsoccer.apigateway.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class LeagueControllerUnitTest {

    @Autowired
    private LeagueController controller;

    @MockitoBean
    private LeagueService service;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private LeagueServiceClient client;

    private static final String VALID_ID   = "11111111-1111-1111-1111-111111111111";
    private static final String INVALID_ID = "short";

    @Test
    void whenGetAll_thenReturnEmpty() {
        when(service.getAllLeagues()).thenReturn(Collections.emptyList());

        ResponseEntity<List<LeagueResponseModel>> resp = controller.getAllLeagues();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().isEmpty());
        verify(service).getAllLeagues();
    }

    @Test
    void whenGetById_invalidLength_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.getLeagueById(INVALID_ID)
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenGetById_valid_thenReturnOk() {
        LeagueResponseModel dto = new LeagueResponseModel();
        when(service.getLeagueById(VALID_ID)).thenReturn(dto);

        ResponseEntity<LeagueResponseModel> resp = controller.getLeagueById(VALID_ID);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).getLeagueById(VALID_ID);
    }

    @Test
    void whenCreate_thenReturnCreated() {
        LeagueRequestModel req = new LeagueRequestModel();
        LeagueResponseModel dto = new LeagueResponseModel();
        when(service.createLeague(req)).thenReturn(dto);

        ResponseEntity<LeagueResponseModel> resp = controller.createLeague(req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).createLeague(req);
    }

    @Test
    void whenUpdate_invalidId_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.updateLeague(INVALID_ID, new LeagueRequestModel())
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenUpdate_valid_thenReturnOk() {
        LeagueRequestModel req = new LeagueRequestModel();
        LeagueResponseModel dto = new LeagueResponseModel();
        when(service.updateLeague(VALID_ID, req)).thenReturn(dto);

        ResponseEntity<LeagueResponseModel> resp = controller.updateLeague(VALID_ID, req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).updateLeague(VALID_ID, req);
    }

    @Test
    void whenDelete_invalid_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.deleteLeague(INVALID_ID)
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenDelete_valid_thenReturnNoContent() {
        doNothing().when(service).deleteLeague(VALID_ID);

        ResponseEntity<Void> resp = controller.deleteLeague(VALID_ID);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(service).deleteLeague(VALID_ID);
    }


    @Test
    void getLeagueById_notFound_throwsNotFound() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "404", null,
                "{\"message\":\"no league\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(LeagueResponseModel.class)))
                .thenThrow(ex);

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> client.getLeagueById("abc"));
        assertEquals("no league", nfe.getMessage());
    }

    @Test
    void getLeagueById_unprocessable_throwsInvalidInput() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "422", null,
                "{\"message\":\"bad input\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(LeagueResponseModel.class)))
                .thenThrow(ex);

        InvalidInputException iie = assertThrows(InvalidInputException.class,
                () -> client.getLeagueById("abc"));
        assertEquals("bad input", iie.getMessage());
    }

    @Test
    void getAllLeagues_nullArray_returnsEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(LeagueResponseModel[].class)))
                .thenReturn(null);
        List<LeagueResponseModel> all = client.getAllLeagues();
        assertTrue(all.isEmpty());
    }

    @Test
    void formatTypeEnum_hasValues() {
        FormatTypeEnum[] vals = FormatTypeEnum.values();
        assertArrayEquals(
                new FormatTypeEnum[]{FormatTypeEnum.LEAGUE, FormatTypeEnum.TOURNAMENT, FormatTypeEnum.CUP},
                vals
        );
    }
}
