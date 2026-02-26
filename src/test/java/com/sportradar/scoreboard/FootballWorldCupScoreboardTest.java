package com.sportradar.scoreboard;

import com.sportradar.scoreboard.exception.MatchAlreadyExistsException;
import com.sportradar.scoreboard.exception.MatchNotFoundException;
import com.sportradar.scoreboard.exception.TeamAlreadyPlayingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.sportradar.scoreboard.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class FootballWorldCupScoreboardTest {

    private Scoreboard scoreboard;

    @BeforeEach
    void initializeEmptyScoreboard() {
        scoreboard = new FootballWorldCupScoreboardImpl();
    }

    @Test
    @DisplayName("Given a scoreboard, when startMatch is called, then match is added with 0-0 score")
    void givenScoreboard_whenStartMatch_thenMatchAddedWithZeroZeroScore() {
        // When
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // Then
        var summary = scoreboard.getSummary();
        assertEquals(1, summary.size(), "Summary should contain one match");

        var match = summary.getFirst();
        assertEquals(TEAM_ENGLAND, match.homeTeam(), "Home team should be England");
        assertEquals(TEAM_SPAIN, match.awayTeam(), "Away team should be Spain");
        assertEquals(0, match.homeTeamScore(), "Home team score should start at 0");
        assertEquals(0, match.awayTeamScore(), "Away team score should start at 0");
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
        String teamEnglandUpperCase = TEAM_ENGLAND.toUpperCase();
        String teamSpainUpperCase = TEAM_SPAIN.toUpperCase();
        assertThrows(MatchAlreadyExistsException.class, () -> scoreboard.startMatch(teamEnglandUpperCase, teamSpainUpperCase));
    }

    @ParameterizedTest
    @CsvSource({
            "England, Brazil",
            "Brazil, England",
            "Spain, Brazil",
            "Brazil, Spain"
    })
    @DisplayName("Given a team is already playing, when startMatch is called with that team, then exception is thrown")
    void givenTeamAlreadyPlaying_whenStartMatch_thenThrowException(String homeTeam, String awayTeam) {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // When & Then
        assertThrows(TeamAlreadyPlayingException.class, () ->
            scoreboard.startMatch(homeTeam, awayTeam),
            "Team already playing should throw TeamAlreadyPlayingException");
    }

    @Test
    @DisplayName("Given a team is already playing, when startMatch is called with that team (case insensitive), then exception is thrown")
    void givenTeamAlreadyPlaying_whenStartMatch_thenThrowExceptionCaseInsensitive() {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // When & Then
        String teamEnglandUpperCase = TEAM_ENGLAND.toUpperCase();
        String teamSpainUpperCase = TEAM_SPAIN.toUpperCase();
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(teamEnglandUpperCase, TEAM_BRAZIL));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_BRAZIL, teamEnglandUpperCase));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(teamSpainUpperCase, TEAM_BRAZIL));
        assertThrows(TeamAlreadyPlayingException.class, () -> scoreboard.startMatch(TEAM_BRAZIL, teamSpainUpperCase));
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

    @ParameterizedTest
    @CsvSource({
            "-1, 0",
            "0, -1",
            "-1, -1"
    })
    @DisplayName("Given negative scores, when updateScore is called, then exception is thrown")
    void givenNegativeScores_whenUpdateScore_thenThrowException(int homeScore, int awayScore) {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            scoreboard.updateScore(TEAM_ENGLAND, TEAM_SPAIN, homeScore, awayScore),
            "Negative scores should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Given a match exists, when updateScore is called with case-insensitive team names, then score is updated")
    void givenMatchExists_whenUpdateScoreCaseInsensitive_thenScoreUpdated() {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // When
        String teamEnglandUpperCase = TEAM_ENGLAND.toUpperCase();
        String teamSpainUpperCase = TEAM_SPAIN.toUpperCase();
        scoreboard.updateScore(teamEnglandUpperCase, teamSpainUpperCase, 2, 1);

        // Then
        var summary = scoreboard.getSummary();
        assertEquals(1, summary.size());

        var match = summary.getFirst();
        assertEquals(2, match.homeTeamScore());
        assertEquals(1, match.awayTeamScore());
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
    @DisplayName("Given a match exists, when finishMatch is called with case-insensitive team names, then match is removed from scoreboard")
    void givenMatchExists_whenFinishMatchCaseInsensitive_thenMatchRemovedFromScoreboard() {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);

        // When
        String teamEnglandUpperCase = TEAM_ENGLAND.toUpperCase();
        String teamSpainUpperCase = TEAM_SPAIN.toUpperCase();
        scoreboard.finishMatch(teamEnglandUpperCase, teamSpainUpperCase);

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
    @DisplayName("Given empty scoreboard, when getSummary is called, then return empty list")
    void givenEmptyScoreboard_whenGetSummary_thenReturnEmptyList() {
        // When
        var summary = scoreboard.getSummary();

        // Then
        assertTrue(summary.isEmpty());
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

    @Test
    @DisplayName("Given getSummary is called, when scoreboard is updated, then summary remains unchanged (immutable snapshot)")
    void givenGetSummaryIsCalled_whenScoreboardIsUpdated_thenSummaryUnchanged() {
        // Given
        scoreboard.startMatch(TEAM_ENGLAND, TEAM_SPAIN);
        var summary1 = scoreboard.getSummary();

        // When
        scoreboard.updateScore(TEAM_ENGLAND, TEAM_SPAIN, 5, 3);

        // Then
        assertEquals(0, summary1.getFirst().homeTeamScore(),
            "Snapshot should preserve original state");
        assertEquals(0, summary1.getFirst().awayTeamScore(),
            "Snapshot should preserve original state");

        // And
        var summary2 = scoreboard.getSummary();
        assertEquals(5, summary2.getFirst().homeTeamScore(),
            "New summary should reflect updated score");
        assertEquals(3, summary2.getFirst().awayTeamScore(),
            "New summary should reflect updated score");
    }
}