package com.worldofsoccer.league.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class LeagueIdentifier {
    private String leagueId;

    public LeagueIdentifier() {
        this.leagueId = UUID.randomUUID().toString();
    }

    public LeagueIdentifier(String leagueId) {
        this.leagueId = leagueId;
    }
}
