package org.sportradar.lib.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Team {

    private final String teamId;
    private final String name;
    @Setter
    private int score;
}
