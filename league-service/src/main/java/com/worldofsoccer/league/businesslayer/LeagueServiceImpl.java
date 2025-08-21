package com.worldofsoccer.league.businesslayer;

import com.worldofsoccer.league.dataaccesslayer.FormatType;
import com.worldofsoccer.league.dataaccesslayer.League;
import com.worldofsoccer.league.dataaccesslayer.LeagueIdentifier;
import com.worldofsoccer.league.dataaccesslayer.LeagueRepository;
import com.worldofsoccer.league.mappinglayer.LeagueResponseMapper;
import com.worldofsoccer.league.mappinglayer.LeagueRequestMapper;
import com.worldofsoccer.league.presentationlayer.LeagueController;
import com.worldofsoccer.league.presentationlayer.LeagueRequestModel;
import com.worldofsoccer.league.presentationlayer.LeagueResponseModel;
import com.worldofsoccer.league.utils.exceptions.InvalidInputException;
import com.worldofsoccer.league.utils.exceptions.InvalidNumberOfTeamsException;
import com.worldofsoccer.league.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Service
public class LeagueServiceImpl implements LeagueService {

    private final LeagueRepository leagueRepository;
    private final LeagueResponseMapper leagueResponseMapper;
    private final LeagueRequestMapper leagueRequestMapper;

    public LeagueServiceImpl(LeagueRepository leagueRepository,
                             LeagueResponseMapper leagueResponseMapper,
                             LeagueRequestMapper leagueRequestMapper) {
        this.leagueRepository = leagueRepository;
        this.leagueResponseMapper = leagueResponseMapper;
        this.leagueRequestMapper = leagueRequestMapper;
    }

    @Override
    public List<LeagueResponseModel> getAllLeagues() {
        List<League> leagues = leagueRepository.findAll();
        List<LeagueResponseModel> leagueResponseList = new ArrayList<>();
        for (League league : leagues) {
            LeagueResponseModel response = leagueResponseMapper.entityToResponseModel(league);
            addLinks(response, league);
            leagueResponseList.add(response);
        }
        return leagueResponseList;
    }

    @Override
    public LeagueResponseModel getLeagueById(String leagueId) {
        League foundLeague = leagueRepository.findByLeagueIdentifier_LeagueId(leagueId);
        if (foundLeague == null) {
            throw new NotFoundException("League not found with ID: " + leagueId);
        }
        LeagueResponseModel response = leagueResponseMapper.entityToResponseModel(foundLeague);
        addLinks(response, foundLeague);
        return response;
    }

    @Override
    public LeagueResponseModel createLeague(LeagueRequestModel leagueRequestModel) {
        if (leagueRequestModel.getName() == null) {
            throw new InvalidInputException("League name is required.");
        }

        int numTeams = leagueRequestModel.getNumberOfTeams();
        if (leagueRequestModel.getFormat() == FormatType.LEAGUE) {
            if (numTeams != 18 && numTeams != 20 && numTeams != 27) {
                throw new InvalidNumberOfTeamsException(
                        "For LEAGUE format, number of teams must be 18, 20, or 27. Provided: " + numTeams);
            }
        } else if (leagueRequestModel.getFormat() == FormatType.CUP) {
            if (numTeams != 32) {
                throw new InvalidNumberOfTeamsException(
                        "For CUP format, number of teams must be 32. Provided: " + numTeams);
            }
        }

        League league = leagueRequestMapper.requestModelToEntity(leagueRequestModel, new LeagueIdentifier());
        leagueRepository.save(league);

        LeagueResponseModel response = leagueResponseMapper.entityToResponseModel(league);
        addLinks(response, league);
        return response;
    }

    @Override
    public LeagueResponseModel updateLeague(String leagueId, LeagueRequestModel leagueRequestModel) {
        League existingLeague = leagueRepository.findByLeagueIdentifier_LeagueId(leagueId);
        if (existingLeague == null) {
            throw new NotFoundException("League not found with ID: " + leagueId);
        }

        if (leagueRequestModel.getName() == null) {
            throw new InvalidInputException("League name is required.");
        }

        int numTeams = leagueRequestModel.getNumberOfTeams();
        if (leagueRequestModel.getFormat() == FormatType.LEAGUE) {
            if (numTeams != 18 && numTeams != 20 && numTeams != 27) {
                throw new InvalidNumberOfTeamsException(
                        "For LEAGUE format, number of teams must be 18, 20, or 27. Provided: " + numTeams);
            }
        } else if (leagueRequestModel.getFormat() == FormatType.CUP) {
            if (numTeams != 32) {
                throw new InvalidNumberOfTeamsException(
                        "For CUP format, number of teams must be 32. Provided: " + numTeams);
            }
        }

        leagueRequestMapper.updateEntity(leagueRequestModel, existingLeague);
        leagueRepository.save(existingLeague);

        LeagueResponseModel response = leagueResponseMapper.entityToResponseModel(existingLeague);
        addLinks(response, existingLeague);
        return response;
    }

    @Override
    public void deleteLeague(String leagueId) {
        League foundLeague = leagueRepository.findByLeagueIdentifier_LeagueId(leagueId);
        if (foundLeague == null) {
            throw new NotFoundException("League not found with ID: " + leagueId);
        }
        leagueRepository.delete(foundLeague);
    }

    private void addLinks(LeagueResponseModel responseModel, League league) {
        Link selfLink = linkTo(methodOn(LeagueController.class)
                .getLeagueById(responseModel.getLeagueId())).withSelfRel();
        responseModel.add(selfLink);

        Link allLeaguesLink = linkTo(methodOn(LeagueController.class)
                .getAllLeagues()).withRel("allLeagues");
        responseModel.add(allLeaguesLink);
    }
}