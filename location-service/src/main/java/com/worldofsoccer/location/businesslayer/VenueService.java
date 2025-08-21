package com.worldofsoccer.location.businesslayer;


import com.worldofsoccer.location.dataaccesslayer.MatchStatus;
import com.worldofsoccer.location.presentationlayer.VenueRequestModel;
import com.worldofsoccer.location.presentationlayer.VenueResponseModel;

import java.util.List;

public interface VenueService {
    List<VenueResponseModel> getAllVenues();
    VenueResponseModel getVenueById(String venueId);
    VenueResponseModel createVenue(VenueRequestModel venueRequestModel);
    VenueResponseModel updateVenue(String venueId, VenueRequestModel venueRequestModel);
    void deleteVenue(String venueId);
    Boolean updateVenueStateBasedOnMatchStatus(String venueId, MatchStatus matchStatus);

}
