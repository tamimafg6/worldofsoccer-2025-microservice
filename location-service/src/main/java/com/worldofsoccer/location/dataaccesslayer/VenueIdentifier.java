package com.worldofsoccer.location.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
@AllArgsConstructor
public class VenueIdentifier {
    private String venueId;

    public VenueIdentifier(){
        this.venueId=UUID.randomUUID().toString();
    }
}
