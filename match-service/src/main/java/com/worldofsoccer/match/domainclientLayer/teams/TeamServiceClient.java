package com.worldofsoccer.match.domainclientLayer.teams;


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
public class TeamServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String TEAM_SERVICE_BASE_URL;

    public TeamServiceClient(RestTemplate restTemplate,
                             ObjectMapper mapper,
                             @Value("${app.teams-service.host}") String teamsServiceHost,
                             @Value("${app.teams-service.port}") String teamsServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        TEAM_SERVICE_BASE_URL = "http://" + teamsServiceHost + ":" + teamsServicePort + "/api/v1/teams";
    }

    public TeamModel getTeamById(String teamId) {
        try {
            String url = TEAM_SERVICE_BASE_URL + "/" + teamId;
            log.debug("Team-Service GET URL is: " + url);
            return restTemplate.getForObject(url, TeamModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<TeamModel> getAllTeams() {
        try {
            String url = TEAM_SERVICE_BASE_URL;
            log.debug("Team-Service GET (all) URL is: " + url);
            TeamModel[] teamsArray = restTemplate.getForObject(url, TeamModel[].class);
            if (teamsArray != null) {
                return Arrays.asList(teamsArray);
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
        log.warn("Unexpected HTTP error: {}. Rethrowing.", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
}
