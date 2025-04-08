package org.sportradar.lib.service.dao;

import org.sportradar.lib.model.Match;

import java.util.Collection;

public interface MatchScoreboardDao extends CrudScoreboardDao<Match> {

    Collection<Match> updateScore(String matchId, int homeTeamScore, int awayTeamScore);
}
