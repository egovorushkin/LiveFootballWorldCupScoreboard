package com.sportradar.scoreboard;

import com.sportradar.scoreboard.model.MatchSummary;

import java.util.List;

/**
 * Interface representing a scoreboard for managing football matches.
 */
public interface Scoreboard {

    /**
     * Starts a new match between the specified home and away teams.
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @throws com.sportradar.scoreboard.exception.MatchAlreadyExistsException if a match with the same teams
     *                                                                         already exists or if either team is already playing in another match
     * @throws com.sportradar.scoreboard.exception.TeamAlreadyPlayingException if either team is already playing
     *                                                                         in another match
     */
    void startMatch(String homeTeam, String awayTeam);

    /**
     * Updates the score of an ongoing match between the specified home and away teams.
     *
     * @param homeTeam  the name of the home team
     * @param awayTeam  the name of the away team
     * @param homeScore the new score for the home team
     * @param awayScore the new score for the away team
     * @throws com.sportradar.scoreboard.exception.MatchNotFoundException if no match is found with the specified teams
     */
    void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore);

    /**
     * Finishes the match between the specified home and away teams, removing it from the scoreboard.
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @throws com.sportradar.scoreboard.exception.MatchNotFoundException if no match is found with the specified teams
     */
    void finishMatch(String homeTeam, String awayTeam);


    /**
     * Retrieves a summary of all ongoing matches, sorted by total score in descending order and then by insertion order.
     *
     * @return a list of MatchSummary objects representing the ongoing matches
     */
    List<MatchSummary> getSummary();
}
