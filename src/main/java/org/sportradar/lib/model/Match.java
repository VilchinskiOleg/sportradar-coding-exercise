package org.sportradar.lib.model;

import java.time.LocalDateTime;

public record Match(String matchId, LocalDateTime startTime, Team homeTeam, Team awayTeam) {

    public int getCommonScore() {
        return homeTeam.getScore() + awayTeam.getScore();
    }
}
