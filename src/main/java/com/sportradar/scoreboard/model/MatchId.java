package com.sportradar.scoreboard.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Record representing the unique identifier of a football match, consisting of the home team and away team names.
 */
public record MatchId(String homeTeam, String awayTeam) {
    public MatchId {
        homeTeam = normalizeTeamName(homeTeam, "Home team");
        awayTeam = normalizeTeamName(awayTeam, "Away team");

        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException(
                    "Home and away teams must be different, got: " + homeTeam
            );
        }
    }

    private static String normalizeTeamName(String name, String label) {
        Objects.requireNonNull(name, label + " must not be null");
        var trimmed = name.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
