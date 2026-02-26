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

### Test Coverage
- **17 test methods** with comprehensive edge case coverage
- **26 total test assertions** across all test classes
- Tests cover: happy paths, error cases, case-insensitive matching, empty state, and snapshot immutability

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
    ├── model/
    │   ├── MatchIdTest.java                  # MatchId validation tests
    │   └── MatchTest.java                    # Match immutability & score tests
    ├── FootballWorldCupScoreboardTest.java   # Integration tests (17 test methods)
    └── TestConstants.java                    # Shared test constants (team names)

```

---

## Design Decisions

### Immutable Domain Model
`Match` and `MatchId` are Java records - immutable by design. Score updates
produce new `Match` instances via `withScore()`, eliminating mutation-related bugs
and making state changes explicit.

### Score Validation
Scores are validated at multiple points:
- During `Match` construction (prevents negative scores)
- During `Match.withScore()` (prevents negative scores in updates)
- Invalid scores throw `IllegalArgumentException` with descriptive messages

### MatchId Normalization
Team names are normalized to lowercase at `MatchId` construction time using
`Locale.ROOT`. This enforces case-insensitive equality at the domain level - no `equalsIgnoreCase` scattered across the codebase.

### Identity and Presentation
`MatchId` stores normalized names (`"mexico"`) for equality and map key purposes.
`Match` separately stores original display names (`"Mexico"`) so the public API
returns team names exactly as the caller provided them.

### Single Data Structure
`FootballWorldCupScoreboardImpl` uses a single `HashMap<MatchId, Match>` as its only
data structure, with an explicit `insertionOrderCounter` for reliable insertion tracking.

### Insertion Order (Explicit Counter)
Matches with equal total score are ordered by insertion sequence. An explicit integer
`insertionOrderCounter` (incremented with each new match) reliably tracks insertion order,
independent of map state. This ensures correct behavior even when matches are finished
and new ones are started.

### MatchSummary DTO
`getSummary()` returns `List<MatchSummary>` rather than exposing internal `Match`
records. This decouples the public API from internal implementation details
(`insertionOrder` is an internal concern and not part of the contract).
The returned list is immutable - changes to the scoreboard after calling `getSummary()`
do not affect previously returned snapshots.

### Exceptions
Three exception types are defined for error handling:
- `MatchAlreadyExistsException` - the exact same home/away pairing already exists
- `TeamAlreadyPlayingException` - one of the teams is busy in another match
- `MatchNotFoundException` - operation targets a non-existent match
- All exceptions extend `RuntimeException` for simplicity, as the library is designed for ease of use in a single-threaded context.

---

## Assumptions

- **Team names are case-insensitive** — `"Mexico"` and `"MEXICO"` are the same team.
  - Case-insensitive matching works across all operations: `startMatch()`, `updateScore()`, `finishMatch()`
- **A team can only play in one match at a time** — starting a match with an
  already-playing team throws `TeamAlreadyPlayingException`.
- **Scores are non-negative integers** — `updateScore(2, 3)` sets the score to 2-3.
  - Negative scores throw `IllegalArgumentException`
  - Scores are validated at both construction and update time
  - Supports any valid integer value (0 to `Integer.MAX_VALUE`)
- **Scores are absolute, not relative** — `updateScore(2, 3)` sets the score to 2-3, it does
  not add to the existing score.
- **getSummary() returns an immutable snapshot** — changes to the scoreboard after calling
  it do not affect the returned list. Each call returns a new list reflecting current state.
- **Matches are ordered by total score, then by most recently started** — this is
  implemented using an explicit `insertionOrderCounter` that increments with each new match.

---

## Test Coverage

The project includes comprehensive test coverage for all functionality and edge cases:

### Test Classes
- **FootballWorldCupScoreboardTest.java** (17 test methods)
  - Happy path: match creation, score updates, match finish
  - Error cases: negative scores, non-existent matches, duplicate matches
  - Case-insensitive matching: all operations tested with mixed-case team names
  - Edge cases: empty scoreboard, snapshot immutability, insertion order with ties
  
- **MatchIdTest.java** (4 test methods)
  - Validation of team names (non-null, non-blank)
  - Case normalization with Locale.ROOT
  - Prevention of same-team matches

- **MatchTest.java** (6 test methods)
  - Record creation and immutability
  - Score validation (negative scores rejected)
  - Score updates via `withScore()`
  - Total score calculation

### Key Test Scenarios
- ✅ Negative score rejection in both constructor and updates
- ✅ Case-insensitive team matching across all operations
- ✅ Empty scoreboard handling
- ✅ Snapshot immutability (getSummary returns independent copy)
- ✅ Insertion order with tied total scores
- ✅ Duplicate match prevention
- ✅ Team already playing prevention

---

## Thread Safety

This implementation is not thread-safe by design. The library assumes single-threaded
usage, consistent with the "simple library" requirement. If concurrent access is
needed, external synchronization should be applied by the consumer.