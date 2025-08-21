package com.worldofsoccer.match.domainclientLayer.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldofsoccer.match.dataAccessLayer.MatchStatus;
import com.worldofsoccer.match.utils.HttpErrorInfo;
import com.worldofsoccer.match.utils.exceptions.InvalidInputException;
import com.worldofsoccer.match.utils.exceptions.NotFoundException;
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

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Component
public class VenueServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String VENUE_SERVICE_BASE_URL;

    public VenueServiceClient(RestTemplate restTemplate,
                              ObjectMapper mapper,
                              @Value("${app.location-service.host}") String host,
                              @Value("${app.location-service.port}") String port) {
        this.restTemplate = restTemplate;
        this.mapper       = mapper;
        this.VENUE_SERVICE_BASE_URL = "http://" + host + ":" + port + "/api/v1/venues";
    }

    public VenueModel getVenueById(String venueId) {
        log.debug("Calling Location-Service GET /venues/{}", venueId);
        try {
            // pull raw JSON so we can re-map it
            String json = restTemplate.getForObject(
                    VENUE_SERVICE_BASE_URL + "/" + venueId,
                    String.class
            );
            return ACLVenueModelFromJson(json);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing Venue JSON", e);
        }
    }

    public List<VenueModel> getAllVenues() {
        try {
            String url = VENUE_SERVICE_BASE_URL;
            log.debug("Location-Service GET (all) URL is: {}", url);
            VenueModel[] venuesArray = restTemplate.getForObject(url, VenueModel[].class);
            return venuesArray != null ? Arrays.asList(venuesArray) : Collections.emptyList();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }



    public VenueModel patchVenueState(String venueId, MatchStatus newStatus) {
        String url = VENUE_SERVICE_BASE_URL + "/" + venueId + "/state";
        log.debug("PATCHing venue state to {} at {}", newStatus, url);

        try {
            // send the text body "SCHEDULED", "IN_PROGRESS", etc.
            String json = restTemplate.patchForObject(url, newStatus.name(), String.class);
            // now json is the JSON of the updated VenueResponseModel
            return mapper.readValue(json, VenueModel.class);

        } catch (HttpClientErrorException ex) {
            // wrap 404 → NotFoundException, 422 → InvalidInputException, etc.
            throw handleHttpClientException(ex);

        } catch (IOException e) {
            // malformed JSON → bubble as 500
            throw new RuntimeException("Unable to parse updated VenueModel", e);
        }
    }


    private VenueModel ACLVenueModelFromJson(String response) throws JsonProcessingException {
        JsonNode n = mapper.readTree(response);

        // these must exactly match your Location-service’s JSON property names:
        String id       = n.get("venueId").asText();
        String name     = n.get("name").asText();
        String city     = n.get("city").asText();
        int    capacity = n.get("capacity").asInt();
        String state    = n.get("venueState").asText();

        return VenueModel.builder()
                .venueId(id)
                .venueName(name)
                .venueCity(city)
                .venueCapacity(capacity)
                .venueState(state)
                .build();
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        String msg;
        try {
            msg = mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException io) {
            msg = io.getMessage();
        }
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(msg);
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(msg);
        }
        log.warn("Unexpected HTTP error from Location-Service: {} – {}",
                ex.getStatusCode(), ex.getResponseBodyAsString());
        return ex;
    }
}
