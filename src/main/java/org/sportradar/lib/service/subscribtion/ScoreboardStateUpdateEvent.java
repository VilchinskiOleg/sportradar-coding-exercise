package org.sportradar.lib.service.subscribtion;

import org.sportradar.lib.model.Match;

import java.util.Collection;
import java.util.Map;

public record ScoreboardStateUpdateEvent(EventType eventType,
                                         Map<String, Object> parameters,
                                         Collection<Match> updatedScoreboardState) {

    public enum EventType {

        CREATED_MATCH, UPDATED_MATCH, DELETED_MATCH;
    }
}
