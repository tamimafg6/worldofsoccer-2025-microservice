package com.worldofsoccer.apigateway.presentationlayer.match;

import com.worldofsoccer.apigateway.domainclientlayer.match.MatchStatus;
import com.worldofsoccer.apigateway.domainclientlayer.match.ResultsType;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MatchResponseModel extends RepresentationModel<MatchResponseModel> {
    // core match fields
    private String matchId;
    private String matchScore;
    private MatchStatus matchStatus;
    private LocalTime matchTime;
    private LocalDate matchDate;
    private LocalTime matchDuration;
    private ResultsType resultsType;
    private Integer matchMinute;

    // venue fields
    private String venueId;
    private String venueName;
    private String venueCity;
    private Integer venueCapacity;
    private String venueState;

    // team fields
    private String teamId;
    private String teamName;
    private String coach;
    private Integer teamFoundingYear;
    private BigDecimal teamBudget;

    // league fields
    private String leagueId;
    private String leagueName;
    private String leagueFormat;
}