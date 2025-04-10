package org.sportradar.lib.service;

import lombok.RequiredArgsConstructor;
import org.sportradar.lib.model.Match;
import org.sportradar.lib.model.Team;
import org.sportradar.lib.service.dao.MatchScoreboardDao;
import org.sportradar.lib.service.subscribtion.ScoreboardStateUpdateSubscriber;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

@RequiredArgsConstructor
public class WorldCupScoreboardService {

    private final MatchScoreboardDao matchScoreboardDao;
    private final Set<ScoreboardStateUpdateSubscriber> subscribers = new HashSet<>();

    public void manageSubscription(ScoreboardStateUpdateSubscriber subscriber, boolean toSubscribe) {
    }

    public Collection<Match> initiateNewMatch(String homeTeamName, String awayTeamName) {
        var newMatch = new Match(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                initiateTeam(homeTeamName),
                initiateTeam(awayTeamName));
        return matchScoreboardDao.create(newMatch);
    }

    public Collection<Match> getAllMatches() {
        return matchScoreboardDao.findAll();
    }

    public Collection<Match> updateScore(String matchId, int homeTeamScore, int awayTeamScore) {
        return matchScoreboardDao.updateScore(matchId, homeTeamScore, awayTeamScore);
    }

    public Collection<Match> deleteMatch(String matchId) {
        return matchScoreboardDao.delete(matchId);
    }

    private Team initiateTeam(String name) {
        return new Team(UUID.randomUUID().toString(), name, 0);
    }
}
