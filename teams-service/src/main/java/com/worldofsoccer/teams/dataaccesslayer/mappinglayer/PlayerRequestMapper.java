package com.worldofsoccer.teams.dataaccesslayer.mappinglayer;

import com.worldofsoccer.teams.dataaccesslayer.player.Player;
import com.worldofsoccer.teams.dataaccesslayer.player.PlayerIdentifier;
import com.worldofsoccer.teams.presentationlayer.player.PlayerRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PlayerRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "playerIdentifier", source = "playerIdentifier")
    })
    Player requestModelToEntity(PlayerRequestModel requestModel, PlayerIdentifier playerIdentifier);

    @Mappings({
            @Mapping(target = "playerIdentifier", ignore = true)
    })
    void updateEntity(PlayerRequestModel requestModel, @MappingTarget Player existingPlayer);
}
