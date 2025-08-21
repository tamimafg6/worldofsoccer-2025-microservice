package com.worldofsoccer.apigateway.presentationlayer.location;

import com.worldofsoccer.apigateway.businesslayer.location.VenueService;
import com.worldofsoccer.apigateway.domainclientlayer.location.VenueServiceClient;
import com.worldofsoccer.apigateway.domainclientlayer.location.VenueStateEnum;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueResponseModel;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class VenueControllerUnitTest {

    @Autowired
    private VenueController controller;

    @MockitoBean
    private VenueService service;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private VenueServiceClient client;

    private static final String VALID_ID   = "11111111-1111-1111-1111-111111111111";
    private static final String INVALID_ID = "bad-id";

    @Test
    void whenGetAll_thenReturnEmptyList() {
        when(service.getAllVenues()).thenReturn(Collections.emptyList());

        ResponseEntity<List<VenueResponseModel>> resp = controller.getAllVenues();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().isEmpty());
        verify(service).getAllVenues();
    }

    @Test
    void whenGetById_invalid_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.getVenueById(INVALID_ID)
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenGetById_valid_thenReturnOk() {
        VenueResponseModel dto = new VenueResponseModel();
        when(service.getVenueById(VALID_ID)).thenReturn(dto);

        ResponseEntity<VenueResponseModel> resp = controller.getVenueById(VALID_ID);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).getVenueById(VALID_ID);
    }

    @Test
    void whenCreate_thenReturnCreated() {
        VenueRequestModel req = new VenueRequestModel();
        VenueResponseModel dto = new VenueResponseModel();
        when(service.createVenue(req)).thenReturn(dto);

        ResponseEntity<VenueResponseModel> resp = controller.createVenue(req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).createVenue(req);
    }

    @Test
    void whenUpdate_invalid_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.updateVenue(INVALID_ID, new VenueRequestModel())
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenUpdate_valid_thenReturnOk() {
        VenueRequestModel req = new VenueRequestModel();
        VenueResponseModel dto = new VenueResponseModel();
        when(service.updateVenue(VALID_ID, req)).thenReturn(dto);

        ResponseEntity<VenueResponseModel> resp = controller.updateVenue(VALID_ID, req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(dto, resp.getBody());
        verify(service).updateVenue(VALID_ID, req);
    }

    @Test
    void whenDelete_invalid_thenThrow() {
        assertThrows(
                InvalidInputException.class,
                () -> controller.deleteVenue(INVALID_ID)
        );
        verifyNoInteractions(service);
    }

    @Test
    void whenDelete_valid_thenReturnNoContent() {
        doNothing().when(service).deleteVenue(VALID_ID);

        ResponseEntity<Void> resp = controller.deleteVenue(VALID_ID);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(service).deleteVenue(VALID_ID);
    }

    // --------------------------------------------------
    // Below: client‐error‐handling tests for VenueServiceClient
    // --------------------------------------------------

    @Test
    void getVenueById_notFound_throwsNotFound() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "404", null,
                "{\"message\":\"no venue\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(VenueResponseModel.class)))
                .thenThrow(ex);

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> client.getVenueById("abc"));
        assertEquals("no venue", nfe.getMessage());
    }

    @Test
    void getVenueById_unprocessable_throwsInvalidInput() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "422", null,
                "{\"message\":\"bad input\"}".getBytes(), null);
        when(restTemplate.getForObject(anyString(), eq(VenueResponseModel.class)))
                .thenThrow(ex);

        InvalidInputException iie = assertThrows(InvalidInputException.class,
                () -> client.getVenueById("abc"));
        assertEquals("bad input", iie.getMessage());
    }

    @Test
    void getAllVenues_nullArray_returnsEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(VenueResponseModel[].class)))
                .thenReturn(null);
        List<VenueResponseModel> all = client.getAllVenues();
        assertTrue(all.isEmpty());
    }

    @Test
    void venueStateEnum_hasValues() {
        VenueStateEnum[] vals = VenueStateEnum.values();
        assertArrayEquals(
                new VenueStateEnum[]{VenueStateEnum.UPCOMING, VenueStateEnum.LIVE, VenueStateEnum.PAST, VenueStateEnum.CANCELED},
                vals
        );
    }
}
