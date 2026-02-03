# TRON Game - MVC Architecture Refactoring

> A classic TRON-style game refactored with MVC architecture, design patterns, and comprehensive test coverage.

## Project Overview

This project refactored a monolithic TRON-style snake game (Gamma & Gamma, 2012) into a maintainable JavaFX application following MVC architecture and SOLID principles, implementing five features, five design patterns, and 493 test cases.

## Major Refactoring Activities

### MVC Architecture Implementation

Restructured into Model-View-Controller: `TronGameModel` manages state, JavaFX views (`FXTronGameView`, `FXBossBattleGameView`, `FXStoryGameView`) handle rendering, `FXGameController` coordinates interactions.

### JavaFX Migration

Migrated from Swing to JavaFX using `Canvas` for rendering, FXML for layouts (`MainMenu.fxml`, `PlayMenu.fxml`, `OptionsMenu.fxml`), and `Stage` dialogs. `FXGameController` singleton ensures thread-safe operations via `Platform.runLater()`.

### High Score System Modernization

Refactored using Gson (2.10.1) for JSON serialization. `Score` stores `HighScoreEntry` objects with score, nickname, gender, manifesto, timestamp. `LocalDateAdapter` handles `LocalDate` serialization with automatic migration from `HighScores.txt` to `HighScores.json`.

## Additional Game Features

### Feature 1: Boss Battle Game Mode

Single-player challenge with Boss (10 HP) in split-screen layout (600px player + 300px Boss). Star power-ups spawn every 5 seconds, dealing 2 HP damage. Victory at Boss HP = 0; defeat on collision.

**Implementation:**
- `com.tron.model.boss.Boss`: `takeDamage()` reduces health, `getHealthPercentage()` calculates ratio, `isAlive()` checks survival
- `com.tron.model.boss.BossBattleGameModel`: `tick()` orchestrates loop, `checkPowerUpCollisions()` detects collisions
- `com.tron.model.boss.BossBattlePowerUpManager`: `update(double deltaTime)` manages spawn timer, `spawnPowerUp()` generates items
- `com.tron.view.fx.FXBossBattleView`: `drawBossArea()` renders health bar, `drawPowerUps()` displays stars

### Feature 2: Advanced Audio Management System

Audio system provides sound feedback and continuous BGM using JavaFX Media API. `AudioManager` singleton manages seven resources: BGM (looping), CLICK, WIN, LOSE, PAUSE, UNPAUSE, PICKUP.

**Implementation:**
- `com.tron.audio.AudioManager`: `playSoundEffect(SoundEffect effect)` for sounds, `startBGM()` initiates loop, `pauseBGM()` preserves position, `stopBGM()` resets
- `com.tron.view.fx.FXStoryGameView`: `checkLevelComplete()` plays WIN/LOSE, pauses BGM on game over
- `com.tron.controller.fx.FXGameInputController`: `handlePauseRequest()` plays PAUSE/UNPAUSE synchronized with BGM
- Menu views trigger CLICK on buttons

### Feature 3: Map System with Wrap-Around Boundaries

Configurable Survival mode environments with wrap-around boundaries (Snake style) and obstacles. `MapType` defines: DEFAULT (collision walls), MAP_1 (wrap-around), MAP_2 (wrap + cross maze), MAP_3 (wrap + inset walls).

**Implementation:**
- `com.tron.model.util.MapType`: `hasWrapAroundBoundaries()` determines behavior
- `com.tron.model.util.MapConfig`: `createMap(MapType type)` creates layouts, `checkObstacleCollision(int x, int y)` validates positions
- `com.tron.model.util.MapObstacle`: `intersects(int x, int y)` point collision, `intersects(Line line)` segment detection
- `com.tron.model.game.Player`: `accelerate()` implements wrap or collision death
- `com.tron.view.fx.FXOptionsMenuView`: `initializeGameplayControls()` provides `ChoiceBox` for selection

### Feature 4: Enhanced High Score System with Player Information

For top-10 Survival scores, `FXPlayerInfoDialog` collects nickname (3-20 chars), gender, manifesto (3-20 chars) with validation. Data persists in JSON with legacy migration.

**Implementation:**
- `com.tron.model.score.HighScoreEntry`: Model with score, nickname, gender, manifesto, `LocalDate`
- `com.tron.model.score.Score`: `addHighScore(HighScoreEntry entry)` maintains top-10, `loadHighScoresFromJson()` with `migrateFromLegacyFormat()`
- `com.tron.view.fx.FXPlayerInfoDialog`: `show(int score, Consumer<HighScoreEntry> onSubmit)`, `validateNickname()`, `validateManifesto()` enforce format
- `com.tron.model.game.SurvivalGameModel`: `saveScore()` checks `Score.qualifiesForHighScore(int)`, displays dialog via `Platform.runLater()`
- `com.tron.view.fx.menu.FXPlayMenuView`: `populateHighScores()` renders leaderboard

### Feature 5: Power-Up System for Story Mode

Collectible stars grant boost charges in Story Mode. White stars (10x10px PNG) spawn every 5 seconds. Players and AI collect via collision, gaining +1 boost.

**Implementation:**
- `com.tron.model.powerup.PowerUpType`: Enum with BOOST (active), BOSS_DAMAGE (reserved)
- `com.tron.model.powerup.PowerUp`: Entity with circular collision (5px), position, state
- `com.tron.model.powerup.PowerUpManager`: `update(double deltaTime)` manages spawns, `checkCollision(int x, int y)` detects collection, `spawnPowerUp()` creates instances
- `com.tron.model.game.StoryGameModel`: Integrates updates, collision, `applyPowerUpEffect(Player player, PowerUp powerUp)`
- `com.tron.view.fx.FXTronGameView`: Renders cached `powerup_star.png`

## Design Patterns Applied

### Pattern 1: Observer Pattern (Custom Implementation)

**Justification:** Eliminates coupling between models and views, enabling automatic UI updates without direct references. Custom implementation (not JavaFX listeners or Java's deprecated `Observable`) achieves separation, testability, and MVC adherence.

**Implementation:**
- `com.tron.model.observer.Subject<T>`: Interface with `attach(T observer)`, `detach(T observer)`, `notifyObservers()`
- `com.tron.model.observer.GameStateObserver`: Interface with `onGameStateChanged()`, `onPlayerCrashed(int playerIndex)`, `onGameReset()`, `onBoostChanged(int playerIndex, int boostCount)`
- `com.tron.model.game.TronGameModel`: Implements `Subject<GameStateObserver>`, maintains observers, calls `notifyGameStateChanged()`
- `com.tron.view.fx.FXBossBattleView`: Implements `GameStateObserver`, `onGameStateChanged()` triggers `draw()`

### Pattern 2: Factory Method Pattern

**Justification:** Decouples mode selection from model instantiation. `FXGameController` creates models through unified interfaces without knowing concrete classes. Improves extensibility (new modes need only new factories) and reduces coupling.

**Implementation:**
- `com.tron.model.game.factory.GameModelFactory`: Abstract factory with `createGameModel()`, `initializeGame()` template
- `com.tron.model.game.factory.StoryGameModelFactory`: `createGameModel()` returns `StoryGameModel`
- `com.tron.model.game.factory.SurvivalGameModelFactory`: Returns `SurvivalGameModel`
- `com.tron.model.game.factory.BossBattleGameModelFactory`: Returns `BossBattleGameModel`
- `com.tron.controller.fx.FXGameController`: Uses `viewFactory.createTwoPlayerView(gameModel)`

### Pattern 3: Strategy Pattern

**Justification:** Enables runtime swapping of player behaviors (AI vs Human) without modifying `Player` internals. Addresses duplication, improves testability, facilitates extension.

**Implementation:**
- `com.tron.model.game.PlayerBehaviorStrategy`: Interface defining `decideMoveDirection()`, `shouldBoost()`, `reset()`, `getVelocity()`
- `com.tron.model.game.AIBehaviorStrategy`: Implements collision avoidance, obstacle detection; `decideMoveDirection()` uses raycasting
- `com.tron.model.game.HumanBehaviorStrategy`: `handleInput(GameInput input)` processes keyboard
- `com.tron.model.game.Player`: `setBehaviorStrategy(PlayerBehaviorStrategy strategy)` enables switching, delegates calls

### Pattern 4: Singleton Pattern

**Justification:** Ensures critical components exist as single instances. Prevents state conflicts, resource duplication, simplifies dependency management through `getInstance()`.

**Implementation:**
- `com.tron.controller.fx.FXGameController`: `private static instance`, `private constructor`, `public static synchronized getInstance()` with lazy initialization
- `com.tron.model.score.Score`: `private static instance`, `private Score(String fileName)`, `public static synchronized getInstance(String fileName)` ensures single manager
- `com.tron.audio.AudioManager`: `private static instance`, `private constructor`, `public static synchronized getInstance()`
- Thread-safe via synchronized methods

### Pattern 5: Decorator Pattern

**Justification:** Adds cross-cutting concerns (logging, monitoring) to strategies dynamically without modifying base classes. Solves class explosion, adheres to Open/Closed Principle, maintains Single Responsibility.

**Implementation:**
- `com.tron.model.game.decorator.BehaviorStrategyDecorator`: Abstract decorator implementing `PlayerBehaviorStrategy`, wraps `decoratedStrategy`, delegates methods
- `com.tron.model.game.decorator.LoggingBehaviorDecorator`: Extends `BehaviorStrategyDecorator`, overrides `decideMoveDirection()` incrementing `moveDecisionCount`, `shouldBoost()` increments `boostDecisionCount`, provides `getMoveDecisionCount()`
- Stackable: `new LoggingBehaviorDecorator(new AIBehaviorStrategy(player))` adds telemetry
- `getDecoratedStrategy()` enables introspection

## Software Testing Summary

### High-Level Test Plan

**Objectives:** Validate refactoring correctness, ensure pattern implementations satisfy contracts, verify feature functionality across all game modes, and maintain regression protection through comprehensive test coverage.

**Testing Types:**
- **Unit Testing (360 cases):** Isolated validation of individual classes and methods using JUnit 5.10.1 and Mockito 5.8.0
- **Integration Testing (114 cases):** Cross-component interaction verification focusing on pattern implementations and observer notification flows
- **System Testing (19 cases):** End-to-end gameplay scenario validation including full story mode progression, survival mode completion, and two-player matches

**Testing Environment:** Maven 3.x build system, JDK 21, JavaFX 21.0.5, JUnit 5.10.1, Mockito 5.8.0. JavaFX tests execute headless via `Platform.startup()` and `Platform.runLater()`. Latest execution: **493 tests in with 6 failures, 461 passed, 23 skipped** .
- P.S. Some tests can pass when run individually, but fail when run all the tests one-off. All skipped and failed tests belong to this category.

### Representative Test Cases

| Test Class & Method | Class & Method Under Test | Test Inputs / Preconditions | Expected Outcome / Postconditions | Actual Outcome / Postconditions | Testing Framework |
|---------------------|---------------------------|----------------------------|----------------------------------|--------------------------------|-------------------|
| `ScoreSingletonTest#testThreadSafety` | `Score.getInstance()` | 20-thread pool requests singleton instance, results collected in `ConcurrentHashMap<Score, Boolean>` | Single instance returned to all threads, map size = 1, executor completes within timeout | Executor completed successfully, map contained exactly 1 element, all threads received identical instance reference | JUnit 5 |
| `GameModelFactoryTest#testStoryFactoryCreateGameModel` | `StoryGameModelFactory.createGameModel()` | Factory invoked via abstract base `GameModelFactory` | Returns fully initialized `StoryGameModel` with players, level metadata, and observer list | Instance type verified as `StoryGameModel`, player count = 2, level counter initialized to 1 | JUnit 5 |
| `PlayerDecoratorIntegrationTest#testDecoratorWithHumanPlayer` | `LoggingBehaviorDecorator.decideMoveDirection()` | Human player strategy wrapped with logging decorator, movement command issued | Movement executes correctly, decorator counters increment, base semantics unchanged | Player moved in requested direction, `getMoveDecisionCount()` increased by 1, no side effects on base strategy | JUnit 5 + Mockito |
| `TwoPlayerGameModelTest#testTwoPlayerCollisionDetection` | `TwoPlayerGameModel.tick()` | Two players positioned on collision course (same coordinates next tick) | Collision detected, game-over state triggered, both players marked dead | Collision detected at tick N, `isGameOver()` returns true, `player1.isAlive()` and `player2.isAlive()` both false | JUnit 5 |
| `SurvivalGameModelTest#testHighScoreIntegration` | `SurvivalGameModel.saveScore()` & `Score.addHighScore(HighScoreEntry)` | Survival run completes with score 5000, high-score file mocked, `qualifiesForHighScore()` returns true | Score entry added to top-10 list, `HighScores.json` updated with new entry | High score list size increased by 1, JSON file contains new entry with correct score value, observers notified | JUnit 5 |
| `StoryGameModelTest#testLevelProgression` | `StoryGameModel.nextLevel()` | Level 1 cleared (all AI defeated), observers registered via `attach()` | Level counter increments to 2, new AI roster spawns, observers receive `onGameReset()` notification | Level counter = 2, AI player count matches level 2 configuration, all observers invoked exactly once | JUnit 5 |
| `PlayerCollisionBoundaryTest#testPlayerCollisionAtBoundary` | `Player.checkCollision(List<GameObject>)` | Player positioned at x=0 (left boundary), velocity set to move left | Boundary collision detected, player marked dead, collision event triggered | `isAlive()` returns false, `PlayerObserver.onPlayerDied()` invoked with correct player reference | JUnit 5 |
| `FXGameControllerBehaviorTest#testGameInitialization` | `FXGameController.startStoryMode()` | Story mode selected from play menu | Controller initializes `StoryGameModel`, view created, observers registered, BGM started | Game model instance created, `FXStoryGameView` attached to scene, `GameStateObserver` list non-empty, audio system confirms BGM playing | JUnit 5 + Mockito |
| `AIBehaviorStrategyTest#testAvoidObstacleDecisionMaking` | `AIBehaviorStrategy.decideMoveDirection()` | AI player facing obstacle in current direction, `TronGameModel` populated with obstacle ahead | AI selects alternative safe direction (left or right) | Direction changed to perpendicular direction (not forward), velocity updated accordingly, no collision on next tick | JUnit 5 + Mockito |
| `FXTwoPlayerGameViewTest#testPlayerRenderingUpdate` | `FXTwoPlayerGameView.draw()` | Player position changed from (100,100) to (105,100) in model, `notifyObservers()` called | View updates canvas to reflect new player position | JavaFX canvas `GraphicsContext` received `fillRect()` call with coordinates (105,100), previous position cleared | JUnit 5 + JavaFX Platform |
| `TronApplicationTest#testStartMethodInitialization` | `TronApplication.start(Stage)` | JavaFX Platform running, mock `Stage` object provided | Controller initialized, stage title set containing "TRON", stage shown successfully | `FXGameController.getInstance()` returns non-null, stage title verified containing "TRON", test completed within 5s timeout | JUnit 5 + JavaFX Platform |
| `EndToEndIntegrationTest#testStoryModeInitializationWorkflow` | `FXGameController.startStoryMode()` & `StoryGameModel` initialization | Fresh controller state, JavaFX Platform initialized, test `Stage` created | Story game model created with 2 players (1 human + 1 AI), level counter = 1, game running state = true | Game model non-null, player array length = 2, `getCurrentLevel()` = 1, `isRunning()` = true, completed within 10s timeout | JUnit 5 + JavaFX Platform |

## References

Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (2012). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley Professional.

## Project Repository

All source code, test suites, and documentation are available in the project repository. Build instructions: `mvn clean install`, run application: `mvn javafx:run`.
