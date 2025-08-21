package com.worldofsoccer.match.domainclientLayer.league;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Slf4j
@Component
public class LeagueServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String LEAGUE_SERVICE_BASE_URL;

    public LeagueServiceClient(RestTemplate restTemplate,
                               ObjectMapper mapper,
                               @Value("${app.league-service.host}") String leagueServiceHost,
                               @Value("${app.league-service.port}") String leagueServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        LEAGUE_SERVICE_BASE_URL = "http://" + leagueServiceHost + ":" + leagueServicePort + "/api/v1/leagues";
    }

    public LeagueModel getLeagueById(String leagueId) {
        try {
            String url = LEAGUE_SERVICE_BASE_URL + "/" + leagueId;
            log.debug("League-Service GET URL is: {}", url);
            return restTemplate.getForObject(url, LeagueModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }





    public List<LeagueModel> getAllLeagues() {
        try {
            String url = LEAGUE_SERVICE_BASE_URL;
            log.debug("League-Service GET (all) URL is: {}", url);
            LeagueModel[] leaguesArray = restTemplate.getForObject(url, LeagueModel[].class);
            return leaguesArray != null ? Arrays.asList(leaguesArray) : Collections.emptyList();
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
        log.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
}