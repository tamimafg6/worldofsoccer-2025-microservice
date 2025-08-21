package com.worldofsoccer.apigateway.domainclientlayer.league;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueResponseModel;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueRequestModel;
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

    public LeagueResponseModel getLeagueById(String leagueId) {
        try {
            String url = LEAGUE_SERVICE_BASE_URL + "/" + leagueId;
            log.debug("League-Service GET URL is: " + url);
            return restTemplate.getForObject(url, LeagueResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LeagueResponseModel createLeague(LeagueRequestModel leagueRequest) {
        try {
            String url = LEAGUE_SERVICE_BASE_URL;
            log.debug("League-Service POST URL is: " + url);
            return restTemplate.postForObject(url, leagueRequest, LeagueResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public LeagueResponseModel updateLeague(String leagueId, LeagueRequestModel leagueRequest) {
        try {
            String url = LEAGUE_SERVICE_BASE_URL + "/" + leagueId;
            log.debug("League-Service PUT URL is: " + url);
            restTemplate.put(url, leagueRequest);
            return getLeagueById(leagueId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteLeague(String leagueId) {
        try {
            String url = LEAGUE_SERVICE_BASE_URL + "/" + leagueId;
            log.debug("League-Service DELETE URL is: " + url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<LeagueResponseModel> getAllLeagues() {
        try {
            String url = LEAGUE_SERVICE_BASE_URL;
            log.debug("League-Service GET (all) URL is: " + url);
            LeagueResponseModel[] leaguesArray = restTemplate.getForObject(url, LeagueResponseModel[].class);
            if (leaguesArray != null) {
                return Arrays.asList(leaguesArray);
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
        log.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
}
