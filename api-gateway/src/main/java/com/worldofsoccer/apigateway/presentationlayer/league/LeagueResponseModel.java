package com.worldofsoccer.apigateway.presentationlayer.league;


import com.worldofsoccer.apigateway.domainclientlayer.league.FormatTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeagueResponseModel extends RepresentationModel<LeagueResponseModel> {
    private String leagueId;
    private String name;
    private String country;
    private FormatTypeEnum format;
    private Integer numberOfTeams;
    private String leagueDifficulty;
    private Integer seasonYear;
    private LocalDate seasonStartDate;
    private LocalDate seasonEndDate;
    private String competitionFormatType;
    private Boolean competitionFormatGroupStage;
    private Boolean competitionFormatKnockout;
}
