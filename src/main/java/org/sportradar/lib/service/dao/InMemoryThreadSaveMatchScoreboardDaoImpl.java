package org.sportradar.lib.service.dao;

import org.sportradar.lib.exception.MatchNotFoundException;
import org.sportradar.lib.model.Match;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class InMemoryThreadSaveMatchScoreboardDaoImpl implements MatchScoreboardDao {

    private final Map<String, Match> itemsById;
    private final NavigableSet<Match> sortedItems;

    public InMemoryThreadSaveMatchScoreboardDaoImpl(Comparator<Match> defaultRepresentationOrder) {
        this.itemsById = new ConcurrentHashMap<>();
        this.sortedItems = new ConcurrentSkipListSet<>(defaultRepresentationOrder);
    }

    @Override
    public NavigableSet<Match> create(Match match) {
        itemsById.put(match.matchId(), match);
        sortedItems.add(match);
        return sortedItems;
    }

    @Override
    public NavigableSet<Match> findAll() {
        return sortedItems;
    }

    @Override
    public NavigableSet<Match> updateScore(String matchId, int homeTeamScore, int awayTeamScore) {
        Match target = getTargetMatch(matchId);
        target.homeTeam().setScore(homeTeamScore);
        target.awayTeam().setScore(awayTeamScore);
        return sortedItems;
    }

    @Override
    public NavigableSet<Match> delete(String matchId) {
        Match target = getTargetMatch(matchId);
        sortedItems.remove(target);
        itemsById.remove(matchId);
        return sortedItems;
    }

    private Match getTargetMatch(String matchId) {
        return Optional.ofNullable(itemsById.get(matchId))
                .orElseThrow(() -> new MatchNotFoundException(matchId));
    }
}
