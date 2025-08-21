package com.worldofsoccer.apigateway.presentationlayer.match;

import com.worldofsoccer.apigateway.businesslayer.match.MatchService;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leagues/{leagueId}/matches")
@Slf4j
public class MatchController {

    private final MatchService service;
    private static final int UUID_LENGTH = 36;

    public MatchController(MatchService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponseModel>> getAll(
            @PathVariable String leagueId) {
        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId length: " + leagueId);
        }
        return ResponseEntity.ok(service.getAllMatches(leagueId));
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponseModel> getOne(
            @PathVariable String leagueId,
            @PathVariable String matchId) {
        if (leagueId.length() != UUID_LENGTH || matchId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid ID length");
        }
        return ResponseEntity.ok(service.getMatchById(leagueId, matchId));
    }

    @PostMapping
    public ResponseEntity<MatchResponseModel> create(
            @PathVariable String leagueId,
            @RequestBody MatchRequestModel req) {
        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId length: " + leagueId);
        }
        return ResponseEntity.status(201).body(service.createMatch(leagueId, req));
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<MatchResponseModel> update(
            @PathVariable String leagueId,
            @PathVariable String matchId,
            @RequestBody MatchRequestModel req) {
        if (leagueId.length() != UUID_LENGTH || matchId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid ID length");
        }
        return ResponseEntity.ok(service.updateMatch(leagueId, matchId, req));
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> delete(
            @PathVariable String leagueId,
            @PathVariable String matchId) {
        if (leagueId.length() != UUID_LENGTH || matchId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid ID length");
        }
        service.deleteMatch(leagueId, matchId);
        return ResponseEntity.noContent().build();
    }
}