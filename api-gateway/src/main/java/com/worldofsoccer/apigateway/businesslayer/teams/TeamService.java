package com.worldofsoccer.apigateway.businesslayer.teams;

import com.worldofsoccer.apigateway.presentationlayer.teams.TeamRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.teams.TeamResponseModel;

import java.util.List;

public interface TeamService {
    TeamResponseModel getTeamById(String teamId);
    TeamResponseModel createTeam(TeamRequestModel teamRequest);
    TeamResponseModel updateTeam(String teamId, TeamRequestModel teamRequest);
    void deleteTeam(String teamId);
    List<TeamResponseModel> getAllTeams();
}
