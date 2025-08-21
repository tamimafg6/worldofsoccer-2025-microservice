package com.worldofsoccer.location.businesslayer;

import com.worldofsoccer.location.dataaccesslayer.Venue;
import com.worldofsoccer.location.dataaccesslayer.VenueIdentifier;
import com.worldofsoccer.location.dataaccesslayer.VenueRepository;
import com.worldofsoccer.location.dataaccesslayer.VenueState;
import com.worldofsoccer.location.mappinglayer.VenueRequestMapper;
import com.worldofsoccer.location.mappinglayer.VenueResponseMapper;
import com.worldofsoccer.location.presentationlayer.VenueController;
import com.worldofsoccer.location.presentationlayer.VenueRequestModel;
import com.worldofsoccer.location.presentationlayer.VenueResponseModel;
import com.worldofsoccer.location.dataaccesslayer.MatchStatus;
import com.worldofsoccer.location.utils.exceptions.InvalidInputException;
import com.worldofsoccer.location.utils.exceptions.InvalidVenueCapacityException;
import com.worldofsoccer.location.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueRequestMapper requestMapper;
    private final VenueResponseMapper responseMapper;

    @Override
    public List<VenueResponseModel> getAllVenues() {
        List<Venue> venues = venueRepository.findAll();
        List<VenueResponseModel> responseList = new ArrayList<>();
        for (Venue venue : venues) {
            VenueResponseModel model = responseMapper.entityToResponseModel(venue);
            addLinks(model, venue);
            responseList.add(model);
        }
        return responseList;
    }

    @Override
    public VenueResponseModel getVenueById(String venueId) {
        Venue venue = venueRepository.findByVenueIdentifier_VenueId(venueId);
        if (venue == null) {
            throw new NotFoundException("Venue not found with ID: " + venueId);
        }
        VenueResponseModel model = responseMapper.entityToResponseModel(venue);
        addLinks(model, venue);
        return model;
    }

    @Override
    public VenueResponseModel createVenue(VenueRequestModel venueRequestModel) {
        if (venueRequestModel.getName() == null) {
            throw new InvalidInputException("Venue name is required.");
        }
        if (venueRequestModel.getCapacity() == null || venueRequestModel.getCapacity() < 100) {
            throw new InvalidVenueCapacityException(
                    "Venue capacity must be at least 100. Provided: " + venueRequestModel.getCapacity());
        }

        Venue venue = requestMapper.requestModelToEntity(venueRequestModel, new VenueIdentifier());
        Venue saved = venueRepository.save(venue);

        VenueResponseModel model = responseMapper.entityToResponseModel(saved);
        addLinks(model, saved);
        return model;
    }

    @Override
    public VenueResponseModel updateVenue(String venueId, VenueRequestModel venueRequestModel) {
        Venue existing = venueRepository.findByVenueIdentifier_VenueId(venueId);
        if (existing == null) {
            throw new NotFoundException("Venue not found with ID: " + venueId);
        }

        if (venueRequestModel.getName() == null) {
            throw new InvalidInputException("Venue name is required.");
        }

        if (venueRequestModel.getCapacity() != null && venueRequestModel.getCapacity() < 100) {
            throw new InvalidVenueCapacityException(
                    "Venue capacity must be at least 100. Provided: " + venueRequestModel.getCapacity());
        }

        requestMapper.updateEntity(venueRequestModel, existing);
        Venue updated = venueRepository.save(existing);

        VenueResponseModel model = responseMapper.entityToResponseModel(updated);
        addLinks(model, updated);
        return model;
    }

    @Override
    public void deleteVenue(String venueId) {
        Venue existing = venueRepository.findByVenueIdentifier_VenueId(venueId);
        if (existing == null) {
            throw new NotFoundException("Venue not found with ID: " + venueId);
        }
        venueRepository.delete(existing);
    }

    @Override
    public Boolean updateVenueStateBasedOnMatchStatus(String venueId, MatchStatus matchStatus) {
        Venue venue = venueRepository.findByVenueIdentifier_VenueId(venueId);
        if (venue == null) {
            throw new NotFoundException("Venue not found with ID: " + venueId);
        }
        VenueState newState;
        if (matchStatus == MatchStatus.SCHEDULED) {
            newState = VenueState.UPCOMING;
        } else if (matchStatus == MatchStatus.IN_PROGRESS) {
            newState = VenueState.LIVE;
        } else if (matchStatus == MatchStatus.COMPLETED) {
            newState = VenueState.PAST;
        }

        else if(matchStatus == MatchStatus.CANCELED){
            newState = VenueState.CANCELED;
        }
        else {
            newState = venue.getVenueState();
        }
        venue.setVenueState(newState);
        venueRepository.save(venue);
        return true;
    }
    private void addLinks(VenueResponseModel model, Venue venue) {
        Link self = linkTo(methodOn(VenueController.class)
                .getVenueById(model.getVenueId()))
                .withSelfRel();
        model.add(self);

        Link all = linkTo(methodOn(VenueController.class)
                .getAllVenues())
                .withRel("allVenues");
        model.add(all);
    }
}

