package com.worldofsoccer.match.domainclientLayer.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class VenueModel {
    private String  venueId;
    private String  venueName;
    private String  venueCity;
    private Integer venueCapacity;
    private String  venueState;
}
