package com.worldofsoccer.location.mappinglayer;

import com.worldofsoccer.location.dataaccesslayer.Venue;
import com.worldofsoccer.location.dataaccesslayer.VenueIdentifier;
import com.worldofsoccer.location.presentationlayer.VenueRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VenueRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "venueIdentifier", source = "venueIdentifier"),
            @Mapping(target = "name", source = "requestModel.name"),
            @Mapping(target = "capacity", source = "requestModel.capacity"),
            @Mapping(target = "city", source = "requestModel.city"),
            @Mapping(target = "yearBuilt", source = "requestModel.yearBuilt"),
            @Mapping(target = "venueState", source = "requestModel.venueState")

    })
    Venue requestModelToEntity(VenueRequestModel requestModel, VenueIdentifier venueIdentifier);

    @Mappings({
            @Mapping(target = "venueIdentifier", ignore = true),
            @Mapping(target = "name", source = "requestModel.name"),
            @Mapping(target = "capacity", source = "requestModel.capacity"),
            @Mapping(target = "city", source = "requestModel.city"),
            @Mapping(target = "yearBuilt", source = "requestModel.yearBuilt"),
            @Mapping(target = "venueState", source = "requestModel.venueState")
    })
    void updateEntity(VenueRequestModel requestModel, @MappingTarget Venue existingVenue);
}
