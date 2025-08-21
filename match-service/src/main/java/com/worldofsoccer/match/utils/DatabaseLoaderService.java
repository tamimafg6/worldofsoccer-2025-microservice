package com.worldofsoccer.match.utils;  import com.worldofsoccer.match.dataAccessLayer.*; import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.location.VenueState;
import com.worldofsoccer.match.domainclientLayer.teams.TeamModel; import com.worldofsoccer.match.domainclientLayer.location.VenueModel; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.CommandLineRunner; import org.springframework.stereotype.Component;  import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalTime; import java.util.UUID;  @Component public class DatabaseLoaderService implements CommandLineRunner {
    @Autowired
    MatchRepository matchRepository;

    @Override
    public void run(String... args) throws Exception {
        var matchIdentifier1 = new MatchIdentifier();

        var teamModel1 = TeamModel.builder()
                .teamId("3fa85f64-5717-4562-b3fc-2c963f66afa6")
                .teamName("Manchester United")
                .coach("Erik ten Hag")
                .teamFoundingYear(1878)
                .teamBudget(new BigDecimal("550000000.00"))
                .build();

        var venueModel1 = VenueModel.builder()
                .venueId("12345678-1234-1234-1234-123456789012")
                .venueName("Old Trafford")
                .venueCity("Manchester")
                .venueCapacity(76000)
                .venueState(String.valueOf(VenueState.UPCOMING))
                .build();

        var leagueModel1 = LeagueModel.builder()
                .leagueId("11111111-1111-1111-1111-111111111111")
                .leagueName("Premier League")
                .leagueFormat("LEAGUE")
                .build();

        var matchResults1 = new MatchResults(ResultsType.DRAW, 0);

        var match1 = Match.builder()
                .matchIdentifier(new MatchIdentifier())
                .teamModel(teamModel1)
                .venueModel(venueModel1)
                .leagueModel(leagueModel1)
                .matchScore("0-0")
                .matchStatus(MatchStatus.SCHEDULED)
                .matchTime(LocalTime.of(20, 0))
                .matchDate(LocalDate.of(2024, 5, 15))
                .matchDuration(LocalTime.of(1, 30))
                .matchResults(matchResults1)
                .build();

        matchRepository.save(match1);


    }
}