package com.worldofsoccer.apigateway.businesslayer.match;

import com.worldofsoccer.apigateway.domainclientlayer.match.MatchServiceClient;
import com.worldofsoccer.apigateway.presentationlayer.match.MatchController;
import com.worldofsoccer.apigateway.presentationlayer.match.MatchRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.match.MatchResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    private final MatchServiceClient client;

    public MatchServiceImpl(MatchServiceClient client) {
        this.client = client;
    }

    @Override
    public List<MatchResponseModel> getAllMatches(String leagueId) {
        log.debug("Business Layer: Fetching all matches for league {}", leagueId);
        List<MatchResponseModel> matches = client.getAllMatches(leagueId);
        for (MatchResponseModel match : matches) {
            addHateoasLinks(match, leagueId);
        }
        return matches;
    }

    @Override
    public MatchResponseModel getMatchById(String leagueId, String matchId) {
        log.debug("Business Layer: Fetching match {} for league {}", matchId, leagueId);
        MatchResponseModel match = client.getMatchById(leagueId, matchId);
        return addHateoasLinks(match, leagueId);
    }

    @Override
    public MatchResponseModel createMatch(String leagueId, MatchRequestModel request) {
        log.debug("Business Layer: Creating match in league {}", leagueId);
        MatchResponseModel createdMatch = client.createMatch(leagueId, request);
        return addHateoasLinks(createdMatch, leagueId);
    }

    @Override
    public MatchResponseModel updateMatch(String leagueId, String matchId, MatchRequestModel request) {
        log.debug("Business Layer: Updating match {} in league {}", matchId, leagueId);
        MatchResponseModel updatedMatch = client.updateMatch(leagueId, matchId, request);
        return addHateoasLinks(updatedMatch, leagueId);
    }

    @Override
    public void deleteMatch(String leagueId, String matchId) {
        log.debug("Business Layer: Deleting match {} from league {}", matchId, leagueId);
        client.deleteMatch(leagueId, matchId);
    }

    private MatchResponseModel addHateoasLinks(MatchResponseModel match, String leagueId) {
        Link selfLink = linkTo(methodOn(MatchController.class)
                .getOne(leagueId, match.getMatchId()))
                .withSelfRel();
        match.add(selfLink);

        Link allMatchesLink = linkTo(methodOn(MatchController.class)
                .getAll(leagueId))
                .withRel("allMatches");
        match.add(allMatchesLink);

        return match;
    }
}