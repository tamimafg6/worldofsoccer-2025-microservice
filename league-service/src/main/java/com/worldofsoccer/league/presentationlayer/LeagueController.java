package com.worldofsoccer.league.presentationlayer;

import com.worldofsoccer.league.businesslayer.LeagueService;
import com.worldofsoccer.league.utils.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;
    private static final int UUID_LENGTH = 36;

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
    public ResponseEntity<LeagueResponseModel> createLeague(@RequestBody LeagueRequestModel leagueRequestModel) {
        LeagueResponseModel createdLeague = leagueService.createLeague(leagueRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLeague);
    }

    @PutMapping("/{leagueId}")
    public ResponseEntity<LeagueResponseModel> updateLeague(
            @PathVariable String leagueId,
            @RequestBody LeagueRequestModel leagueRequestModel
    ) {
        if (leagueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid leagueId provided: " + leagueId);
        }
        LeagueResponseModel updatedLeague = leagueService.updateLeague(leagueId, leagueRequestModel);
        return ResponseEntity.status(HttpStatus.OK).body(updatedLeague);
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
