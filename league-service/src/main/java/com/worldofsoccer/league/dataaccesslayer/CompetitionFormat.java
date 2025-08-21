package com.worldofsoccer.league.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionFormat {
    private FormatType type;
    private boolean groupStage;
    private boolean knockout;
}
