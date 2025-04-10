package org.sportradar.lib.service;

import lombok.RequiredArgsConstructor;
import org.sportradar.lib.model.Match;
import org.sportradar.lib.service.dao.MatchScoreboardDao;

import java.util.Collection;

@RequiredArgsConstructor
public class WorldCupScoreboardService {

    private final MatchScoreboardDao matchScoreboardDao;

    public Collection<Match> initiateNewMatch(String homeTeamName, String awayTeamName) {
        return null;
    }

    public Collection<Match> getAllMatches() {
        return null;
    }

    public Collection<Match> updateScore(String matchId, int homeTeamScore, int awayTeamScore) {
        return null;
    }

    public Collection<Match> deleteMatch(String matchId) {
        return null;
    }
}
