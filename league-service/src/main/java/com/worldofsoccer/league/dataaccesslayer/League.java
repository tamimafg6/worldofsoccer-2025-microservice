package com.worldofsoccer.league.dataaccesslayer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "leagues")
@Data
@NoArgsConstructor
public class League {

    @EmbeddedId
    private LeagueIdentifier leagueIdentifier;

    private String name;
    private String country;

    @Enumerated(EnumType.STRING)
    private FormatType format;

    private Integer numberOfTeams;
    private String leagueDifficulty;

    private Integer seasonYear;
    private LocalDate seasonStartDate;
    private LocalDate seasonEndDate;

    private String competitionFormatType;
    private Boolean competitionFormatGroupStage;
    private Boolean competitionFormatKnockout;

    public League(@NotNull String name,@NotNull String country,@NotNull FormatType format,
                  @NotNull Integer numberOfTeams,@NotNull String leagueDifficulty) {
        this.leagueIdentifier = new LeagueIdentifier();
        this.name = name;
        this.country = country;
        this.format = format;
        this.numberOfTeams = numberOfTeams;
        this.leagueDifficulty = leagueDifficulty;
    }
}
