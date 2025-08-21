package com.worldofsoccer.teams.presentationlayer.player;


import com.worldofsoccer.teams.businesslayer.player.PlayerService;
import com.worldofsoccer.teams.utils.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/teams/{teamId}/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private static final int UUID_LENGTH = 36;

    @GetMapping()
    public ResponseEntity<List<PlayerResponseModel>> getPlayersByTeamID(@PathVariable String teamId) {
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        return ResponseEntity.ok().body(playerService.getPlayersByTeamId(teamId));
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponseModel> getPlayerById(@PathVariable("teamId") String teamId, @PathVariable("playerId") String playerId) {
        if (teamId.length() != UUID_LENGTH || playerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId or playerId provided.");
        }
        return ResponseEntity.ok().body(playerService.getPlayerById(teamId, playerId));
    }


    @PostMapping()
    public ResponseEntity<PlayerResponseModel> createPlayerInTeam(@PathVariable String teamId, @RequestBody PlayerRequestModel playerRequestModel) {
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayerInTeam(teamId, playerRequestModel));
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerResponseModel> updatePlayerInTeam(@PathVariable String teamId, @PathVariable String playerId, @RequestBody PlayerRequestModel playerRequestModel) {
        if (teamId.length() != UUID_LENGTH || playerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId or playerId provided.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(playerService.updatePlayerInTeam(teamId, playerId, playerRequestModel));
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deletePlayerFromTeam(@PathVariable String teamId, @PathVariable String playerId) {
        if (teamId.length() != UUID_LENGTH || playerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId or playerId provided.");
        }
        playerService.deletePlayerFromTeam(teamId, playerId);
        return ResponseEntity.noContent().build();
    }
}
