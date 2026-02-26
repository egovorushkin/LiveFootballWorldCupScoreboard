# Live Football World Cup Scoreboard

A simple in-memory library implementing a Live Football World Cup Scoreboard
that shows ongoing matches and their scores. The library provides an API to start matches, update scores, finish matches, and retrieve a summary of ongoing matches sorted by total score and most recently started.

---

## Requirements

- Java 25
- Maven 3.9

---

## Build & Test
```bash
mvn clean test
```

---

## Project Structure
```
src/
├── main/java/com/sportradar/scoreboard/
│   │── exception/
│   │   ├── MatchAlreadyExistsException.java
│   │   ├── MatchNotFoundException.java
│   │   └── TeamAlreadyPlayingException.java
│   ├── model/
│   │   ├── Match.java                        # Immutable domain record
│   │   ├── MatchId.java                      # Value object — match identity
│   │   └── MatchSummary.java                 # Read-only DTO for public API
│   │── Scoreboard.java                       # Public interface (API contract)
│   └── FootballWorldCupScoreboardImpl.java   # In-memory implementation of the scoreboard
└── test/java/com/sportradar/scoreboard/
    │── model/
    │   ├── MatchIdTest.java
    │   └── MatchTest.java
    │── FootballWorldCupScoreboardTest.java
    └── TestConstants.java

```

---

## Design Decisions

### Immutable Domain Model
`Match` and `MatchId` are Java records - immutable by design. Score updates
produce new `Match` instances via `withScore()`, eliminating mutation-related bugs
and making state changes explicit.

### MatchId Normalization
Team names are normalized to lowercase at `MatchId` construction time using
`Locale.ROOT`. This enforces case-insensitive equality at the domain level - no `equalsIgnoreCase` scattered across the codebase.

### Identity and Presentation
`MatchId` stores normalized names (`"mexico"`) for equality and map key purposes.
`Match` separately stores original display names (`"Mexico"`) so the public API
returns team names exactly as the caller provided them.

### Single Data Structure
`FootballWorldCupScoreboardImpl` uses a single `HashMap<MatchId, Match>` as its only
data structure.

### Insertion Order
Matches with equal total score are ordered by the most recently started. An integer
`insertionOrder` counter is used and directly models the insertion sequence, which is what the requirement actually describes

### MatchSummary DTO
`getSummary()` returns `List<MatchSummary>` rather than exposing internal `Match`
records. This decouples the public API from internal implementation details
(`insertionOrder` is an internal concern and not part of the contract).

### Exceptions
Three exception types are defined for error handling:
- `MatchAlreadyExistsException` - the exact same home/away pairing already exists
- `TeamAlreadyPlayingException` - one of the teams is busy in another match
- `MatchNotFoundException` - operation targets a non-existent match
- All exceptions extend `RuntimeException` for simplicity, as the library is designed for ease of use in a single-threaded context.

---

## Assumptions

- **Team names are case-insensitive** — `"Mexico"` and `"MEXICO"` are the same team.
- **A team can only play in one match at a time** — starting a match with an
  already-playing team throws `TeamAlreadyPlayingException`.
- **Scores are absolute** — `updateScore(2, 3)` sets the score to 2-3, it does
  not add to the existing score.
- **getSummary() returns a snapshot** — changes to the scoreboard after calling
  it do not affect the returned list.
- **Matches are ordered by total score, then by most recently started** — this is
  implemented using an `insertionOrder` counter that increments with each new match.

---

## Thread Safety

This implementation is not thread-safe by design. The library assumes single-threaded
usage, consistent with the "simple library" requirement. If concurrent access is
needed, external synchronization should be applied by the consumer.