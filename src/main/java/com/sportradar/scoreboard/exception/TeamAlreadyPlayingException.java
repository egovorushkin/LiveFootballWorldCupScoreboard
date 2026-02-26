package com.sportradar.scoreboard.exception;

/**
 * Thrown when attempting to start a match where one or both teams
 * are already participating in another ongoing match.
 */
public class TeamAlreadyPlayingException extends RuntimeException {
    public TeamAlreadyPlayingException(String teamName) {
        super("Team is already playing in another match: " + teamName);
    }
}
