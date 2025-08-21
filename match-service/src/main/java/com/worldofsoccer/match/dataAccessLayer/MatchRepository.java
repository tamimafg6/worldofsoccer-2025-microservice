package com.worldofsoccer.match.dataAccessLayer;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MatchRepository extends MongoRepository<Match, String> {

    Match findByMatchIdentifier_MatchId(String matchId);
    Match findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(String leagueId, String matchId);


}
