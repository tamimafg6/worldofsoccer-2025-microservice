package com.worldofsoccer.apigateway.businesslayer.match;

import com.worldofsoccer.apigateway.presentationlayer.match.MatchRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.match.MatchResponseModel;

import java.util.List;

public interface MatchService {
    List<MatchResponseModel> getAllMatches(String leagueId);
    MatchResponseModel getMatchById(String leagueId, String matchId);
    MatchResponseModel createMatch(String leagueId, MatchRequestModel request);
    MatchResponseModel updateMatch(String leagueId, String matchId, MatchRequestModel request);
    void deleteMatch(String leagueId, String matchId);
}
