package com.worldofsoccer.match.domainclientLayer.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamModel {
    private String teamId;

    @JsonProperty("name")
    private String teamName;

    private String coach;

    @JsonProperty("foundingYear")
    private Integer teamFoundingYear;

    @JsonProperty("budget")
    private BigDecimal teamBudget;
}
