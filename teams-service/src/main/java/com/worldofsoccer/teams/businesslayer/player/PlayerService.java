package com.worldofsoccer.teams.businesslayer.player;


import com.worldofsoccer.teams.presentationlayer.player.PlayerRequestModel;
import com.worldofsoccer.teams.presentationlayer.player.PlayerResponseModel;

import java.util.List;

public interface PlayerService {

    List<PlayerResponseModel> getPlayersByTeamId(String teamId);
    PlayerResponseModel getPlayerById(String teamId, String playerId);
    PlayerResponseModel createPlayerInTeam(String teamId, PlayerRequestModel playerRequestModel);
    PlayerResponseModel updatePlayerInTeam(String teamId, String playerId, PlayerRequestModel playerRequestModel);
    void deletePlayerFromTeam(String teamId, String playerId);
}
