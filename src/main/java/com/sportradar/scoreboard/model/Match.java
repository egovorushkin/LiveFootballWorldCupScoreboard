package com.sportradar.scoreboard.model;

/**
 * Record representing a football match, containing the unique identifier (MatchId), home team name, away team name,
 * home team score, and away team score.
 */
public record Match(MatchId id,
                    String homeTeam,
                    String awayTeam,
                    int homeTeamScore,
                    int awayTeamScore,
                    int insertionOrder) {
}
