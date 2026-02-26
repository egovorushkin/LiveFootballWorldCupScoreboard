package com.sportradar.scoreboard.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sportradar.scoreboard.TestConstants.TEAM_ENGLAND;
import static com.sportradar.scoreboard.TestConstants.TEAM_SPAIN;
import static org.junit.jupiter.api.Assertions.*;

class MatchIdTest {

    @Test
    @DisplayName("Given valid team names, when creating MatchId, then success")
    void givenValidTeamNames_whenCreatingMatchId_thenSuccess() {
        var matchId = new MatchId(TEAM_ENGLAND, TEAM_SPAIN);
        assertEquals("england", matchId.homeTeam());
        assertEquals("spain", matchId.awayTeam());
    }

    @Test
    @DisplayName("Given same team names, when creating MatchId, then throw exception")
    void givenNullTeamName_whenCreatingMatchId_thenThrowException() {
        assertThrows(NullPointerException.class, () -> new MatchId(null, TEAM_SPAIN));
        assertThrows(NullPointerException.class, () -> new MatchId(TEAM_ENGLAND, null));
    }

    @Test
    @DisplayName("Given blank team names, when creating MatchId, then throw exception")
    void givenBlankTeamName_whenCreatingMatchId_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new MatchId("   ", TEAM_SPAIN));
        assertThrows(IllegalArgumentException.class, () -> new MatchId(TEAM_ENGLAND, "   "));
    }

    @Test
    @DisplayName("Given same team names, when creating MatchId, then throw exception")
    void givenSameTeamNames_whenCreatingMatchId_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new MatchId(TEAM_ENGLAND, TEAM_ENGLAND));
    }

}