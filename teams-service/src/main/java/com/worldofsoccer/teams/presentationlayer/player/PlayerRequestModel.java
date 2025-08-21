package com.worldofsoccer.teams.presentationlayer.player;

import com.worldofsoccer.teams.dataaccesslayer.player.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRequestModel {
    private String firstName;
    private String lastName;
    private Integer age;
    private String nationality;
    private Integer jerseyNumber;
    private Position position;
}
