package com.worldofsoccer.teams.presentationlayer.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerResponseModel extends RepresentationModel<PlayerResponseModel> {
    private String playerId;
    private String firstName;
    private String lastName;
    private Integer age;
    private String nationality;
    private Integer jerseyNumber;
    private String position;
    private String teamId;
}
