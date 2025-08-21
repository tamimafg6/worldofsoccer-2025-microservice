package com.worldofsoccer.league.mappinglayer;

import com.worldofsoccer.league.dataaccesslayer.League;
import com.worldofsoccer.league.presentationlayer.LeagueController;
import com.worldofsoccer.league.presentationlayer.LeagueResponseModel;

import org.mapstruct.*;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface LeagueResponseMapper {

    @Mappings({
            @Mapping(target = "leagueId", expression = "java(league.getLeagueIdentifier() != null ? league.getLeagueIdentifier().getLeagueId() : null)"),
            @Mapping(target = "name", source = "league.name"),
            @Mapping(target = "country", source = "league.country"),
            @Mapping(target = "format", source = "league.format"),
            @Mapping(target = "numberOfTeams", source = "league.numberOfTeams"),
            @Mapping(target = "leagueDifficulty", source = "league.leagueDifficulty"),
            @Mapping(target = "seasonYear", source = "league.seasonYear"),
            @Mapping(target = "seasonStartDate", source = "league.seasonStartDate"),
            @Mapping(target = "seasonEndDate", source = "league.seasonEndDate"),
            @Mapping(target = "competitionFormatType", source = "league.competitionFormatType"),
            @Mapping(target = "competitionFormatGroupStage", source = "league.competitionFormatGroupStage"),
            @Mapping(target = "competitionFormatKnockout", source = "league.competitionFormatKnockout")
    })
    LeagueResponseModel entityToResponseModel(League league);


}
