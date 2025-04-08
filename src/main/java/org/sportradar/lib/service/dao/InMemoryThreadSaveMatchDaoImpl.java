package org.sportradar.lib.service.dao;

import org.sportradar.lib.model.Match;

import java.util.NavigableSet;

public class InMemoryThreadSaveMatchDaoImpl implements MatchDao {

    @Override
    public Match create(Match match) {
        return null;
    }

    @Override
    public NavigableSet<Match> findAll() {
        return null;
    }

    @Override
    public int updateScore(String matchId, int homeTeamScore, int awayTeamScore) {
        return 0;
    }

    @Override
    public int delete(String matchId) {
        return 0;
    }
}
