package com.worldofsoccer.teams.dataaccesslayer.team;

import com.worldofsoccer.teams.dataaccesslayer.player.Player;
import com.worldofsoccer.teams.dataaccesslayer.player.PlayerRepository;
import com.worldofsoccer.teams.dataaccesslayer.player.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TeamRepositoryIntegrationTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private Team testTeam;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    public void whenTeamsExist_thenReturnAllTeams() {
        Team team1 = new Team("Team A", "Coach A", 1990, BigDecimal.valueOf(1000000));
        team1.setTeamStatus(TeamStatus.IS_PLAYING);
        Team team2 = new Team("Team B", "Coach B", 2000, BigDecimal.valueOf(2000000));
        team2.setTeamStatus(TeamStatus.IS_PLAYING);

        teamRepository.save(team1);
        teamRepository.save(team2);
        long count = teamRepository.count();

        List<Team> teams = teamRepository.findAll();

        assertNotNull(teams);
        assertThat(count, is(greaterThan(0L)));
        assertEquals(count, teams.size());
    }

    @Test
    public void whenTeamExists_thenReturnTeamByTeamId() {
        Team team = new Team("Team C", "Coach C", 1995, BigDecimal.valueOf(1500000));
        team.setTeamStatus(TeamStatus.IS_PLAYING);
        teamRepository.save(team);

        Team foundTeam = teamRepository.findByTeamIdentifier_TeamId(
                team.getTeamIdentifier().getTeamId()
        );

        assertNotNull(foundTeam);
        assertEquals(team.getTeamIdentifier().getTeamId(), foundTeam.getTeamIdentifier().getTeamId());
    }

    @Test
    public void whenTeamDoesNotExist_thenReturnNull() {
        final String NOT_FOUND_TEAM_ID = "00000000-0000-0000-0000-000000000000";
        Team foundTeam = teamRepository.findByTeamIdentifier_TeamId(NOT_FOUND_TEAM_ID);
        assertNull(foundTeam);
    }

    @Test
    public void whenTeamEntityIsValid_thenAddTeam() {
        Team team = new Team("Team D", "Coach D", 2010, BigDecimal.valueOf(3000000));
        team.setTeamStatus(TeamStatus.IS_PLAYING);
        Team savedTeam = teamRepository.save(team);
        assertNotNull(savedTeam);
        assertNotNull(savedTeam.getId());
        assertNotNull(savedTeam.getTeamIdentifier());
        assertNotNull(savedTeam.getTeamIdentifier().getTeamId());
        assertEquals("Team D", savedTeam.getTeamName());
    }


    @Test
    public void whenPlayersExist_thenReturnPlayersByTeamId() {
        testTeam = new Team("Team X", "Coach X", 2005, BigDecimal.valueOf(800000));
        testTeam.setTeamStatus(TeamStatus.IS_PLAYING);
        teamRepository.save(testTeam);

        Player player1 = new Player("Cristiano", "Ronaldo", 36, "Portugal", 7, Position.FORWARD, testTeam.getTeamIdentifier());
        Player player2 = new Player("Neymar", "Junior", 29, "Brazil", 10, Position.FORWARD, testTeam.getTeamIdentifier());
        playerRepository.save(player1);
        playerRepository.save(player2);

        List<Player> players = playerRepository.findAllByTeamIdentifier_TeamId(
                testTeam.getTeamIdentifier().getTeamId()
        );

        assertNotNull(players);
        assertEquals(2, players.size());
    }

    @Test
    public void whenPlayerDoesNotExist_thenReturnNull() {
        Player foundPlayer = playerRepository.findByPlayerIdentifier_PlayerIdAndTeamIdentifier_TeamId(
                "non-existent-player-id", "non-existent-team-id"
        );
        assertNull(foundPlayer);
    }

    @Test
    public void whenPlayerDeleted_thenNotFoundInRepository() {
        testTeam = new Team("Team Y", "Coach Y", 2012, BigDecimal.valueOf(1000000));
        testTeam.setTeamStatus(TeamStatus.IS_PLAYING);
        teamRepository.save(testTeam);

        Player player = new Player("Kylian", "Mbappe", 22, "France", 7, Position.FORWARD, testTeam.getTeamIdentifier());
        Player savedPlayer = playerRepository.save(player);
        String playerId = savedPlayer.getPlayerIdentifier().getPlayerId();

        playerRepository.delete(savedPlayer);
        Player deletedPlayer = playerRepository.findByPlayerIdentifier_PlayerIdAndTeamIdentifier_TeamId(
                playerId, testTeam.getTeamIdentifier().getTeamId()
        );

        assertNull(deletedPlayer);
    }

    @Test
    public void whenDeletingNullTeam_thenThrowException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            teamRepository.delete(null);
        });
    }

    @Test
    public void whenTeamHasPlayers_thenRetrievePlayersThroughTeamIdentifier() {
        Team team = new Team("Team Relationship", "Coach Rel", 2018, BigDecimal.valueOf(1200000));
        team.setTeamStatus(TeamStatus.IS_PLAYING);
        Team savedTeam = teamRepository.save(team);
        String teamId = savedTeam.getTeamIdentifier().getTeamId();

        Player player1 = new Player("PlayerOne", "One", 25, "CountryA", 10, Position.MIDFIELDER, savedTeam.getTeamIdentifier());
        Player player2 = new Player("PlayerTwo", "Two", 27, "CountryB", 11, Position.DEFENDER, savedTeam.getTeamIdentifier());
        playerRepository.save(player1);
        playerRepository.save(player2);

        List<Player> players = playerRepository.findAllByTeamIdentifier_TeamId(teamId);

        assertNotNull(players);
        assertEquals(2, players.size());
    }
}
