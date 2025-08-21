package com.worldofsoccer.teams.dataaccesslayer.team;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class TeamIdentifier {
    private String teamId;

    public TeamIdentifier() {
        this.teamId = UUID.randomUUID().toString();
    }

    public TeamIdentifier(String teamId) {
        this.teamId = teamId;
    }
}
