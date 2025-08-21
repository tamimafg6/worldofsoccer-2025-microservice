package com.worldofsoccer.league.presentationlayer;

import com.worldofsoccer.league.dataaccesslayer.FormatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeagueRequestModel {
    private String name;
    private String country;
    private FormatType format;
    private Integer numberOfTeams;
    private String leagueDifficulty;
    private Integer seasonYear;
    private LocalDate seasonStartDate;
    private LocalDate seasonEndDate;
    private String competitionFormatType;
    private Boolean competitionFormatGroupStage;
    private Boolean competitionFormatKnockout;
}
