package com.sportradar.scoreboard;

import com.sportradar.scoreboard.exception.MatchAlreadyExistsException;
import com.sportradar.scoreboard.exception.MatchNotFoundException;
import com.sportradar.scoreboard.exception.TeamAlreadyPlayingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sportradar.scoreboard.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("Given a match exists, when finishMatch is called, then match is removed from scoreboard")
    void givenMatchExists_whenFinishMatch_thenMatchRemovedFromScoreboard() {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // When
        scoreboard.finishMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // Then
        var summary = scoreboard.getSummary();
        assertTrue(summary.isEmpty());
    }

    @Test
    @DisplayName("Given a match does not exist, when finishMatch is called, then exception is thrown")
    void givenMatchDoesNotExist_whenFinishMatch_thenThrowException() {
        // When & Then
        assertThrows(MatchNotFoundException.class, () -> scoreboard.finishMatch(TEAM_ENGLAND, TEAM_SPAIN));
    }


    @Test
    @DisplayName("Given multiple matches with different scores, when getSummary is called, then matches are ordered by total score descending")
    void givenMultipleMatches_whenGetSummary_thenReturnMatchesOrderedByTotalScoreDescending() {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);
        scoreboard.startMatch(TEAM_BRAZIL, TEAM_ARGENTINA);
        scoreboard.updateScore(TEAM_ENGLAND, TEAM_SPAIN, 2, 1);
        scoreboard.updateScore(TEAM_BRAZIL, TEAM_ARGENTINA, 3, 2);

        // When
        var summary = scoreboard.getSummary();

        // Then
        assertEquals(2, summary.size());
        assertEquals(TEAM_BRAZIL, summary.get(0).homeTeam());
        assertEquals(TEAM_ARGENTINA, summary.get(0).awayTeam());
        assertEquals(TEAM_ENGLAND, summary.get(1).homeTeam());
        assertEquals(TEAM_SPAIN, summary.get(1).awayTeam());
    }

    @Test
    @DisplayName("Given multiple matches with same total scores, when getSummary is called, then matches are ordered by recently started match")
    void givenMultipleMatchesWithSameTotalScores_whenGetSummary_thenReturnMatchesOrderedByRecentlyStartedMatch() {
        // Given
        scoreboard.startMatch(TEAM_MEXICO, TEAM_CANADA);
        scoreboard.startMatch(TEAM_SPAIN, TEAM_BRAZIL);
        scoreboard.startMatch(TEAM_GERMANY, TEAM_FRANCE);
        scoreboard.startMatch(TEAM_URUGUAY, TEAM_ITALY);
        scoreboard.startMatch(TEAM_ARGENTINA, TEAM_AUSTRALIA);
        scoreboard.updateScore(TEAM_MEXICO, TEAM_CANADA, 0, 5);
        scoreboard.updateScore(TEAM_SPAIN, TEAM_BRAZIL, 10, 1);
        scoreboard.updateScore(TEAM_GERMANY, TEAM_FRANCE, 2, 2);
        scoreboard.updateScore(TEAM_URUGUAY, TEAM_ITALY, 6, 6);
        scoreboard.updateScore(TEAM_ARGENTINA, TEAM_AUSTRALIA, 3, 1);

        // When
        var summary = scoreboard.getSummary();

        // Then
        assertEquals(5, summary.size());
        assertEquals(TEAM_URUGUAY, summary.get(0).homeTeam());
        assertEquals(TEAM_ITALY, summary.get(0).awayTeam());
        assertEquals(TEAM_SPAIN, summary.get(1).homeTeam());
        assertEquals(TEAM_BRAZIL, summary.get(1).awayTeam());
        assertEquals(TEAM_MEXICO, summary.get(2).homeTeam());
        assertEquals(TEAM_CANADA, summary.get(2).awayTeam());
        assertEquals(TEAM_ARGENTINA, summary.get(3).homeTeam());
        assertEquals(TEAM_AUSTRALIA, summary.get(3).awayTeam());
        assertEquals(TEAM_GERMANY, summary.get(4).homeTeam());
        assertEquals(TEAM_FRANCE, summary.get(4).awayTeam());
    }
}