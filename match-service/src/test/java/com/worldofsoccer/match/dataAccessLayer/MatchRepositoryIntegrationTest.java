package com.worldofsoccer.match.dataAccessLayer;

import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.location.VenueModel;
import com.worldofsoccer.match.domainclientLayer.teams.TeamModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MatchRepositoryIntegrationTest {

    @Autowired
    private MatchRepository matchRepository;

    private Match match;

    @BeforeEach
    void setup() {
        matchRepository.deleteAll();

        TeamModel team = TeamModel.builder()
                .teamId("T-1").teamName("Team").coach("C").teamFoundingYear(2000).teamBudget(BigDecimal.TEN).build();
        VenueModel venue = VenueModel.builder()
                .venueId("V-1").venueName("Venue").venueCity("City").venueCapacity(100).venueState("UPCOMING").build();
        LeagueModel league = LeagueModel.builder()
                .leagueId("L-1").leagueName("League").leagueFormat("F").build();

        match = Match.builder()
                .matchIdentifier(new MatchIdentifier())
                .teamModel(team)
                .venueModel(venue)
                .leagueModel(league)
                .matchScore("0-0")
                .matchStatus(MatchStatus.SCHEDULED)
                .matchTime(LocalTime.of(10, 0))
                .matchDate(LocalDate.now())
                .matchDuration(LocalTime.of(1, 0))
                .matchResults(new MatchResults(null, null))
                .build();

        matchRepository.save(match);
    }

    @Test
    void whenFindByMatchIdentifier_thenReturnMatch() {
        Match found = matchRepository.findByMatchIdentifier_MatchId(match.getMatchIdentifier().getMatchId());
        assertNotNull(found);
        assertEquals(match.getMatchScore(), found.getMatchScore());
    }

    @Test
    void whenFindByLeagueAndMatchId_thenReturnMatch() {
        Match found = matchRepository.findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(
                match.getLeagueModel().getLeagueId(),
                match.getMatchIdentifier().getMatchId()
        );
        assertNotNull(found);
    }
}
