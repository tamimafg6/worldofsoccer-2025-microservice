package com.worldofsoccer.apigateway.presentationlayer.location;

import com.worldofsoccer.apigateway.domainclientlayer.location.VenueStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueRequestModel {
    private String name;
    private Integer capacity;
    private String city;
    private Integer yearBuilt;
    private VenueStateEnum venueState;

}