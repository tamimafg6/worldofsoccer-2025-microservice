package com.worldofsoccer.apigateway.presentationlayer.teams;

import com.worldofsoccer.apigateway.domainclientlayer.location.VenueStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequestModel {
    private String teamName;
    private String coach;
    private Integer foundingYear;
    private Double budget;
    private String teamStatus;


}
