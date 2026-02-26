package com.sportradar.scoreboard;

import com.sportradar.scoreboard.exception.MatchAlreadyExistsException;
import com.sportradar.scoreboard.exception.MatchNotFoundException;
import com.sportradar.scoreboard.exception.TeamAlreadyPlayingException;
import com.sportradar.scoreboard.model.Match;
import com.sportradar.scoreboard.model.MatchId;
import com.sportradar.scoreboard.model.MatchSummary;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FootballWorldCupScoreboardImpl implements Scoreboard {

    private final Map<MatchId, Match> matches = new HashMap<>();
    private int insertionOrderCounter = 0;

    @Override
    public void startMatch(String homeTeam, String awayTeam) {
        var matchId = new MatchId(homeTeam, awayTeam);

        validateMatch(matchId);

        var match = new Match(matchId, homeTeam, awayTeam, 0, 0, insertionOrderCounter++);
        matches.put(matchId, match);
    }

    private void validateMatch(MatchId matchId) {
        // validation: match already exists
        if (matches.containsKey(matchId)) {
            throw new MatchAlreadyExistsException(matchId);
        }

        // validation: home team or away team is already playing in another match
        matches.keySet().stream()
                .flatMap(key -> Stream.of(key.homeTeam(), key.awayTeam()))
                .filter(team -> team.equals(matchId.homeTeam()) || team.equals(matchId.awayTeam()))
                .findFirst()
                .ifPresent(team -> {
                    throw new TeamAlreadyPlayingException(team);
                });
    }

    @Override
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        var id = getMatchId(homeTeam, awayTeam);

        matches.computeIfPresent(id, (_, existingMatch) -> existingMatch.withScore(homeScore, awayScore));
    }

    @Override
    public void finishMatch(String homeTeam, String awayTeam) {
        var id = getMatchId(homeTeam, awayTeam);

        matches.remove(id);
    }

    private MatchId getMatchId(String homeTeam, String awayTeam) {
        var id = new MatchId(homeTeam, awayTeam);

        if (matches.get(id) == null) {
            throw new MatchNotFoundException(id);
        }

        return id;
    }

    @Override
    public List<MatchSummary> getSummary() {
        return matches.values().stream()
                .sorted(Comparator
                        .comparingInt(Match::totalScore)
                        .thenComparingInt(Match::insertionOrder).reversed())
                .map(MatchSummary::from)
                .toList();
    }
}
