package com.worldofsoccer.match.mappingLayer;

import com.worldofsoccer.match.dataAccessLayer.*;
import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.location.VenueModel;
import com.worldofsoccer.match.domainclientLayer.teams.TeamModel;
import com.worldofsoccer.match.presentationlayer.MatchRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MatchRequestMapper {

    @Mappings({
            @Mapping(source = "requestModel.matchScore", target = "matchScore"),
            @Mapping(source = "requestModel.matchStatus", target = "matchStatus"),
            @Mapping(source = "requestModel.matchTime", target = "matchTime"),
            @Mapping(source = "requestModel.matchDate", target = "matchDate"),
            @Mapping(source = "requestModel.matchDuration", target = "matchDuration"),
            @Mapping(source = "requestModel.resultsType", target = "matchResults.resultsType"),
            @Mapping(source = "requestModel.matchMinute", target = "matchResults.matchMinute")
    })
    Match requestModelToEntity(MatchRequestModel requestModel,
                               MatchIdentifier matchIdentifier,
                               VenueModel venueModel,
                               TeamModel teamModel,
                               LeagueModel leagueModel);
}