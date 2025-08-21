package com.worldofsoccer.teams.dataaccesslayer.player;

import com.worldofsoccer.teams.dataaccesslayer.team.TeamIdentifier;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private PlayerIdentifier playerIdentifier;

    private String firstName;
    private String lastName;
    private Integer age;
    private String nationality;
    private Integer jerseyNumber;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Embedded
    private TeamIdentifier teamIdentifier;


    public Player(@NotNull String firstName, @NotNull String lastName, @NotNull Integer age,
                  @NotNull String nationality, @NotNull Integer jerseyNumber, @NotNull Position position,
                  @NotNull TeamIdentifier teamIdentifier) {
        this.playerIdentifier = new PlayerIdentifier();
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.nationality = nationality;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
        this.teamIdentifier = teamIdentifier;
    }
}
