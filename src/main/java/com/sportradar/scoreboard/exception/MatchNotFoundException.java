package com.sportradar.scoreboard.exception;

import com.sportradar.scoreboard.model.MatchId;

/**
 * Thrown when an operation targets a match that does not exist on the scoreboard.
 */
public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(MatchId id) {
        super("Match not found: " + id.homeTeam() + " vs " + id.awayTeam());
    }
}
