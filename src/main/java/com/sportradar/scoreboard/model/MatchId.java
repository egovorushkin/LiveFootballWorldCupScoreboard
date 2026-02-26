package com.sportradar.scoreboard.model;

/**
 * Record representing the unique identifier of a football match, consisting of the home team and away team names.
 */
public record MatchId(String homeTeam, String awayTeam) {
}
