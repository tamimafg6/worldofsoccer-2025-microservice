package com.worldofsoccer.teams.presentationlayer.team;

import com.worldofsoccer.teams.businesslayer.team.TeamService;
import com.worldofsoccer.teams.utils.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private static final int UUID_LENGTH = 36;

    @GetMapping()
    public ResponseEntity<List<TeamResponseModel>> getAllTeams() {
        return ResponseEntity.ok().body(teamService.getAllTeams());
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponseModel> getTeamById(@PathVariable String teamId) {
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        return ResponseEntity.ok().body(teamService.getTeamById(UUID.fromString(teamId)));
    }

    @PostMapping()
    public ResponseEntity<TeamResponseModel> createTeam(@RequestBody TeamRequestModel teamRequestModel) {
        TeamResponseModel createdTeam = teamService.createTeam(teamRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamResponseModel> updateTeam(@PathVariable String teamId, @RequestBody TeamRequestModel teamRequestModel) {
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        TeamResponseModel updatedTeam = teamService.updateTeam(UUID.fromString(teamId), teamRequestModel);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTeam);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable String teamId) {
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        teamService.deleteTeam(UUID.fromString(teamId));
        return ResponseEntity.noContent().build();
    }
}
