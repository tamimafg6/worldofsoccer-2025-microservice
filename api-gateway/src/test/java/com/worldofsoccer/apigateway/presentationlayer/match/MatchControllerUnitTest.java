package com.worldofsoccer.apigateway.presentationlayer.match;

import com.worldofsoccer.apigateway.businesslayer.match.MatchService;
import com.worldofsoccer.apigateway.domainclientlayer.match.MatchServiceClient;
import com.worldofsoccer.apigateway.domainclientlayer.match.MatchStatus;
import com.worldofsoccer.apigateway.domainclientlayer.match.ResultsType;
import com.worldofsoccer.apigateway.utils.HttpErrorInfo;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class MatchControllerUnitTest {

    @Autowired
    private MatchController controller;

    @MockitoBean
    private MatchService service;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private MatchServiceClient client;

    private static final String VALID_LEAGUE = "11111111-1111-1111-1111-111111111111";
    private static final String VALID_MATCH  = "22222222-2222-2222-2222-222222222222";
    private static final String INVALID_ID   = "bad";

    @Test
    void whenGetAll_invalidLeague_thenThrow() {
        assertThrows(InvalidInputException.class,
                () -> controller.getAll("short"));
        verifyNoInteractions(service);
    }

    @Test
    void whenGetAll_thenReturnEmptyList() {
        when(service.getAllMatches(VALID_LEAGUE)).thenReturn(Collections.emptyList());

        ResponseEntity<List<MatchResponseModel>> resp = controller.getAll(VALID_LEAGUE);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().isEmpty());
        verify(service).getAllMatches(VALID_LEAGUE);
    }

    @Test
    void whenGetOne_invalidIds_thenThrow() {
        assertThrows(InvalidInputException.class,
                () -> controller.getOne("short", VALID_MATCH));
        assertThrows(InvalidInputException.class,
                () -> controller.getOne(VALID_LEAGUE, "short"));
        verifyNoInteractions(service);
    }

    @Test
    void whenGetOne_thenReturnOk() {
        MatchResponseModel dto = new MatchResponseModel();
        when(service.getMatchById(VALID_LEAGUE, VALID_MATCH)).thenReturn(dto);

        ResponseEntity<MatchResponseModel> resp = controller.getOne(VALID_LEAGUE, VALID_MATCH);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).getMatchById(VALID_LEAGUE, VALID_MATCH);
    }

    @Test
    void whenCreate_invalidLeague_thenThrow() {
        assertThrows(InvalidInputException.class,
                () -> controller.create("short", new MatchRequestModel()));
        verifyNoInteractions(service);
    }

    @Test
    void whenCreate_thenReturnCreated() {
        MatchRequestModel req = new MatchRequestModel();
        MatchResponseModel dto = new MatchResponseModel();
        when(service.createMatch(VALID_LEAGUE, req)).thenReturn(dto);

        ResponseEntity<MatchResponseModel> resp = controller.create(VALID_LEAGUE, req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).createMatch(VALID_LEAGUE, req);
    }

    @Test
    void whenUpdate_invalidIds_thenThrow() {
        assertThrows(InvalidInputException.class,
                () -> controller.update("short", VALID_MATCH, new MatchRequestModel()));
        assertThrows(InvalidInputException.class,
                () -> controller.update(VALID_LEAGUE, "short", new MatchRequestModel()));
        verifyNoInteractions(service);
    }

    @Test
    void whenUpdate_thenReturnOk() {
        MatchRequestModel req = new MatchRequestModel();
        MatchResponseModel dto = new MatchResponseModel();
        when(service.updateMatch(VALID_LEAGUE, VALID_MATCH, req)).thenReturn(dto);

        ResponseEntity<MatchResponseModel> resp = controller.update(VALID_LEAGUE, VALID_MATCH, req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).updateMatch(VALID_LEAGUE, VALID_MATCH, req);
    }

    @Test
    void whenDelete_invalidIds_thenThrow() {
        assertThrows(InvalidInputException.class,
                () -> controller.delete("short", VALID_MATCH));
        assertThrows(InvalidInputException.class,
                () -> controller.delete(VALID_LEAGUE, "short"));
        verifyNoInteractions(service);
    }

    @Test
    void whenDelete_thenReturnNoContent() {
        doNothing().when(service).deleteMatch(VALID_LEAGUE, VALID_MATCH);

        ResponseEntity<Void> resp = controller.delete(VALID_LEAGUE, VALID_MATCH);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(service).deleteMatch(VALID_LEAGUE, VALID_MATCH);
    }

    // --------------------------------------------------
    // Below: client‐error‐handling tests for MatchServiceClient
    // --------------------------------------------------

    @Test
    void getMatchById_notFound_throwsNotFound() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "404", null,
                "{\"message\":\"no match\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(MatchResponseModel.class)))
                .thenThrow(ex);

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> client.getMatchById("L", "M"));
        assertEquals("no match", nfe.getMessage());
    }

    @Test
    void getMatchById_unprocessable_throwsInvalidInput() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "422", null,
                "{\"message\":\"bad input\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(MatchResponseModel.class)))
                .thenThrow(ex);

        InvalidInputException iie = assertThrows(InvalidInputException.class,
                () -> client.getMatchById("L", "M"));
        assertEquals("bad input", iie.getMessage());
    }

    @Test
    void getAllMatches_nullArray_returnsEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(MatchResponseModel[].class)))
                .thenReturn(null);
        List<MatchResponseModel> all = client.getAllMatches("L");
        assertTrue(all.isEmpty());
    }

    @Test
    void enums_haveValues() {
        assertArrayEquals(new MatchStatus[]{MatchStatus.SCHEDULED, MatchStatus.IN_PROGRESS, MatchStatus.COMPLETED, MatchStatus.CANCELED},
                MatchStatus.values());
        assertArrayEquals(new ResultsType[]{ResultsType.WINNER, ResultsType.LOSER, ResultsType.DRAW},
                ResultsType.values());
    }
}
