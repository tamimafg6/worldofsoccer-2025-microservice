package com.worldofsoccer.league.businesslayer;


import com.worldofsoccer.league.presentationlayer.LeagueRequestModel;
import com.worldofsoccer.league.presentationlayer.LeagueResponseModel;

import java.util.List;

public interface LeagueService {
    List<LeagueResponseModel> getAllLeagues();
    LeagueResponseModel getLeagueById(String leagueId);
    LeagueResponseModel createLeague(LeagueRequestModel leagueRequestModel);
    LeagueResponseModel updateLeague(String leagueId, LeagueRequestModel leagueRequestModel);
    void deleteLeague(String leagueId);
}
