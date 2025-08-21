package com.worldofsoccer.teams.businesslayer.player;

import com.worldofsoccer.teams.dataaccesslayer.mappinglayer.PlayerRequestMapper;
import com.worldofsoccer.teams.dataaccesslayer.mappinglayer.PlayerResponseMapper;
import com.worldofsoccer.teams.dataaccesslayer.player.Player;
import com.worldofsoccer.teams.dataaccesslayer.player.PlayerIdentifier;
import com.worldofsoccer.teams.dataaccesslayer.player.PlayerRepository;
import com.worldofsoccer.teams.dataaccesslayer.team.Team;
import com.worldofsoccer.teams.dataaccesslayer.team.TeamRepository;
import com.worldofsoccer.teams.presentationlayer.player.PlayerRequestModel;
import com.worldofsoccer.teams.presentationlayer.player.PlayerResponseModel;
import com.worldofsoccer.teams.presentationlayer.player.PlayerController;
import com.worldofsoccer.teams.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRequestMapper playerRequestMapper;
    private final PlayerResponseMapper playerResponseMapper;

    @Override
    public List<PlayerResponseModel> getPlayersByTeamId(String teamId) {
        List<Player> players = playerRepository.findAllByTeamIdentifier_TeamId(teamId);
        List<PlayerResponseModel> responseList = new ArrayList<>();
        for (Player player : players) {
            PlayerResponseModel response = playerResponseMapper.entityToResponseModel(player);
            addLinks(response, player);
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public PlayerResponseModel getPlayerById(String teamId, String playerId) {
        Player player = playerRepository.findByPlayerIdentifier_PlayerIdAndTeamIdentifier_TeamId(playerId, teamId);
        if (player == null) {
            throw new NotFoundException("Player not found with ID: " + playerId + " in Team: " + teamId);
        }
        PlayerResponseModel response = playerResponseMapper.entityToResponseModel(player);
        addLinks(response, player);
        return response;
    }

    @Override
    public PlayerResponseModel createPlayerInTeam(String teamId, PlayerRequestModel playerRequestModel) {
        Team team = teamRepository.findByTeamIdentifier_TeamId(teamId);
        if (team == null) {
            throw new NotFoundException("Team not found with ID: " + teamId);
        }
        Player player = playerRequestMapper.requestModelToEntity(playerRequestModel, new PlayerIdentifier());
        player.setTeamIdentifier(team.getTeamIdentifier());
        playerRepository.save(player);
        PlayerResponseModel response = playerResponseMapper.entityToResponseModel(player);
        addLinks(response, player);
        return response;
    }

    @Override
    public PlayerResponseModel updatePlayerInTeam(String teamId, String playerId, PlayerRequestModel playerRequestModel) {
        Team team = teamRepository.findByTeamIdentifier_TeamId(teamId);
        if (team == null) {
            throw new NotFoundException("Team not found with ID: " + teamId);
        }
        Player player = playerRepository.findByPlayerIdentifier_PlayerIdAndTeamIdentifier_TeamId(playerId, teamId);
        if (player == null) {
            throw new NotFoundException("Player not found with ID: " + playerId + " in Team: " + teamId);
        }
        playerRequestMapper.updateEntity(playerRequestModel, player);
        player.setTeamIdentifier(team.getTeamIdentifier());
        playerRepository.save(player);
        PlayerResponseModel response = playerResponseMapper.entityToResponseModel(player);
        addLinks(response, player);
        return response;
    }

    @Override
    public void deletePlayerFromTeam(String teamId, String playerId) {
        Player player = playerRepository.findByPlayerIdentifier_PlayerIdAndTeamIdentifier_TeamId(playerId, teamId);
        if (player == null) {
            throw new NotFoundException("Player not found with ID: " + playerId + " in Team: " + teamId);
        }
        playerRepository.delete(player);
    }

    private void addLinks(PlayerResponseModel responseModel, Player player) {
        String teamId = player.getTeamIdentifier().getTeamId();
        String playerId = responseModel.getPlayerId();

        Link selfLink = linkTo(methodOn(PlayerController.class)
                .getPlayerById(teamId, playerId))
                .withSelfRel();
        responseModel.add(selfLink);

        Link allPlayersInTeamLink = linkTo(methodOn(PlayerController.class)
                .getPlayersByTeamID(teamId))
                .withRel("allPlayersInTeam");
        responseModel.add(allPlayersInTeamLink);
    }
}
