package com.worldofsoccer.apigateway.businesslayer.teams;

import com.worldofsoccer.apigateway.domainclientlayer.teams.TeamServiceClient;
import com.worldofsoccer.apigateway.presentationlayer.teams.TeamController;
import com.worldofsoccer.apigateway.presentationlayer.teams.TeamRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.teams.TeamResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Slf4j
public class TeamServiceImpl implements TeamService {

    private final TeamServiceClient teamServiceClient;

    public TeamServiceImpl(TeamServiceClient teamServiceClient) {
        this.teamServiceClient = teamServiceClient;
    }

    @Override
    public TeamResponseModel getTeamById(String teamId) {
        log.debug("Business Layer: Fetching team with id: {}", teamId);
        TeamResponseModel team = teamServiceClient.getTeamById(teamId);
        return addHateoasLinks(team);
    }

    @Override
    public TeamResponseModel createTeam(TeamRequestModel teamRequest) {
        log.debug("Business Layer: Creating new team");
        TeamResponseModel createdTeam = teamServiceClient.createTeam(teamRequest);
        return addHateoasLinks(createdTeam);
    }

    @Override
    public TeamResponseModel updateTeam(String teamId, TeamRequestModel teamRequest) {
        log.debug("Business Layer: Updating team with id: {}", teamId);
        TeamResponseModel updatedTeam = teamServiceClient.updateTeam(teamId, teamRequest);
        return addHateoasLinks(updatedTeam);
    }

    @Override
    public void deleteTeam(String teamId) {
        log.debug("Business Layer: Deleting team with id: {}", teamId);
        teamServiceClient.deleteTeam(teamId);
    }

    @Override
    public List<TeamResponseModel> getAllTeams() {
        log.debug("Business Layer: Fetching all teams");
        List<TeamResponseModel> teams = teamServiceClient.getAllTeams();
        for (TeamResponseModel team : teams) {
            addHateoasLinks(team);
        }
        return teams;
    }

    private TeamResponseModel addHateoasLinks(TeamResponseModel team) {
        Link selfLink = linkTo(methodOn(TeamController.class)
                .getTeamById(team.getTeamId()))
                .withSelfRel();
        team.add(selfLink);

        Link allTeamsLink = linkTo(methodOn(TeamController.class)
                .getAllTeams())
                .withRel("allTeams");
        team.add(allTeamsLink);

        return team;
    }
}