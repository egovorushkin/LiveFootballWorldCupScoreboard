package com.sportradar.scoreboard.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sportradar.scoreboard.TestConstants.TEAM_ENGLAND;
import static com.sportradar.scoreboard.TestConstants.TEAM_SPAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatchTest {

    @Test
    @DisplayName("Given valid match details, when creating a match, then create successfully")
    void givenValidMatchDetails_whenCreatingMatch_thenCreateSuccessfully() {
        var matchId = new MatchId(TEAM_ENGLAND, TEAM_SPAIN);
        var match = new Match(matchId, TEAM_ENGLAND, TEAM_SPAIN, 0, 0, 1);

        assertEquals(matchId, match.id());
        assertEquals(TEAM_ENGLAND, match.homeTeam());
        assertEquals(TEAM_SPAIN, match.awayTeam());
        assertEquals(0, match.homeTeamScore());
        assertEquals(0, match.awayTeamScore());
    }

    @Test
    @DisplayName("Given negative home score, when creating a match, then throw exception")
    void givenNegativeHomeScore_whenCreatingMatch_thenThrowException() {
        var matchId = new MatchId(TEAM_ENGLAND, TEAM_SPAIN);

        assertThrows(IllegalArgumentException.class, () ->
                new Match(matchId, TEAM_ENGLAND, TEAM_SPAIN, -1, 0, 1));
    }

    @Test
    @DisplayName("Given negative away score, when creating a match, then throw exception")
    void givenNegativeAwayScore_whenCreatingMatch_thenThrowException() {
        var matchId = new MatchId(TEAM_ENGLAND, TEAM_SPAIN);

        assertThrows(IllegalArgumentException.class, () ->
                new Match(matchId, TEAM_ENGLAND, TEAM_SPAIN, 0, -1, 1));
    }

    @Test
    @DisplayName("Given negative home and away score, when creating a match, then throw IllegalArgumentException")
    void givenNegativeHomeAndAwayScoreWhenCreatingMatchThenThrowException() {
        var matchId = new MatchId(TEAM_ENGLAND, TEAM_SPAIN);

        assertThrows(IllegalArgumentException.class, () ->
                new Match(matchId, TEAM_ENGLAND, TEAM_SPAIN, 1, -1, 1));
    }

    @Test
    @DisplayName("Given a match, when withScore is called, then return new match with updated scores")
    void givenMatch_whenWithScoreThenReturnNewMatchWithUpdatedScores() {
        var matchId = new MatchId(TEAM_ENGLAND, TEAM_SPAIN);
        var match = new Match(matchId, TEAM_ENGLAND, TEAM_SPAIN, 0, 0, 1);

        var updatedMatch = match.withScore(2, 1);

        assertEquals(matchId, updatedMatch.id());
        assertEquals(TEAM_ENGLAND, updatedMatch.homeTeam());
        assertEquals(TEAM_SPAIN, updatedMatch.awayTeam());
        assertEquals(2, updatedMatch.homeTeamScore());
        assertEquals(1, updatedMatch.awayTeamScore());

        // original match should remain unchanged
        assertEquals(0, match.homeTeamScore());
        assertEquals(0, match.awayTeamScore());
    }

    @Test
    @DisplayName("Given a match, when totalScore is called, then return sum of home and away scores")
    void givenMatchWhenTotalScoreThenReturnSumOfHomeAndAwayScores() {
        var matchId = new MatchId(TEAM_ENGLAND, TEAM_SPAIN);
        var match = new Match(matchId, TEAM_ENGLAND, TEAM_SPAIN, 2, 1, 1);

        assertEquals(3, match.totalScore());
    }

}