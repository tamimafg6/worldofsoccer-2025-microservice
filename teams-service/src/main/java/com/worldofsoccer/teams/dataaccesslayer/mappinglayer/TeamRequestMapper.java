package com.worldofsoccer.teams.dataaccesslayer.mappinglayer;

import com.worldofsoccer.teams.dataaccesslayer.team.Team;
import com.worldofsoccer.teams.dataaccesslayer.team.TeamIdentifier;
import com.worldofsoccer.teams.presentationlayer.team.TeamRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TeamRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "teamIdentifier", source = "teamIdentifier")
    })
    Team requestModelToEntity(TeamRequestModel requestModel, TeamIdentifier teamIdentifier);

    @Mappings({
            @Mapping(target = "teamIdentifier", ignore = true)
    })
    void updateEntity(TeamRequestModel requestModel, @MappingTarget Team existingTeam);
}
