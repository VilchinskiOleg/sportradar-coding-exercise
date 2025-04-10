package org.sportradar.lib.service;

import lombok.RequiredArgsConstructor;
import org.sportradar.lib.model.Match;
import org.sportradar.lib.model.Team;
import org.sportradar.lib.service.dao.MatchScoreboardDao;
import org.sportradar.lib.service.subscribtion.ScoreboardStateUpdateEvent;
import org.sportradar.lib.service.subscribtion.ScoreboardStateUpdateSubscriber;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static org.sportradar.lib.service.subscribtion.ScoreboardStateUpdateEvent.EventType;

@RequiredArgsConstructor
public class WorldCupScoreboardService {

    private final MatchScoreboardDao matchScoreboardDao;
    private final Set<ScoreboardStateUpdateSubscriber> subscribers = new HashSet<>();

    public void manageSubscription(ScoreboardStateUpdateSubscriber subscriber, boolean toSubscribe) {
        if (toSubscribe) {
            subscribers.add(subscriber);
        } else {
            subscribers.remove(subscriber);
        }
    }

    public Collection<Match> initiateNewMatch(String homeTeamName, String awayTeamName) {
        var newMatch = new Match(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                initiateTeam(homeTeamName),
                initiateTeam(awayTeamName));
        Collection<Match> updatedScoreboardState = matchScoreboardDao.create(newMatch);
        notifySubscribersWithUpdate(EventType.CREATED_MATCH, updatedScoreboardState,
                Map.of("homeTeamName", homeTeamName, "awayTeamName", awayTeamName));
        return updatedScoreboardState;
    }

    public Collection<Match> getAllMatches() {
        return matchScoreboardDao.findAll();
    }

    public Collection<Match> updateScore(String matchId, int homeTeamScore, int awayTeamScore) {
        Collection<Match> updatedScoreboardState = matchScoreboardDao.updateScore(matchId, homeTeamScore, awayTeamScore);
        notifySubscribersWithUpdate(EventType.UPDATED_MATCH, updatedScoreboardState,
                Map.of("matchId", matchId, "homeTeamScore", homeTeamScore, "awayTeamScore", awayTeamScore));
        return updatedScoreboardState;
    }

    public Collection<Match> deleteMatch(String matchId) {
        Collection<Match> updatedScoreboardState = matchScoreboardDao.delete(matchId);
        notifySubscribersWithUpdate(EventType.DELETED_MATCH, updatedScoreboardState, Map.of("matchId", matchId));
        return updatedScoreboardState;
    }

    private Team initiateTeam(String name) {
        return new Team(UUID.randomUUID().toString(), name, 0);
    }

    private void notifySubscribersWithUpdate(EventType eventType,
                                             Collection<Match> updatedScoreboardState,
                                             Map<String, Object> parameters) {
        if (subscribers.isEmpty()) return;
        var event = new ScoreboardStateUpdateEvent(eventType, parameters, updatedScoreboardState);
        subscribers.forEach(subscriber -> subscriber.receiveScoreboardStateUpdate(event));
    }
}
