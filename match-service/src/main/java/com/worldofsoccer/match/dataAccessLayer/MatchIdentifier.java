package com.worldofsoccer.match.dataAccessLayer;

import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Getter
public class MatchIdentifier {
    private String matchId;

    public MatchIdentifier() {
        this.matchId = UUID.randomUUID().toString();
    }


}
