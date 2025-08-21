package com.worldofsoccer.league.mappinglayer;


import com.worldofsoccer.league.dataaccesslayer.League;
import com.worldofsoccer.league.dataaccesslayer.LeagueIdentifier;
import com.worldofsoccer.league.presentationlayer.LeagueRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LeagueRequestMapper {

    @Mappings({
            @Mapping(target = "leagueIdentifier", source = "leagueIdentifier"),
            @Mapping(target = "name", source = "requestModel.name"),
            @Mapping(target = "country", source = "requestModel.country"),
            @Mapping(target = "format", source = "requestModel.format"),
            @Mapping(target = "numberOfTeams", source = "requestModel.numberOfTeams"),
            @Mapping(target = "leagueDifficulty", source = "requestModel.leagueDifficulty")
    })
    League requestModelToEntity(LeagueRequestModel requestModel, LeagueIdentifier leagueIdentifier);

    @Mappings({
            @Mapping(target = "leagueIdentifier", ignore = true),
            @Mapping(target = "name", source = "requestModel.name"),
            @Mapping(target = "country", source = "requestModel.country"),
            @Mapping(target = "format", source = "requestModel.format"),
            @Mapping(target = "numberOfTeams", source = "requestModel.numberOfTeams"),
            @Mapping(target = "leagueDifficulty", source = "requestModel.leagueDifficulty")
    })
    void updateEntity(LeagueRequestModel requestModel, @MappingTarget League existingLeague);
}
