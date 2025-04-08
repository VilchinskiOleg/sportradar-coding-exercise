package org.sportradar.lib.service.dao;

import org.sportradar.lib.model.Match;

public interface MatchDao extends CrudDao<Match> {

    int updateScore(String matchId, int homeTeamScore, int awayTeamScore);
}
