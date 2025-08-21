package com.worldofsoccer.match.businessLayer;


import com.worldofsoccer.match.presentationlayer.MatchRequestModel;
import com.worldofsoccer.match.presentationlayer.MatchResponseModel;

import java.util.List;

public interface MatchService {
    List<MatchResponseModel> getAllMatches(String leagueId);
    MatchResponseModel getMatchByMatchId( String leagueId, String matchId);
    MatchResponseModel createMatch(MatchRequestModel request, String leagueId);
    MatchResponseModel updateMatch(String matchId, MatchRequestModel request, String leagueId);
    void deleteMatch(String matchId, String leagueId);
}
