package com.sportradar.scoreboard.model;

import java.util.Objects;

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

    public Match {
        Objects.requireNonNull(id, "Match ID must not be null");
        Objects.requireNonNull(homeTeam, "Home team display name must not be null");
        Objects.requireNonNull(awayTeam, "Away team display name must not be null");
        validateScore(homeTeamScore, "Home score");
        validateScore(awayTeamScore, "Away score");
    }

    public int totalScore() {
        return homeTeamScore + awayTeamScore;
    }

    /**
     * Returns a new Match instance with updated scores.
     */
    public Match withScore(int homeScore, int awayScore) {
        return new Match(id, homeTeam, awayTeam, homeScore, awayScore, insertionOrder);
    }

    @Override
    public String toString() {
        return homeTeam + " " + homeTeamScore + " - " + awayTeam + " " + awayTeamScore;
    }

    private static void validateScore(int score, String label) {
        if (score < 0) {
            throw new IllegalArgumentException(label + " cannot be negative, got: " + score);
        }
    }
}
