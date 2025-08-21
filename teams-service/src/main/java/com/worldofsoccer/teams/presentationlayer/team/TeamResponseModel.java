package com.worldofsoccer.teams.presentationlayer.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponseModel extends RepresentationModel<TeamResponseModel> {
    private String teamId;
    private String name;
    private String coach;
    private Integer foundingYear;
    private String budget;
    private String teamStatus;
}
