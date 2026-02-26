package com.sportradar.scoreboard;

import com.sportradar.scoreboard.exception.MatchAlreadyExistsException;
import com.sportradar.scoreboard.exception.MatchNotFoundException;
import com.sportradar.scoreboard.exception.TeamAlreadyPlayingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sportradar.scoreboard.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FootballWorldCupScoreboardTest {

    private Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        // Given
        scoreboard = new FootballWorldCupScoreboardImpl();
    }

    @Test
    @DisplayName("Given a scoreboard, when startMatch is called, then match is added with 0-0 score")
    void givenScoreboard_whenStartMatch_thenMatchAddedWithZeroZeroScore() {
        // When
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // Then
        var summary = scoreboard.getSummary();
        assertEquals(1, summary.size());

        var match = summary.getFirst();
        assertEquals(TEAM_ENGLAND, match.homeTeam());
        assertEquals(TEAM_SPAIN, match.awayTeam());
        assertEquals(0, match.homeTeamScore());
        assertEquals(0, match.awayTeamScore());
    }

    @Test
    @DisplayName("Given a match already exists, when startMatch is called with same teams, then exception is thrown")
    void givenMatchAlreadyExists_whenStartMatch_thenThrowException() {
        // When
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // Then
        assertThrows(MatchAlreadyExistsException.class, () -> scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN));
    }

    @Test
    @DisplayName("Given a match already exists, when startMatch is called with same teams (case insensitive), then exception is thrown")
    void givenMatchAlreadyExists_whenStartMatch_thenThrowExceptionCaseInsensitive() {
        // When
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // Then
        assertThrows(MatchAlreadyExistsException.class, () -> scoreboard.startMatch(TEAM_ENGLAND.toUpperCase(), TEAM_SPAIN.toUpperCase()));
    }

    @Test
    @DisplayName("Given a team is already playing, when startMatch is called with that team, then exception is thrown")
    void givenTeamAlreadyPlaying_whenStartMatch_thenThrowException() {
        // When
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // Then
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_ENGLAND, TEAM_BRAZIL));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_BRAZIL, TEAM_ENGLAND));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_SPAIN, TEAM_BRAZIL));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_BRAZIL, TEAM_SPAIN));
    }

    @Test
    @DisplayName("Given a team is already playing, when startMatch is called with that team (case insensitive), then exception is thrown")
    void givenTeamAlreadyPlaying_whenStartMatch_thenThrowExceptionCaseInsensitive() {
        // When
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // Then
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_ENGLAND.toUpperCase(), TEAM_BRAZIL));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_BRAZIL, TEAM_ENGLAND.toUpperCase()));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_SPAIN.toUpperCase(), TEAM_BRAZIL));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_BRAZIL, TEAM_SPAIN.toUpperCase()));
    }

    @Test
    @DisplayName("Given a match exists, when updateScore is called, then score is updated")
    void givenMatchExists_whenUpdateScore_thenScoreUpdated() {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // When
        scoreboard.updateScore(TEAM_ENGLAND, TEAM_SPAIN, 1, 0);

        // Then
        var summary = scoreboard.getSummary();
        assertEquals(1, summary.size());

        var match = summary.getFirst();
        assertEquals(TEAM_ENGLAND, match.homeTeam());
        assertEquals(TEAM_SPAIN, match.awayTeam());
        assertEquals(1, match.homeTeamScore());
        assertEquals(0, match.awayTeamScore());
    }


    @Test
    @DisplayName("Given a match does not exist, when updateScore is called, then exception is thrown")
    void givenMatchDoesNotExist_whenUpdateScore_thenThrowException() {
        // When & Then
        assertThrows(MatchNotFoundException.class, () -> scoreboard.updateScore(TEAM_ENGLAND, TEAM_SPAIN, 1, 0));
    }



}