package com.worldofsoccer.teams.dataaccesslayer.team;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private TeamIdentifier teamIdentifier;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    private String coach;
    private Integer foundingYear;
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    private TeamStatus teamStatus = TeamStatus.IS_PLAYING;

    public Team(@NotNull String teamName, @NotNull String coach, @NotNull Integer foundingYear, @NotNull BigDecimal budget) {
        this.teamIdentifier = new TeamIdentifier();
        this.teamName = teamName;
        this.coach = coach;
        this.foundingYear = foundingYear;
        this.budget = budget;
        this.teamStatus = TeamStatus.IS_PLAYING;
    }
}
