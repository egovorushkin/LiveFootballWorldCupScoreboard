package com.sportradar.scoreboard.exception;

import com.sportradar.scoreboard.model.MatchId;

/**
 * Thrown when attempting to start a match that is already in progress.
 */
public class MatchAlreadyExistsException extends RuntimeException {
    public MatchAlreadyExistsException(MatchId id) {
        super("Match already exists: " + id.homeTeam() + " vs " + id.awayTeam());
    }
}
