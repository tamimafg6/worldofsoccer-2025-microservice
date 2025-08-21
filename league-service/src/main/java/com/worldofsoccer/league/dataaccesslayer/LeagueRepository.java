package com.worldofsoccer.league.dataaccesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Integer> {
    League findByLeagueIdentifier_LeagueId(String leagueId);
}
