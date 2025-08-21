package com.worldofsoccer.apigateway.businesslayer.league;


import com.worldofsoccer.apigateway.presentationlayer.league.LeagueRequestModel;
import com.worldofsoccer.apigateway.presentationlayer.league.LeagueResponseModel;
import java.util.List;

public interface LeagueService {
    LeagueResponseModel getLeagueById(String leagueId);
    LeagueResponseModel createLeague(LeagueRequestModel leagueRequest);
    LeagueResponseModel updateLeague(String leagueId, LeagueRequestModel leagueRequest);
    void deleteLeague(String leagueId);
    List<LeagueResponseModel> getAllLeagues();
}
