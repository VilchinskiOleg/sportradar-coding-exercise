package org.sportradar.lib.service.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.sportradar.lib.model.Match;
import org.sportradar.lib.model.Team;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryThreadSaveMatchDaoImplTest {

    private MatchScoreboardDao matchDao = new InMemoryThreadSaveMatchScoreboardDaoImpl(Comparator
            .comparing(Match::getCommonScore)
            .thenComparing(Match::startTime).reversed());

    @AfterEach
    void cleanEach() {
        matchDao.findAll().clear();
    }

    @Test
    void testCreateNewMatchesAndGetThemAllOrdered() throws InterruptedException {
        assertTrue(matchDao.findAll().isEmpty());

        matchDao.create(getMatchMock("HomeTeam-1", 1, "AwayTeam-1", 0));
        matchDao.create(getMatchMock("HomeTeam-2", 2,"AwayTeam-2", 3));
        Thread.sleep(1500);
        Match match3 = getMatchMock("HomeTeam-3", 2,"AwayTeam-3", 3);
        matchDao.create(match3);
        Thread.sleep(500);
        matchDao.create(getMatchMock("HomeTeam-4", 0,"AwayTeam-4", 0));

        var res = matchDao.findAll();
        assertEquals(4, matchDao.findAll().size());
        assertEquals(
                List.of("HomeTeam-3", "HomeTeam-2", "HomeTeam-1", "HomeTeam-4"),
                res.stream().map(match -> match.homeTeam().getName()).toList());
    }

    @Test
    void testCreateNewMatchAndDelete() {
        assertTrue(matchDao.findAll().isEmpty());

        Match match = getMatchMock();

        assertEquals(1, matchDao.create(match).size());
        assertEquals(0, matchDao.delete(match.matchId()).size());
        assertTrue(matchDao.findAll().isEmpty());
    }

    @Test
    void testCreateNewMatchAndUpdateScore() {
        assertTrue(matchDao.findAll().isEmpty());

        Match match = getMatchMock();
        assertEquals(0, match.homeTeam().getScore());
        assertEquals(0, match.awayTeam().getScore());
        matchDao.create(match);

        assertEquals(1, matchDao.findAll().size());
        matchDao.updateScore(match.matchId(), 2, 1);

        assertEquals(2, match.homeTeam().getScore());
        assertEquals(1, match.awayTeam().getScore());
    }

    private Match getMatchMock() {
        var homeTeam = new Team(UUID.randomUUID().toString(), "HomeTeam", 0);
        var awayTeam = new Team(UUID.randomUUID().toString(), "AwayTeam", 0);
        return new Match(UUID.randomUUID().toString(), LocalDateTime.now(), homeTeam, awayTeam);
    }

    private Match getMatchMock(String homeTeamName, int homeTeamScore, String awayTeamName, int awayTeamScore) {
        var homeTeam = new Team(UUID.randomUUID().toString(), homeTeamName, homeTeamScore);
        var awayTeam = new Team(UUID.randomUUID().toString(), awayTeamName, awayTeamScore);
        return new Match(UUID.randomUUID().toString(), LocalDateTime.now(), homeTeam, awayTeam);
    }
}
