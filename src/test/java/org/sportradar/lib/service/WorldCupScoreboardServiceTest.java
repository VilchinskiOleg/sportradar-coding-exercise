package org.sportradar.lib.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sportradar.lib.MatchMockUtils;
import org.sportradar.lib.model.Match;
import org.sportradar.lib.service.dao.MatchScoreboardDao;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorldCupScoreboardServiceTest {

    @InjectMocks
    WorldCupScoreboardService scoreboardService;
    @Mock
    MatchScoreboardDao matchScoreboardDao;

    @Test
    void testInitiateNewMatch() {
        doAnswer(invocation -> Collections.singleton(invocation.getArgument(0)))
                .when(matchScoreboardDao).create(any());
        var results = scoreboardService.initiateNewMatch("ht1", "at1");

        assertTrue(results.iterator().hasNext());
        Match createdMatch = results.iterator().next();
        assertEquals("ht1", createdMatch.homeTeam().getName());
        assertEquals(0, createdMatch.homeTeam().getScore());
        assertEquals("at1", createdMatch.awayTeam().getName());
        assertEquals(0, createdMatch.awayTeam().getScore());
    }

    @Test
    void testGetAllMatchesRetainingOrder() throws InterruptedException {
        var orderedMatches = new TreeSet<>(Comparator.comparing(Match::getCommonScore)
                .thenComparing(Match::startTime).reversed());
        orderedMatches.add(MatchMockUtils.getMatchMock("HT-1", 1, "AT-1", 0));
        orderedMatches.add(MatchMockUtils.getMatchMock("HT-2", 2,"AT-2", 3));
        Thread.sleep(1500);
        orderedMatches.add(MatchMockUtils.getMatchMock("HT-3", 2,"AT-3", 3));
        Thread.sleep(500);
        orderedMatches.add(MatchMockUtils.getMatchMock("HT-4", 0,"AT-4", 0));

        assertEquals(
                List.of("HT-3", "HT-2", "HT-1", "HT-4"),
                orderedMatches.stream().map(match -> match.homeTeam().getName()).toList());

        doReturn(orderedMatches).when(matchScoreboardDao).findAll();

        assertEquals(
                List.of("HT-3", "HT-2", "HT-1", "HT-4"),
                scoreboardService.getAllMatches().stream().map(match -> match.homeTeam().getName()).toList());
    }

    @Test
    void testUpdateScoreCallJustDelegatedToDAO() {
        var updatedMatch = MatchMockUtils.getMatchMock("ht", 1, "at", 0);
        doReturn(Collections.singleton(updatedMatch)).when(matchScoreboardDao).updateScore(eq("123"), eq(1), eq(0));
        var res = scoreboardService.updateScore("123", 1, 0);

        verify(matchScoreboardDao).updateScore(eq("123"), eq(1), eq(0));
        assertTrue(res.iterator().hasNext());
        assertEquals(updatedMatch, res.iterator().next());
    }

    @Test
    void testDeleteMatchCallJustDelegatedToDAO() {
        doReturn(Collections.EMPTY_LIST).when(matchScoreboardDao).delete(eq("123"));
        var res = scoreboardService.deleteMatch("123");

        verify(matchScoreboardDao).delete(eq("123"));
        assertTrue(res.isEmpty());
    }
}
