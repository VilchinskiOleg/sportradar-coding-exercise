package org.sportradar.lib.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sportradar.lib.MatchMockUtils;
import org.sportradar.lib.model.Match;
import org.sportradar.lib.service.dao.MatchScoreboardDao;
import org.sportradar.lib.service.subscribtion.ScoreboardStateUpdateEvent;
import org.sportradar.lib.service.subscribtion.ScoreboardStateUpdateEvent.EventType;
import org.sportradar.lib.service.subscribtion.ScoreboardStateUpdateSubscriber;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class WorldCupScoreboardServiceTest {

    MatchScoreboardDao matchScoreboardDao = mock(MatchScoreboardDao.class);
    WorldCupScoreboardService scoreboardService = new WorldCupScoreboardService(matchScoreboardDao);

    TestSubscriber subscriber = new TestSubscriber();

    @BeforeAll
    void initAll() {
        scoreboardService.manageSubscription(subscriber, true);
    }

    @Test
    void testInitiateNewMatch() {
        doAnswer(invocation -> Collections.singleton(invocation.getArgument(0)))
                .when(matchScoreboardDao).create(any());
        var results = scoreboardService.initiateNewMatch("ht1", "at1");

        // Validate base functionality :
        assertTrue(results.iterator().hasNext());
        Match createdMatch = results.iterator().next();
        assertEquals("ht1", createdMatch.homeTeam().getName());
        assertEquals(0, createdMatch.homeTeam().getScore());
        assertEquals("at1", createdMatch.awayTeam().getName());
        assertEquals(0, createdMatch.awayTeam().getScore());

        // Validate subscription :
        assertNotNull(subscriber.receivedEvent);
        assertEquals(EventType.CREATED_MATCH, subscriber.receivedEvent.eventType());
        assertEquals(results, subscriber.receivedEvent.updatedScoreboardState());
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

        // Validate base functionality :
        verify(matchScoreboardDao).updateScore(eq("123"), eq(1), eq(0));
        assertTrue(res.iterator().hasNext());
        assertEquals(updatedMatch, res.iterator().next());

        // Validate subscription :
        assertNotNull(subscriber.receivedEvent);
        assertEquals(EventType.UPDATED_MATCH, subscriber.receivedEvent.eventType());
        assertEquals(res, subscriber.receivedEvent.updatedScoreboardState());
    }

    @Test
    void testDeleteMatchCallJustDelegatedToDAO() {
        doReturn(Collections.EMPTY_LIST).when(matchScoreboardDao).delete(eq("123"));
        var res = scoreboardService.deleteMatch("123");

        // Validate base functionality :
        verify(matchScoreboardDao).delete(eq("123"));
        assertTrue(res.isEmpty());

        // Validate subscription :
        assertNotNull(subscriber.receivedEvent);
        assertEquals(EventType.DELETED_MATCH, subscriber.receivedEvent.eventType());
        assertEquals(res, subscriber.receivedEvent.updatedScoreboardState());
    }


    private class TestSubscriber implements ScoreboardStateUpdateSubscriber {

        ScoreboardStateUpdateEvent receivedEvent;

        @Override
        public void receiveScoreboardStateUpdate(ScoreboardStateUpdateEvent event) {
            receivedEvent = event;
        }
    }
}
