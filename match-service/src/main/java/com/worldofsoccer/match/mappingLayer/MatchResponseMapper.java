package com.worldofsoccer.match.mappingLayer;

import com.worldofsoccer.match.dataAccessLayer.Match;
import com.worldofsoccer.match.presentationlayer.MatchResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchResponseMapper {

    @Mapping(expression = "java(match.getMatchIdentifier().getMatchId())", target = "matchId")
    @Mapping(expression = "java(match.getMatchScore())", target = "matchScore")
    @Mapping(expression = "java(match.getMatchStatus())", target = "matchStatus")
    @Mapping(expression = "java(match.getMatchTime())", target = "matchTime")
    @Mapping(expression = "java(match.getMatchDate())", target = "matchDate")
    @Mapping(expression = "java(match.getMatchDuration())", target = "matchDuration")
    @Mapping(expression = "java(match.getMatchResults() != null ? match.getMatchResults().getResultsType() : null)",
            target = "resultsType")
    @Mapping(expression = "java(match.getMatchResults() != null ? match.getMatchResults().getMatchMinute() : null)",
            target = "matchMinute")

    // Venue mappings
    @Mapping(expression = "java(match.getVenueModel().getVenueId())", target = "venueId")
    @Mapping(expression = "java(match.getVenueModel().getVenueName())", target = "venueName")
    @Mapping(expression = "java(match.getVenueModel().getVenueCity())", target = "venueCity")
    @Mapping(expression = "java(match.getVenueModel().getVenueCapacity())", target = "venueCapacity")
    @Mapping(expression = "java(match.getVenueModel().getVenueState())", target = "venueState")

    // Team mappings
    @Mapping(expression = "java(match.getTeamModel().getTeamId())", target = "teamId")
    @Mapping(expression = "java(match.getTeamModel().getTeamName())", target = "teamName")
    @Mapping(expression = "java(match.getTeamModel().getCoach())", target = "coach")
    @Mapping(expression = "java(match.getTeamModel().getTeamFoundingYear())", target = "teamFoundingYear")
    @Mapping(expression = "java(match.getTeamModel().getTeamBudget())", target = "teamBudget")

    // League mappings
    @Mapping(expression = "java(match.getLeagueModel().getLeagueId())", target = "leagueId")
    @Mapping(expression = "java(match.getLeagueModel().getLeagueName())", target = "leagueName")
    @Mapping(expression = "java(match.getLeagueModel().getLeagueFormat())", target = "leagueFormat")
    MatchResponseModel entityToResponseModel(Match match);

    List<MatchResponseModel> entityListToResponseModelList(List<Match> matches);
}