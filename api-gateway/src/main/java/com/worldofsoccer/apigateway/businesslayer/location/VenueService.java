package com.worldofsoccer.apigateway.businesslayer.location;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueResponseModel;
import java.util.List;

public interface VenueService {
    VenueResponseModel getVenueById(String venueId);
    VenueResponseModel createVenue(VenueRequestModel venueRequest);
    VenueResponseModel updateVenue(String venueId, VenueRequestModel venueRequest);
    void deleteVenue(String venueId);
    List<VenueResponseModel> getAllVenues();
}