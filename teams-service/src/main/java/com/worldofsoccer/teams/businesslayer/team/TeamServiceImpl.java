package com.worldofsoccer.teams.businesslayer.team;

import com.worldofsoccer.teams.dataaccesslayer.mappinglayer.TeamRequestMapper;
import com.worldofsoccer.teams.dataaccesslayer.mappinglayer.TeamResponseMapper;
import com.worldofsoccer.teams.dataaccesslayer.team.Team;
import com.worldofsoccer.teams.dataaccesslayer.team.TeamIdentifier;
import com.worldofsoccer.teams.dataaccesslayer.team.TeamRepository;
import com.worldofsoccer.teams.presentationlayer.team.TeamController;
import com.worldofsoccer.teams.presentationlayer.team.TeamRequestModel;
import com.worldofsoccer.teams.presentationlayer.team.TeamResponseModel;
import com.worldofsoccer.teams.utils.exceptions.InvalidInputException;
import com.worldofsoccer.teams.utils.exceptions.NotFoundException;
import com.worldofsoccer.teams.utils.exceptions.TeamAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamRequestMapper teamRequestMapper;
    private final TeamResponseMapper teamResponseMapper;

    @Override
    public List<TeamResponseModel> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        List<TeamResponseModel> responseList = new ArrayList<>();
        for (Team team : teams) {
            TeamResponseModel response = teamResponseMapper.entityToResponseModel(team);
            addLinks(response, team);
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public TeamResponseModel getTeamById(UUID teamId) {
        Team team = teamRepository.findByTeamIdentifier_TeamId(teamId.toString());
        if (team == null) {
            throw new NotFoundException("Team not found with ID: " + teamId);
        }
        TeamResponseModel response = teamResponseMapper.entityToResponseModel(team);
        addLinks(response, team);
        return response;
    }

    @Override
    public TeamResponseModel createTeam(TeamRequestModel teamRequestModel) {
        if (teamRequestModel.getTeamName() == null) {
            throw new InvalidInputException("Team name is required.");
        }

        Team existingTeam = teamRepository.findByTeamName(teamRequestModel.getTeamName());
        if (existingTeam != null) {
            throw new TeamAlreadyExistsException("A team with the name " + teamRequestModel.getTeamName() + " already exists.");
        }

        Team team = teamRequestMapper.requestModelToEntity(teamRequestModel, new TeamIdentifier());
        Team savedTeam = teamRepository.save(team);
        TeamResponseModel response = teamResponseMapper.entityToResponseModel(savedTeam);
        addLinks(response, savedTeam);
        return response;
    }

    @Override
    public TeamResponseModel updateTeam(UUID teamId, TeamRequestModel teamRequestModel) {
        Team team = teamRepository.findByTeamIdentifier_TeamId(teamId.toString());
        if (team == null) {
            throw new NotFoundException("Team not found with ID: " + teamId);
        }

        if (teamRequestModel.getTeamName() != null) {
            Team existingTeam = teamRepository.findByTeamName(teamRequestModel.getTeamName());
            if (existingTeam != null && !existingTeam.getTeamIdentifier().getTeamId().equals(teamId.toString())) {
                throw new TeamAlreadyExistsException("A team with the name " + teamRequestModel.getTeamName() + " already exists.");
            }
        }

        teamRequestMapper.updateEntity(teamRequestModel, team);
        Team updatedTeam = teamRepository.save(team);
        TeamResponseModel response = teamResponseMapper.entityToResponseModel(updatedTeam);
        addLinks(response, updatedTeam);
        return response;
    }
    @Override
    public void deleteTeam(UUID teamId) {
        Team team = teamRepository.findByTeamIdentifier_TeamId(teamId.toString());
        if (team == null) {
            throw new NotFoundException("Team not found with ID: " + teamId);
        }
        teamRepository.delete(team);
    }

    private void addLinks(TeamResponseModel responseModel, Team team) {
        Link selfLink = linkTo(methodOn(TeamController.class)
                .getTeamById(responseModel.getTeamId())).withSelfRel();
        responseModel.add(selfLink);

        Link allTeamsLink = linkTo(methodOn(TeamController.class)
                .getAllTeams()).withRel("allTeams");
        responseModel.add(allTeamsLink);
    }
}
