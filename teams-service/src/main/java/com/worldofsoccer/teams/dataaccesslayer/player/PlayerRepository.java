package com.worldofsoccer.teams.dataaccesslayer.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    List<Player> findAllByTeamIdentifier_TeamId(String teamId);

    Player findByPlayerIdentifier_PlayerIdAndTeamIdentifier_TeamId(String playerId, String teamId);
}
