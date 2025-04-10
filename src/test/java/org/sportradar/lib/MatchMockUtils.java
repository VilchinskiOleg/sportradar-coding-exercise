package org.sportradar.lib;

import org.sportradar.lib.model.Match;
import org.sportradar.lib.model.Team;

import java.time.LocalDateTime;
import java.util.UUID;

public class MatchMockUtils {

    public static Match getMatchMock() {
        var homeTeam = new Team(UUID.randomUUID().toString(), "HomeTeam", 0);
        var awayTeam = new Team(UUID.randomUUID().toString(), "AwayTeam", 0);
        return new Match(UUID.randomUUID().toString(), LocalDateTime.now(), homeTeam, awayTeam);
    }

    public static Match getMatchMock(String homeTeamName, int homeTeamScore, String awayTeamName, int awayTeamScore) {
        var homeTeam = new Team(UUID.randomUUID().toString(), homeTeamName, homeTeamScore);
        var awayTeam = new Team(UUID.randomUUID().toString(), awayTeamName, awayTeamScore);
        return new Match(UUID.randomUUID().toString(), LocalDateTime.now(), homeTeam, awayTeam);
    }
}
