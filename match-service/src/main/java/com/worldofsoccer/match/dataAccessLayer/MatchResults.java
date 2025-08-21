package com.worldofsoccer.match.dataAccessLayer;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

public class MatchResults {

    private ResultsType resultsType;

    private  Integer matchMinute;

    public MatchResults ( ResultsType resultsType,  Integer matchMinute ){
        this.resultsType = resultsType;
        this.matchMinute = matchMinute;
    }

}
