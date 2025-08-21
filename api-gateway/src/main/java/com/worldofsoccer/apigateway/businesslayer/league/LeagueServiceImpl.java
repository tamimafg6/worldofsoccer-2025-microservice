package com.worldofsoccer.apigateway.businesslayer.league;

import com.worldofsoccer.apigateway.domainclientlayer.league.LeagueServiceClient;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueController;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Slf4j
public class LeagueServiceImpl implements LeagueService {

    private final LeagueServiceClient leagueServiceClient;

    public LeagueServiceImpl(LeagueServiceClient leagueServiceClient) {
        this.leagueServiceClient = leagueServiceClient;
    }

    @Override
    public LeagueResponseModel getLeagueById(String leagueId) {
        log.debug("Business Layer: Fetching league with id: {}", leagueId);
        LeagueResponseModel league = leagueServiceClient.getLeagueById(leagueId);
        return addHateoasLinks(league);
    }

    @Override
    public LeagueResponseModel createLeague(LeagueRequestModel leagueRequest) {
        log.debug("Business Layer: Creating new league");
        LeagueResponseModel createdLeague = leagueServiceClient.createLeague(leagueRequest);
        return addHateoasLinks(createdLeague);
    }

    @Override
    public LeagueResponseModel updateLeague(String leagueId, LeagueRequestModel leagueRequest) {
        log.debug("Business Layer: Updating league with id: {}", leagueId);
        LeagueResponseModel updatedLeague = leagueServiceClient.updateLeague(leagueId, leagueRequest);
        return addHateoasLinks(updatedLeague);
    }

    @Override
    public void deleteLeague(String leagueId) {
        log.debug("Business Layer: Deleting league with id: {}", leagueId);
        leagueServiceClient.deleteLeague(leagueId);
    }

    @Override
    public List<LeagueResponseModel> getAllLeagues() {
        log.debug("Business Layer: Fetching all leagues");
        List<LeagueResponseModel> leagues = leagueServiceClient.getAllLeagues();
        for (LeagueResponseModel league : leagues) {
            addHateoasLinks(league);
        }
        return leagues;
    }

    private LeagueResponseModel addHateoasLinks(LeagueResponseModel league) {
        Link selfLink = linkTo(methodOn(LeagueController.class)
                .getLeagueById(league.getLeagueId()))
                .withSelfRel();
        league.add(selfLink);

        Link allLeaguesLink = linkTo(methodOn(LeagueController.class)
                .getAllLeagues())
                .withRel("allLeagues");
        league.add(allLeaguesLink);

        return league;
    }
}