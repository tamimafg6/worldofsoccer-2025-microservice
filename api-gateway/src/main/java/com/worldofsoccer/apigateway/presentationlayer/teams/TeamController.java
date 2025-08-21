package com.worldofsoccer.apigateway.presentationlayer.teams;

import com.worldofsoccer.apigateway.businesslayer.teams.TeamService;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@Slf4j
public class TeamController {

    private final TeamService service;
    private static final int UUID_LENGTH = 36;

    public TeamController(TeamService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponseModel>> getAllTeams() {
        log.debug("Request received in API-Gateway Teams Controller: getAllTeams");
        return ResponseEntity.ok(service.getAllTeams());
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponseModel> getTeamById(
            @PathVariable String teamId) {
        log.debug("Request received in API-Gateway Teams Controller: getTeamById");
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        return ResponseEntity.ok(service.getTeamById(teamId));
    }

    @PostMapping
    public ResponseEntity<TeamResponseModel> createTeam(
            @RequestBody TeamRequestModel req) {
        log.debug("Request received in API-Gateway Teams Controller: createTeam");
        TeamResponseModel created = service.createTeam(req);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamResponseModel> updateTeam(
            @PathVariable String teamId,
            @RequestBody TeamRequestModel req) {
        log.debug("Request received in API-Gateway Teams Controller: updateTeam");
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        return ResponseEntity.ok(service.updateTeam(teamId, req));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable String teamId) {
        log.debug("Request received in API-Gateway Teams Controller: deleteTeam");
        if (teamId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid teamId provided: " + teamId);
        }
        service.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }
}
