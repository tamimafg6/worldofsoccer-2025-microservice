package com.worldofsoccer.match.dataAccessLayer;

import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.location.VenueModel;
import com.worldofsoccer.match.domainclientLayer.teams.TeamModel;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "matches")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    @Id
    private String id;



    private MatchIdentifier matchIdentifier;

    private LeagueModel leagueModel;
    private TeamModel teamModel;
    private VenueModel venueModel;


    private String matchScore;

    private MatchStatus matchStatus;

    private LocalTime matchTime;
    private LocalDate matchDate;
    private LocalTime matchDuration;

    private MatchResults matchResults;


}
