package com.worldofsoccer.teams.dataaccesslayer.mappinglayer;

import com.worldofsoccer.teams.dataaccesslayer.player.Player;
import com.worldofsoccer.teams.presentationlayer.player.PlayerController;
import com.worldofsoccer.teams.presentationlayer.player.PlayerResponseModel;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface PlayerResponseMapper {

    @Mappings({
            @Mapping(expression = "java(player.getPlayerIdentifier().getPlayerId())", target = "playerId"),
            @Mapping(expression = "java(player.getFirstName())", target = "firstName"),
            @Mapping(expression = "java(player.getLastName())", target = "lastName"),
            @Mapping(expression = "java(player.getAge())", target = "age"),
            @Mapping(expression = "java(player.getNationality())", target = "nationality"),
            @Mapping(expression = "java(player.getJerseyNumber())", target = "jerseyNumber"),
            @Mapping(expression = "java(player.getPosition().toString())", target = "position"),
            @Mapping(expression = "java(player.getTeamIdentifier().getTeamId())", target = "teamId")
    })
    PlayerResponseModel entityToResponseModel(Player player);


}
