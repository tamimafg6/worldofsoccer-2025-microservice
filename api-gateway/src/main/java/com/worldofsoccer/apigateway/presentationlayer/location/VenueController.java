package com.worldofsoccer.apigateway.presentationlayer.location;

import com.worldofsoccer.apigateway.businesslayer.location.VenueService;
import com.worldofsoccer.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/venues")
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
    public ResponseEntity<VenueResponseModel> createVenue(@RequestBody VenueRequestModel venueRequest) {
        VenueResponseModel createdVenue = venueService.createVenue(venueRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    @PutMapping("/{venueId}")
    public ResponseEntity<VenueResponseModel> updateVenue(
            @PathVariable String venueId,
            @RequestBody VenueRequestModel venueRequest) {
        if (venueId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid venueId provided: " + venueId);
        }
        VenueResponseModel updatedVenue = venueService.updateVenue(venueId, venueRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedVenue);
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