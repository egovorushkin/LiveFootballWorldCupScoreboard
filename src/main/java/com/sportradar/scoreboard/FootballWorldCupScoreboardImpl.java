package com.sportradar.scoreboard;

import com.sportradar.scoreboard.exception.MatchAlreadyExistsException;
import com.sportradar.scoreboard.exception.TeamAlreadyPlayingException;
import com.sportradar.scoreboard.model.Match;
import com.sportradar.scoreboard.model.MatchId;
import com.sportradar.scoreboard.model.MatchSummary;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootballWorldCupScoreboardImpl implements Scoreboard {

    private final Map<MatchId, Match> matches = new HashMap<>();

    @Override
    public void startMatch(String homeTeam, String awayTeam) {
        var matchId = new MatchId(homeTeam, awayTeam);

        // validation: match already exists
        if (matches.containsKey(matchId)) {
            throw new MatchAlreadyExistsException(matchId);
        }

        // validation: home team or away team is already playing in another match
        boolean teamAlreadyPlaying = matches.keySet().stream().anyMatch(key ->
                key.homeTeam().equals(matchId.homeTeam()) ||
                        key.awayTeam().equals(matchId.homeTeam()) ||
                        key.homeTeam().equals(matchId.awayTeam()) ||
                        key.awayTeam().equals(matchId.awayTeam())
        );

        if (teamAlreadyPlaying) {
            throw new TeamAlreadyPlayingException("One of the teams is already playing in another match");
        }

        var match = new Match(matchId, homeTeam, awayTeam, 0, 0, matches.size());
        matches.put(matchId, match);


    }

    @Override
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void finishMatch(String homeTeam, String awayTeam) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<MatchSummary> getSummary() {
        return matches.values().stream()
                .sorted(Comparator
                        .comparingInt(Match::totalScore).reversed()
                        .thenComparingInt(Match::insertionOrder).reversed())
                .map(MatchSummary::from)
                .toList();
    }
}
