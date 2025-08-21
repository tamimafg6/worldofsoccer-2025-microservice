package com.worldofsoccer.match.presentationlayer;

import com.worldofsoccer.match.businessLayer.MatchService;
import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.league.LeagueServiceClient;
import com.worldofsoccer.match.utils.exceptions.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leagues/{leagueId}/matches")
public class MatchController {

    private final MatchService matchService;
    private final LeagueServiceClient leagueServiceClient;
    private static final int UUID_LENGTH = 36;

    public MatchController(MatchService matchService,
                           LeagueServiceClient leagueServiceClient) {
        this.matchService = matchService;
        this.leagueServiceClient = leagueServiceClient;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponseModel>> getAllLeagueMatches(
            @PathVariable String leagueId) {

        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId provided: " + leagueId);
        }

        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            return ResponseEntity.notFound().build();
        }

        List<MatchResponseModel> matches = matchService.getAllMatches(leagueId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponseModel> getLeagueMatchById(
            @PathVariable String leagueId,
            @PathVariable String matchId) {

        if (leagueId.length() != UUID_LENGTH || matchId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid ID provided");
        }

        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            return ResponseEntity.notFound().build();
        }

        MatchResponseModel match = matchService.getMatchByMatchId(leagueId,matchId);
        return ResponseEntity.ok(match);
    }

    @PostMapping
    public ResponseEntity<MatchResponseModel> createMatch(
            @PathVariable String leagueId,
            @RequestBody MatchRequestModel request) {

        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId provided: " + leagueId);
        }

        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            return ResponseEntity.notFound().build();
        }

        request.setLeagueId(leagueId);
        MatchResponseModel created = matchService.createMatch(request,leagueId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<MatchResponseModel> updateLeagueMatch(
            @RequestBody MatchRequestModel request,
            @PathVariable String leagueId,
            @PathVariable String matchId) {

        if (leagueId.length() != UUID_LENGTH || matchId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid ID provided");
        }

        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            return ResponseEntity.notFound().build();
        }

        request.setLeagueId(leagueId);
        MatchResponseModel updated = matchService.updateMatch(matchId, request,leagueId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteLeagueMatch(
            @PathVariable String leagueId,
            @PathVariable String matchId) {

        if (leagueId.length() != UUID_LENGTH || matchId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid ID provided");
        }

        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            return ResponseEntity.notFound().build();
        }

        matchService.deleteMatch(matchId,leagueId);
        return ResponseEntity.noContent().build();
    }
}