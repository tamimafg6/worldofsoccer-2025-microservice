package com.worldofsoccer.teams.dataaccesslayer.mappinglayer;

import com.worldofsoccer.teams.dataaccesslayer.team.Team;
import com.worldofsoccer.teams.presentationlayer.team.TeamController;
import com.worldofsoccer.teams.presentationlayer.team.TeamResponseModel;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface TeamResponseMapper {

    @Mappings({
            @Mapping(expression = "java(team.getTeamIdentifier().getTeamId())", target = "teamId"),
            @Mapping(source = "team.teamName", target = "name"),
            @Mapping(source = "team.coach", target = "coach"),
            @Mapping(source = "team.foundingYear", target = "foundingYear"),
            @Mapping(expression = "java(team.getBudget().toPlainString())", target = "budget"),
            @Mapping(expression = "java(team.getTeamStatus().name())", target = "teamStatus")
    })
    TeamResponseModel entityToResponseModel(Team team);


}
