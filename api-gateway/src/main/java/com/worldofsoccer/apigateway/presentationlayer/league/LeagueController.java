package com.worldofsoccer.apigateway.presentationlayer.league;

import com.worldofsoccer.apigateway.businesslayer.league.LeagueService;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/leagues")
public class LeagueController {

    private final LeagueService leagueService;
    private static final int UUID_LENGTH = 36;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    public ResponseEntity<List<LeagueResponseModel>> getAllLeagues() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<LeagueResponseModel> getLeagueById(@PathVariable String leagueId) {
        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId provided: " + leagueId);
        }
        return ResponseEntity.ok(leagueService.getLeagueById(leagueId));
    }

    @PostMapping
    public ResponseEntity<LeagueResponseModel> createLeague(@RequestBody LeagueRequestModel leagueRequest) {
        LeagueResponseModel created = leagueService.createLeague(leagueRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{leagueId}")
    public ResponseEntity<LeagueResponseModel> updateLeague(
            @PathVariable String leagueId,
            @RequestBody LeagueRequestModel leagueRequest) {

        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId provided: " + leagueId);
        }
        LeagueResponseModel updated = leagueService.updateLeague(leagueId, leagueRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<Void> deleteLeague(@PathVariable String leagueId) {
        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId provided: " + leagueId);
        }
        leagueService.deleteLeague(leagueId);
        return ResponseEntity.noContent().build();
    }
}
