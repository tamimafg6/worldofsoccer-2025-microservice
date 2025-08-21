package com.worldofsoccer.apigateway.domainclientlayer.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueResponseModel;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueRequestModel;
import com.worldofsoccer.apigateway.utils.HttpErrorInfo;
import com.worldofsoccer.apigateway.utils.exceptions.NotFoundException;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class VenueServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String VENUE_SERVICE_BASE_URL;

    public VenueServiceClient(RestTemplate restTemplate,
                              ObjectMapper mapper,
                              @Value("${app.location-service.host}") String locationServiceHost,
                              @Value("${app.location-service.port}") String locationServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        VENUE_SERVICE_BASE_URL = "http://" + locationServiceHost + ":" + locationServicePort + "/api/v1/venues";
    }

    public VenueResponseModel getVenueById(String venueId) {
        try {
            String url = VENUE_SERVICE_BASE_URL + "/" + venueId;
            log.debug("Venue-Service GET URL is: " + url);
            return restTemplate.getForObject(url, VenueResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public VenueResponseModel createVenue(VenueRequestModel venueRequest) {
        try {
            String url = VENUE_SERVICE_BASE_URL;
            log.debug("Venue-Service POST URL is: " + url);
            return restTemplate.postForObject(url, venueRequest, VenueResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public VenueResponseModel updateVenue(String venueId, VenueRequestModel venueRequest) {
        try {
            String url = VENUE_SERVICE_BASE_URL + "/" + venueId;
            log.debug("Venue-Service PUT URL is: " + url);
            restTemplate.put(url, venueRequest);
            return getVenueById(venueId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteVenue(String venueId) {
        try {
            String url = VENUE_SERVICE_BASE_URL + "/" + venueId;
            log.debug("Venue-Service DELETE URL is: " + url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<VenueResponseModel> getAllVenues() {
        try {
            String url = VENUE_SERVICE_BASE_URL;
            log.debug("Venue-Service GET (all) URL is: " + url);
            VenueResponseModel[] venuesArray = restTemplate.getForObject(url, VenueResponseModel[].class);
            if (venuesArray != null) {
                return Arrays.asList(venuesArray);
            }
            return Collections.emptyList();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ioex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }
        log.warn("Unexpected HTTP error: {}. Rethrowing", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
}