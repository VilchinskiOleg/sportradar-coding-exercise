package org.sportradar.lib.exception;

public class MatchNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "";
    private final String matchId;

    public MatchNotFoundException(String matchId) {
        super();
        this.matchId = matchId;
    }

    @Override
    public String getMessage() {
        return String.format(ERROR_MESSAGE_TEMPLATE, matchId);
    }


}
