package com.worldofsoccer.apigateway.presentationlayer.teams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
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
