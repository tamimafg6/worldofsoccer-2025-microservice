package com.worldofsoccer.teams.businesslayer.team;


import com.worldofsoccer.teams.presentationlayer.team.TeamRequestModel;
import com.worldofsoccer.teams.presentationlayer.team.TeamResponseModel;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<TeamResponseModel> getAllTeams();
    TeamResponseModel getTeamById(UUID teamId);
    TeamResponseModel createTeam(TeamRequestModel teamRequestModel);
    TeamResponseModel updateTeam(UUID teamId, TeamRequestModel teamRequestModel);
    void deleteTeam(UUID teamId);
}
