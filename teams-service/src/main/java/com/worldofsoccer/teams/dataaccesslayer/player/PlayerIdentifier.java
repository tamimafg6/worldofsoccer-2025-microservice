package com.worldofsoccer.teams.dataaccesslayer.player;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class PlayerIdentifier {
    private String playerId;

    public PlayerIdentifier() {
        this.playerId = UUID.randomUUID().toString();
    }
}
