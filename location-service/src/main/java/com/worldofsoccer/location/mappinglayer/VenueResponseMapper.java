package com.worldofsoccer.location.mappinglayer;

import com.worldofsoccer.location.dataaccesslayer.Venue;
import com.worldofsoccer.location.presentationlayer.VenueResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VenueResponseMapper {

    @Mappings({
            @Mapping(target = "venueId", expression = "java(venue.getVenueIdentifier().getVenueId())"),
            @Mapping(source = "venue.name", target = "name"),
            @Mapping(source = "venue.capacity", target = "capacity"),
            @Mapping(source = "venue.city", target = "city"),
            @Mapping(source = "venue.yearBuilt", target = "yearBuilt"),
            @Mapping(source = "venue.venueState", target = "venueState")
    })
    VenueResponseModel entityToResponseModel(Venue venue);
}