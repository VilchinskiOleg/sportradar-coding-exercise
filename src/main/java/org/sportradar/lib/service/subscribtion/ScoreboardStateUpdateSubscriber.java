package org.sportradar.lib.service.subscribtion;

public interface ScoreboardStateUpdateSubscriber {

    void receiveScoreboardStateUpdate(ScoreboardStateUpdateEvent event);
}
