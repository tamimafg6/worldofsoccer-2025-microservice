package com.worldofsoccer.location.presentationlayer;

import com.worldofsoccer.location.businesslayer.VenueService;
import com.worldofsoccer.location.dataaccesslayer.MatchStatus;
import com.worldofsoccer.location.utils.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/v1/venues")
public class VenueController {

    private final VenueService venueService;
    private static final int UUID_LENGTH = 36;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public ResponseEntity<List<VenueResponseModel>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<VenueResponseModel> getVenueById(@PathVariable String venueId) {
        if (venueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid venueId provided: " + venueId);
        }
        return ResponseEntity.ok(venueService.getVenueById(venueId));
    }

    @PostMapping
    public ResponseEntity<VenueResponseModel> createVenue(@RequestBody VenueRequestModel venueRequestModel) {
        VenueResponseModel createdVenue = venueService.createVenue(venueRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }
    @PutMapping("/{venueId}")
    public ResponseEntity<VenueResponseModel> updateVenue(
            @PathVariable String venueId,
            @RequestBody VenueRequestModel venueRequestModel
    ) {
        if (venueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid venueId provided: " + venueId);
        }
        VenueResponseModel updated = venueService.updateVenue(venueId, venueRequestModel);
        return ResponseEntity.ok(updated);  // <-- 200 OK, not CREATED
    }


    @PatchMapping("/{venueId}/state")
    public ResponseEntity<VenueResponseModel> updateVenueState(
            @PathVariable String venueId,
            @RequestBody String matchStatus
    ) {
        if (venueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid venueId provided: " + venueId);
        }

        Boolean updated = venueService.updateVenueStateBasedOnMatchStatus(
                venueId,
                MatchStatus.valueOf(matchStatus.toUpperCase())
        );

        return updated
                ? ResponseEntity.ok(venueService.getVenueById(venueId))
                : ResponseEntity.badRequest().build();
    }



    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteVenue(@PathVariable String venueId) {
        if (venueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid venueId provided: " + venueId);
        }
        venueService.deleteVenue(venueId);
        return ResponseEntity.noContent().build();
    }
}