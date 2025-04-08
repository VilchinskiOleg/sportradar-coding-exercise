package org.sportradar.lib.service.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.sportradar.lib.model.Match;
import org.sportradar.lib.model.Team;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryThreadSaveMatchDaoImplTest {

    private MatchDao matchDao = new InMemoryThreadSaveMatchDaoImpl();

    @AfterEach
    void cleanEach() {
        matchDao.findAll().clear();
    }

    @Test
    void testCreateNewMatchesAndGetThemAllOrdered() throws InterruptedException {
        assertTrue(matchDao.findAll().isEmpty());

        matchDao.create(getMatchMock("HomeTeam-1", 1, "AwayTeam-1", 0));
        matchDao.create(getMatchMock("HomeTeam-2", 2,"AwayTeam-2", 3));
        Thread.sleep(500);
        Match match3 = getMatchMock("HomeTeam-3", 2,"AwayTeam-3", 3);
        Match createdMatch3 = matchDao.create(match3);

        assertEquals(match3, createdMatch3);
        var res = matchDao.findAll();
        assertEquals(3, matchDao.findAll().size());
        assertEquals(match3, res.iterator().next());
    }

    @Test
    void testCreateNewMatchAndDelete() {
        assertTrue(matchDao.findAll().isEmpty());

        Match createdMatch = matchDao.create(getMatchMock());

        assertEquals(1, matchDao.findAll().size());
        assertEquals(1, matchDao.delete(createdMatch.matchId()));
        assertTrue(matchDao.findAll().isEmpty());
    }

    @Test
    void testCreateNewMatchAndUpdateScore() {
        assertTrue(matchDao.findAll().isEmpty());

        Match createdMatch = matchDao.create(getMatchMock());
        assertEquals(0, createdMatch.homeTeam().score());
        assertEquals(0, createdMatch.awayTeam().score());

        assertEquals(1, matchDao.findAll().size());
        assertEquals(1, matchDao.updateScore(createdMatch.matchId(), 2, 1));

        assertEquals(2, createdMatch.homeTeam().score());
        assertEquals(1, createdMatch.awayTeam().score());
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
