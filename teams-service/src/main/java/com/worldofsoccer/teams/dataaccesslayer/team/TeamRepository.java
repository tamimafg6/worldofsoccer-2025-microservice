package com.worldofsoccer.teams.dataaccesslayer.team;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    Team findByTeamIdentifier_TeamId(String teamId);

    Team findByTeamName(String teamName);
}
