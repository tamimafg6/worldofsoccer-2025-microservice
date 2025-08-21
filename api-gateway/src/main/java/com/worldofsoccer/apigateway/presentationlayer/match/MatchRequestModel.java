package com.worldofsoccer.apigateway.presentationlayer.match;


import com.worldofsoccer.apigateway.domainclientlayer.match.MatchStatus;
import com.worldofsoccer.apigateway.domainclientlayer.match.ResultsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequestModel {
    private String matchScore;
    private MatchStatus matchStatus;
    private LocalTime matchTime;
    private LocalDate matchDate;
    private LocalTime matchDuration;
    private ResultsType resultsType;
    private Integer matchMinute;
    private String venueId;
    private String teamId;
    private String leagueId;
}
