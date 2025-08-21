package com.worldofsoccer.location.presentationlayer;


import com.worldofsoccer.location.dataaccesslayer.VenueState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueResponseModel extends RepresentationModel<VenueResponseModel> {
    private String venueId;
    private String name;
    private Integer capacity;
    private String city;
    private Integer yearBuilt;
    private VenueState venueState;
}
