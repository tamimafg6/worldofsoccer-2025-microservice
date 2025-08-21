package com.worldofsoccer.apigateway.domainclientlayer.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldofsoccer.apigateway.presentationlayer.match.MatchRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.match.MatchResponseModel;
import com.worldofsoccer.apigateway.utils.HttpErrorInfo;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import com.worldofsoccer.apigateway.utils.exceptions.NotFoundException;
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
public class MatchServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String BASE;  // e.g. http://localhost:8080/api/v1/leagues

    public MatchServiceClient(RestTemplate restTemplate,
                              ObjectMapper mapper,
                              @Value("${app.match-service.host}") String host,
                              @Value("${app.match-service.port}") String port) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.BASE = "http://" + host + ":" + port + "/api/v1/leagues";
    }

    public List<MatchResponseModel> getAllMatches(String leagueId) {
        try {
            String url = BASE + "/" + leagueId + "/matches";
            MatchResponseModel[] arr = restTemplate.getForObject(url, MatchResponseModel[].class);
            return arr != null ? Arrays.asList(arr) : Collections.emptyList();
        } catch (HttpClientErrorException ex) {
            throw handle(ex);
        }
    }

    public MatchResponseModel getMatchById(String leagueId, String matchId) {
        try {
            String url = BASE + "/" + leagueId + "/matches/" + matchId;
            return restTemplate.getForObject(url, MatchResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handle(ex);
        }
    }

    public MatchResponseModel createMatch(String leagueId, MatchRequestModel req) {
        try {
            String url = BASE + "/" + leagueId + "/matches";
            return restTemplate.postForObject(url, req, MatchResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handle(ex);
        }
    }

    public MatchResponseModel updateMatch(String leagueId, String matchId, MatchRequestModel req) {
        try {
            String url = BASE + "/" + leagueId + "/matches/" + matchId;
            restTemplate.put(url, req);
            return getMatchById(leagueId, matchId);
        } catch (HttpClientErrorException ex) {
            throw handle(ex);
        }
    }

    public void deleteMatch(String leagueId, String matchId) {
        try {
            String url = BASE + "/" + leagueId + "/matches/" + matchId;
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handle(ex);
        }
    }

    private RuntimeException handle(HttpClientErrorException ex) {
        String msg = extractMessage(ex);
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new NotFoundException(msg);
        }
        if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(msg);
        }
        log.warn("Unexpected HTTP error from match-service: {}", ex.getStatusCode());
        return ex;
    }

    private String extractMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException e) {
            return ex.getMessage();
        }
    }
}
