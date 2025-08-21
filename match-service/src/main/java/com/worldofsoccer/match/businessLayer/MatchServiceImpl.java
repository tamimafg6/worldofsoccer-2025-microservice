package com.worldofsoccer.match.businessLayer;

import com.worldofsoccer.match.dataAccessLayer.Match;
import com.worldofsoccer.match.dataAccessLayer.MatchIdentifier;
import com.worldofsoccer.match.dataAccessLayer.MatchRepository;
import com.worldofsoccer.match.dataAccessLayer.MatchStatus;
import com.worldofsoccer.match.domainclientLayer.league.LeagueModel;
import com.worldofsoccer.match.domainclientLayer.league.LeagueServiceClient;
import com.worldofsoccer.match.domainclientLayer.location.VenueModel;
import com.worldofsoccer.match.domainclientLayer.location.VenueState;
import com.worldofsoccer.match.domainclientLayer.teams.TeamModel;
import com.worldofsoccer.match.domainclientLayer.teams.TeamServiceClient;
import com.worldofsoccer.match.domainclientLayer.location.VenueServiceClient;
import com.worldofsoccer.match.mappingLayer.MatchRequestMapper;
import com.worldofsoccer.match.mappingLayer.MatchResponseMapper;
import com.worldofsoccer.match.presentationlayer.MatchRequestModel;
import com.worldofsoccer.match.presentationlayer.MatchResponseModel;
import com.worldofsoccer.match.utils.exceptions.InvalidInputException;
import com.worldofsoccer.match.utils.exceptions.InvalidMatchDurationException;
import com.worldofsoccer.match.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TeamServiceClient teamServiceClient;
    private final LeagueServiceClient leagueServiceClient;
    private final VenueServiceClient venueServiceClient;
    private final MatchRequestMapper matchRequestMapper;
    private final MatchResponseMapper matchResponseMapper;

    public MatchServiceImpl(MatchRepository matchRepository,
                            TeamServiceClient teamServiceClient,
                            LeagueServiceClient leagueServiceClient,
                            VenueServiceClient venueServiceClient,
                            MatchRequestMapper matchRequestMapper,
                            MatchResponseMapper matchResponseMapper) {
        this.matchRepository = matchRepository;
        this.teamServiceClient = teamServiceClient;
        this.leagueServiceClient = leagueServiceClient;
        this.venueServiceClient = venueServiceClient;
        this.matchRequestMapper = matchRequestMapper;
        this.matchResponseMapper = matchResponseMapper;
    }

    @Override
    public List<MatchResponseModel> getAllMatches(String leagueId) {
        List<MatchResponseModel> matches = new ArrayList<>();
        matchRepository.findAll().forEach(match ->
                matches.add(matchResponseMapper.entityToResponseModel(match)));
        return matches;
    }

    @Override
    public MatchResponseModel getMatchByMatchId(String leagueId, String matchId) {
        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            throw new NotFoundException("League not found with ID: " + leagueId);
        }

        Match match = matchRepository.findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(leagueId, matchId);
        if (match == null) {
            throw new NotFoundException("Match not found with ID: " + matchId);
        }

        MatchResponseModel matchResponseModel = matchResponseMapper.entityToResponseModel(match);
        return matchResponseModel;
    }
    @Override
    public MatchResponseModel createMatch(MatchRequestModel request,String leagueId) {

        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            throw new NotFoundException("League not found with ID: " + leagueId);
        }



        TeamModel team = teamServiceClient.getTeamById(request.getTeamId());
        if (team == null) {
            throw new InvalidInputException("Team not found with ID: " + request.getTeamId());
        }

        if (request.getMatchDuration() == null ||
                request.getMatchDuration().isBefore(LocalTime.of(1, 30)) ||
                request.getMatchDuration().isAfter(LocalTime.of(3, 0))) {
            throw new InvalidMatchDurationException("Match duration must be between 1:30 and 3:00 hours");
        }

        VenueModel venue = venueServiceClient.getVenueById(request.getVenueId());
        if (venue == null) {
            throw new InvalidInputException("Venue not found with ID: " + request.getVenueId());
        }

        // Aggregate invariant - check venue availability
        VenueState venueState = VenueState.valueOf(venue.getVenueState());
        if (venueState != VenueState.UPCOMING && venueState != VenueState.PAST) {
            throw new InvalidInputException("Venue " + venue.getVenueId() + " is not available for scheduling");
        }


        Match match = matchRequestMapper.requestModelToEntity(request, new MatchIdentifier(), venue, team, league);

        // Set initial match status


        match.setMatchStatus(MatchStatus.SCHEDULED);
        Match savedMatch = matchRepository.save(match);



        // Update venue state through venue service
        venueServiceClient.patchVenueState(venue.getVenueId(), MatchStatus.SCHEDULED);

        return matchResponseMapper.entityToResponseModel(savedMatch);
    }

    @Override
    public MatchResponseModel updateMatch(String matchId,
                                          MatchRequestModel request,
                                          String leagueId) {
        // Validate league
        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            throw new NotFoundException("League not found with ID: " + leagueId);
        }

        // Fetch existing match
        Match existingMatch = matchRepository.findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(leagueId, matchId);
        if (existingMatch == null) {
            throw new NotFoundException("Match not found with ID: " + matchId);
        }

        // Validate duration
        if (request.getMatchDuration() == null ||
                request.getMatchDuration().isBefore(LocalTime.of(1, 30)) ||
                request.getMatchDuration().isAfter(LocalTime.of(3, 0))) {
            throw new InvalidMatchDurationException(
                    "Match duration must be between 1:30 and 3:00 hours");
        }

        // Prevent updates to completed matches
        if (existingMatch.getMatchStatus() == MatchStatus.COMPLETED) {
            throw new InvalidInputException("Cannot update completed match");
        }

        // Aggregate invariant: if status changed, update venue state
        MatchStatus oldStatus = existingMatch.getMatchStatus();
        MatchStatus newStatus = request.getMatchStatus();
        if (newStatus != null && newStatus != oldStatus) {
            venueServiceClient.patchVenueState(
                    existingMatch.getVenueModel().getVenueId(),
                    newStatus
            );
        }

        // If venue changed, update old and new venue states
        if (!existingMatch.getVenueModel().getVenueId().equals(request.getVenueId())) {
            VenueModel newVenue = venueServiceClient.getVenueById(request.getVenueId());
            if (newVenue == null) {
                throw new InvalidInputException(
                        "Venue not found with ID: " + request.getVenueId());
            }
            VenueState newVenueState = VenueState.valueOf(newVenue.getVenueState());
            if (newVenueState != VenueState.UPCOMING && newVenueState != VenueState.PAST) {
                throw new InvalidInputException(
                        "New venue " + newVenue.getVenueId() + " is not available");
            }

            // Cancel old venue
            venueServiceClient.patchVenueState(
                    existingMatch.getVenueModel().getVenueId(),
                    MatchStatus.CANCELED
            );
            // Set correct state for new venue (use updated status if provided)
            venueServiceClient.patchVenueState(
                    newVenue.getVenueId(),
                    newStatus != null ? newStatus : oldStatus
            );
        }

        // Pull fresh team & venue
        TeamModel team = teamServiceClient.getTeamById(request.getTeamId());
        VenueModel venue = venueServiceClient.getVenueById(request.getVenueId());

        // Map request to entity
        Match updatedMatch = matchRequestMapper.requestModelToEntity(
                request,
                existingMatch.getMatchIdentifier(),
                venue,
                team,
                league
        );
        updatedMatch.setId(existingMatch.getId());

        // Save and return
        Match saved = matchRepository.save(updatedMatch);
        return matchResponseMapper.entityToResponseModel(saved);
    }



    @Override
    public void deleteMatch(String matchId,String leagueId) {

        LeagueModel league = leagueServiceClient.getLeagueById(leagueId);
        if (league == null) {
            throw new NotFoundException("League not found with ID: " + leagueId);
        }
        Match match = matchRepository.findByLeagueModel_LeagueIdAndMatchIdentifier_MatchId(leagueId,matchId);
        if (match == null) {
            throw new NotFoundException("Match not found with ID: " + matchId);
        }

        // Aggregate invariant - prevent deletion of in-progress matches
        if (match.getMatchStatus() == MatchStatus.IN_PROGRESS) {
            throw new InvalidInputException("Cannot delete match that is in progress");
        }

        // Update venue state before deleting match
        venueServiceClient.patchVenueState(match.getVenueModel().getVenueId(), MatchStatus.CANCELED);

        matchRepository.delete(match);
        log.debug("Deleted match with ID: {}", matchId);
    }


}
