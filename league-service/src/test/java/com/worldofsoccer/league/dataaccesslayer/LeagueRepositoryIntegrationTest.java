package com.worldofsoccer.league.dataaccesslayer;

import com.worldofsoccer.league.utils.exceptions.InvalidNumberOfTeamsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LeagueRepositoryIntegrationTest {

    @Autowired
    private LeagueRepository leagueRepository;

    @BeforeEach
    public void setupDb(){
        leagueRepository.deleteAll();
    }

    @Test
    public void whenLeaguesExist_thenReturnAllLeagues(){
        League league1 = new League("Serie A", "Italy", FormatType.LEAGUE, 20, "Medium");
        league1.setSeasonYear(2021);
        league1.setSeasonStartDate(LocalDate.of(2021, 8, 1));
        league1.setSeasonEndDate(LocalDate.of(2022, 5, 20));
        league1.setCompetitionFormatType("HomeAway");
        league1.setCompetitionFormatGroupStage(false);
        league1.setCompetitionFormatKnockout(false);

        League league2 = new League("La Liga", "Spain", FormatType.LEAGUE, 20, "High");
        league2.setSeasonYear(2021);
        league2.setSeasonStartDate(LocalDate.of(2021, 8, 15));
        league2.setSeasonEndDate(LocalDate.of(2022, 5, 30));
        league2.setCompetitionFormatType("HomeAway");
        league2.setCompetitionFormatGroupStage(false);
        league2.setCompetitionFormatKnockout(false);

        leagueRepository.save(league1);
        leagueRepository.save(league2);
        long afterSizeDB = leagueRepository.count();

        List<League> leagueList = leagueRepository.findAll();

        assertNotNull(leagueList);
        assertNotEquals(0, afterSizeDB);
        assertEquals(afterSizeDB, leagueList.size());
    }

    @Test
    public void whenLeagueExists_thenReturnLeagueById(){
        League league = new League("Bundesliga", "Germany", FormatType.LEAGUE, 18, "High");
        league.setSeasonYear(2021);
        league.setSeasonStartDate(LocalDate.of(2021, 9, 1));
        league.setSeasonEndDate(LocalDate.of(2022, 6, 30));
        league.setCompetitionFormatType("HomeAway");
        league.setCompetitionFormatGroupStage(false);
        league.setCompetitionFormatKnockout(false);
        leagueRepository.save(league);

        League foundLeague = leagueRepository.findByLeagueIdentifier_LeagueId(
                league.getLeagueIdentifier().getLeagueId()
        );

        assertNotNull(foundLeague);
        assertEquals(league.getLeagueIdentifier().getLeagueId(),
                foundLeague.getLeagueIdentifier().getLeagueId());
        assertEquals("Bundesliga", foundLeague.getName());
    }

    @Test
    public void whenLeagueDoesNotExist_thenReturnNull(){
        League foundLeague = leagueRepository.findByLeagueIdentifier_LeagueId("NON_EXISTENT");
        assertNull(foundLeague);
    }

    @Test
    public void whenMultipleLeaguesSaved_thenCountMatches(){
        League league1 = new League("Serie A", "Italy", FormatType.LEAGUE, 20, "Medium");
        league1.setSeasonYear(2021);
        league1.setSeasonStartDate(LocalDate.of(2021, 8, 1));
        league1.setSeasonEndDate(LocalDate.of(2022, 5, 20));
        league1.setCompetitionFormatType("HomeAway");
        league1.setCompetitionFormatGroupStage(false);
        league1.setCompetitionFormatKnockout(false);

        League league2 = new League("Ligue 1", "France", FormatType.LEAGUE, 20, "Medium");
        league2.setSeasonYear(2021);
        league2.setSeasonStartDate(LocalDate.of(2021, 8, 1));
        league2.setSeasonEndDate(LocalDate.of(2022, 5, 20));
        league2.setCompetitionFormatType("HomeAway");
        league2.setCompetitionFormatGroupStage(false);
        league2.setCompetitionFormatKnockout(false);

        leagueRepository.save(league1);
        leagueRepository.save(league2);

        List<League> allLeagues = leagueRepository.findAll();

        assertNotNull(allLeagues);
        assertEquals(2, allLeagues.size());
    }


    @Test
    public void testLeagueIdentifierParameterizedConstructor(){
        String testId = "TEST-UUID-1234";
        LeagueIdentifier identifier = new LeagueIdentifier(testId);
        assertEquals(testId, identifier.getLeagueId());
    }

    @Test
    public void testInvalidNumberOfTeamsExceptionConstructors(){
        InvalidNumberOfTeamsException e1 = new InvalidNumberOfTeamsException();
        assertNull(e1.getMessage(), "Default constructor should have null message");

        String message = "Invalid team count";
        InvalidNumberOfTeamsException e2 = new InvalidNumberOfTeamsException(message);
        assertEquals(message, e2.getMessage());

        Throwable cause = new RuntimeException("Underlying cause");
        InvalidNumberOfTeamsException e3 = new InvalidNumberOfTeamsException(message, cause);
        assertEquals(message, e3.getMessage());
        assertEquals(cause, e3.getCause());

        InvalidNumberOfTeamsException e4 = new InvalidNumberOfTeamsException(cause);
        assertEquals(cause, e4.getCause());
    }
}
