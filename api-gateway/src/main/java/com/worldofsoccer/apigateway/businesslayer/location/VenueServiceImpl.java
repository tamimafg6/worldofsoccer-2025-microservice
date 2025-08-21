package com.worldofsoccer.apigateway.businesslayer.location;

import com.worldofsoccer.apigateway.domainclientlayer.location.VenueServiceClient;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueController;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.location.VenueResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Slf4j
public class VenueServiceImpl implements VenueService {

    private final VenueServiceClient venueServiceClient;

    public VenueServiceImpl(VenueServiceClient venueServiceClient) {
        this.venueServiceClient = venueServiceClient;
    }

    @Override
    public VenueResponseModel getVenueById(String venueId) {
        log.debug("Business Layer: Fetching venue with id: {}", venueId);
        VenueResponseModel venue = venueServiceClient.getVenueById(venueId);
        return addHateoasLinks(venue);
    }

    @Override
    public VenueResponseModel createVenue(VenueRequestModel venueRequest) {
        log.debug("Business Layer: Creating new venue");
        VenueResponseModel createdVenue = venueServiceClient.createVenue(venueRequest);
        return addHateoasLinks(createdVenue);
    }

    @Override
    public VenueResponseModel updateVenue(String venueId, VenueRequestModel venueRequest) {
        log.debug("Business Layer: Updating venue with id: {}", venueId);
        VenueResponseModel updatedVenue = venueServiceClient.updateVenue(venueId, venueRequest);
        return addHateoasLinks(updatedVenue);
    }

    @Override
    public void deleteVenue(String venueId) {
        log.debug("Business Layer: Deleting venue with id: {}", venueId);
        venueServiceClient.deleteVenue(venueId);
    }

    @Override
    public List<VenueResponseModel> getAllVenues() {
        log.debug("Business Layer: Fetching all venues");
        List<VenueResponseModel> venues = venueServiceClient.getAllVenues();
        for (VenueResponseModel venue : venues) {
            addHateoasLinks(venue);
        }
        return venues;
    }

    private VenueResponseModel addHateoasLinks(VenueResponseModel venue) {
        Link selfLink = linkTo(methodOn(VenueController.class)
                .getVenueById(venue.getVenueId()))
                .withSelfRel();
        venue.add(selfLink);

        Link allVenuesLink = linkTo(methodOn(VenueController.class)
                .getAllVenues())
                .withRel("allVenues");
        venue.add(allVenuesLink);

        return venue;
    }
}