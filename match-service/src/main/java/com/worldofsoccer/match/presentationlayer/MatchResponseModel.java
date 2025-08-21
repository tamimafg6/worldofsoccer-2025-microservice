package com.worldofsoccer.match.presentationlayer;


import com.worldofsoccer.match.dataAccessLayer.MatchStatus;
import com.worldofsoccer.match.dataAccessLayer.ResultsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponseModel  {
    private String       matchId;
    private String       matchScore;
    private MatchStatus matchStatus;
    private LocalTime    matchTime;
    private LocalDate    matchDate;
    private LocalTime    matchDuration;
    private ResultsType resultsType;
    private Integer      matchMinute;

    private String venueId;
    private String teamId;
    private String leagueId;

    private String  venueName;
    private String  venueCity;
    private Integer venueCapacity;
    private String  venueState;

    private String   teamName;
    private String   coach;
    private Integer  teamFoundingYear;
    private BigDecimal teamBudget;

    private String leagueName;
    private String leagueFormat;
}
