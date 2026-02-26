package com.sportradar.scoreboard.model;

/**
 * Read-only DTO representing a match entry in the scoreboard summary
 * Record containing the home team name, home team score, away team name,
 * away team score, and total score (home score + away score).
 */
public record MatchSummary(
        String homeTeam,
        int homeTeamScore,
        String awayTeam,
        int awayTeamScore,
        int totalScore
) {
    public static MatchSummary from(Match match) {
        return new MatchSummary(
                match.homeTeam(),
                match.homeTeamScore(),
                match.awayTeam(),
                match.awayTeamScore(),
                match.totalScore()
        );
    }

    @Override
    public String toString() {
        return homeTeam + " " + homeTeamScore + " - " + awayTeam + " " + awayTeamScore;
    }
}
