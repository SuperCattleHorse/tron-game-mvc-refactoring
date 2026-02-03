package com.tron.model.score;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the {@link Score} Singleton Pattern implementation.
 * 
 * <p>
 * This test class validates the Singleton Pattern applied to Score,
 * ensuring that only one instance of Score exists throughout the
 * application lifecycle, providing a single source of truth for high scores.
 * </p>
 * 
 * <p>
 * Tests cover:
 * </p>
 * <ul>
 * <li>Instance Uniqueness - Verify only one instance is created</li>
 * <li>Thread Safety - Verify thread-safe access in multi-threaded
 * environments</li>
 * <li>Lazy Initialization - Verify instance is created on first access</li>
 * <li>State Consistency - Verify all references access the same state</li>
 * <li>Instance Behavior - Verify singleton behavior with different
 * constructors</li>
 * </ul>
 * 
 * @author MattBrown
 * @author MattBrown
 * @version 1.0
 * @see Score
 */
@DisplayName("Score Singleton Pattern Tests")
public class ScoreSingletonTest {

    @TempDir
    Path tempDir;

    /**
     * Nested test class for basic Singleton behavior tests.
     */
    @Nested
    @DisplayName("Basic Singleton Behavior Tests")
    class BasicSingletonTests {

        /**
         * Test Case: ScoreSingletonTest.testInstanceUniqueness()
         * 
         * Tests that multiple calls to getInstance() return the same instance object.
         * 
         * Class and Method under test: Score.getInstance()
         * Test Inputs/Preconditions: None
         * Expected Outcome: Two variables assigned from getInstance() should reference
         * the same object (identity equality)
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testInstanceUniqueness - Multiple getInstance() calls return same instance")
        void testInstanceUniqueness() {
            // Arrange & Act
            Score instance1 = Score.getInstance();
            Score instance2 = Score.getInstance();
            Score instance3 = Score.getInstance();

            // Assert
            assertNotNull(instance1, "First instance should not be null");
            assertNotNull(instance2, "Second instance should not be null");
            assertNotNull(instance3, "Third instance should not be null");
            assertSame(instance1, instance2, "First and second instances should be identical");
            assertSame(instance2, instance3, "Second and third instances should be identical");
            assertSame(instance1, instance3, "First and third instances should be identical");
        }

        /**
         * Test Case: ScoreSingletonTest.testInstanceWithFilename()
         * 
         * Tests that getInstance(String filename) with parameters returns same instance
         * if already created, ignoring the filename parameter.
         * 
         * Class and Method under test: Score.getInstance(String filename)
         * Test Inputs/Preconditions: First call without parameter, second with
         * "test.txt"
         * Expected Outcome: Both calls return the same instance (singleton behavior
         * maintained)
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testInstanceWithFilename - getInstance(filename) returns existing instance")
        void testInstanceWithFilename() throws Exception {
            // Arrange - Create temporary file
            Path testFile = tempDir.resolve("TestScores.txt");
            Files.createFile(testFile);

            // Act - Get instances
            Score instance1 = Score.getInstance();
            Score instance2 = Score.getInstance(testFile.toString());

            // Assert - Both should be the same instance
            assertNotNull(instance1, "First instance should not be null");
            assertNotNull(instance2, "Second instance should not be null");
            assertSame(instance1, instance2,
                    "getInstance() and getInstance(filename) should return same instance");
        }

    }

    /**
     * Nested test class for thread safety tests.
     */
    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        /**
         * Test Case: ScoreSingletonTest.testThreadSafetyWithFilename()
         * 
         * Tests that getInstance(String filename) is thread-safe when called
         * simultaneously from multiple threads with different filenames.
         * 
         * Class and Method under test: Score.getInstance(String filename)
         * Test Inputs/Preconditions: 15 threads with different filename parameters
         * Expected Outcome: All threads receive the same singleton instance
         * (filename parameter is ignored after first instantiation)
         * Testing Framework: JUnit 5, ExecutorService
         */
        @Test
        @DisplayName("testThreadSafetyWithFilename - Multiple threads with filename get same instance")
        void testThreadSafetyWithFilename() throws InterruptedException, Exception {
            // Arrange
            Set<Score> instances = new HashSet<>();
            ExecutorService executor = Executors.newFixedThreadPool(15);
            int threadCount = 15;

            // Create temporary test files
            for (int i = 0; i < threadCount; i++) {
                Path testFile = tempDir.resolve("TestScores" + i + ".txt");
                Files.createFile(testFile);
            }

            // Act
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.execute(() -> {
                    String filename = "TestScores" + threadId + ".txt";
                    instances.add(Score.getInstance(filename));
                });
            }

            executor.shutdown();
            boolean completed = executor.awaitTermination(10, TimeUnit.SECONDS);

            // Assert
            assertTrue(completed, "All threads should complete");
            assertEquals(1, instances.size(),
                    "All threads should receive the same singleton instance");
        }

        /**
         * Test Case: ScoreSingletonTest.testHighConcurrencyThreadSafety()
         * 
         * Tests thread safety with high concurrency (50 threads).
         * 
         * Class and Method under test: Score.getInstance()
         * Test Inputs/Preconditions: 50 concurrent threads attempting to get singleton
         * instance
         * Expected Outcome: All threads receive the same instance; no race conditions
         * occur
         * Testing Framework: JUnit 5, ExecutorService with high thread count
         */
        @Test
        @DisplayName("testHighConcurrencyThreadSafety - 50 concurrent threads get same instance")
        void testHighConcurrencyThreadSafety() throws InterruptedException {
            // Arrange
            Set<Score> instances = new HashSet<>();
            ExecutorService executor = Executors.newFixedThreadPool(50);
            int threadCount = 50;
            AtomicInteger successCount = new AtomicInteger(0);

            // Act
            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    Score instance = Score.getInstance();
                    if (instance != null) {
                        instances.add(instance);
                        successCount.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            boolean completed = executor.awaitTermination(15, TimeUnit.SECONDS);

            // Assert
            assertTrue(completed, "All 50 threads should complete");
            assertEquals(threadCount, successCount.get(), "All threads should successfully get instance");
            assertEquals(1, instances.size(), "All threads should get the same singleton instance");
        }

        /**
         * Test Case: ScoreSingletonTest.testConcurrentAccessConsistency()
         * 
         * Tests that concurrent access to getInstance() produces consistent results.
         * 
         * Class and Method under test: Score.getInstance()
         * Test Inputs/Preconditions: Multiple rapid successive calls from different
         * threads
         * Expected Outcome: All instances are identical; no instance creation conflicts
         * Testing Framework: JUnit 5 with ExecutorService
         */
        @Test
        @DisplayName("testConcurrentAccessConsistency - Rapid concurrent access remains consistent")
        void testConcurrentAccessConsistency() throws InterruptedException {
            // Arrange
            Set<Integer> hashCodes = new HashSet<>();
            ExecutorService executor = Executors.newFixedThreadPool(10);

            // Act
            for (int i = 0; i < 100; i++) {
                executor.execute(() -> {
                    Score instance = Score.getInstance();
                    hashCodes.add(System.identityHashCode(instance));
                });
            }

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

            // Assert
            assertEquals(1, hashCodes.size(),
                    "All concurrent accesses should return instance with same hash code");
        }
    }

    /**
     * Nested test class for state consistency tests.
     */
    @Nested
    @DisplayName("State Consistency Tests")
    class StateConsistencyTests {
        /**
         * Test Case: ScoreSingletonTest.testStateConsistencyAcrossReferences()
         * 
         * Tests that state modifications through one reference are visible
         * through another reference to the same singleton.
         * 
         * Class and Method under test: Score.getInstance(), Score.getHighScores()
         * Test Inputs/Preconditions: Multiple references to same singleton instance
         * Expected Outcome: Both references point to same list, see same data
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testStateConsistencyAcrossReferences - State changes visible across references")
        void testStateConsistencyAcrossReferences() throws Exception {
            // Arrange
            Score scoreRef1 = Score.getInstance();
            Score scoreRef2 = Score.getInstance();

            // Act - Add score through scoreRef1
            scoreRef1.addHighScore(9999);
            int updatedSize = scoreRef1.getHighScores().size();

            // Assert - Should see same list state through scoreRef2
            int ref2Size = scoreRef2.getHighScores().size();
            assertEquals(updatedSize, ref2Size,
                    "Both references should see the same list size after modification");
            assertTrue(scoreRef2.getHighScores().contains(9999),
                    "Score added through ref1 should be visible in ref2");
        }

        /**
         * Test Case: ScoreSingletonTest.testSingleSourceOfTruth()
         * 
         * Tests that all references access the same underlying data.
         * 
         * Class and Method under test: Score.getInstance(), Score.getHighScores()
         * Test Inputs/Preconditions: Multiple references to same singleton
         * Expected Outcome: All references see identical list contents and
         * modifications
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testSingleSourceOfTruth - All references access same data")
        void testSingleSourceOfTruth() throws Exception {
            // Arrange
            Score scoreA = Score.getInstance();
            Score scoreB = Score.getInstance();
            Score scoreC = Score.getInstance();

            // Act - Add a very high score through scoreA (will be in top 10)
            scoreA.addHighScore(99999);

            // Assert - All references should see the same modified state
            List<Integer> listA = scoreA.getHighScores();
            List<Integer> listB = scoreB.getHighScores();
            List<Integer> listC = scoreC.getHighScores();

            assertEquals(listA.size(), listB.size(), "scoreA and scoreB should have same list size");
            assertEquals(listB.size(), listC.size(), "scoreB and scoreC should have same list size");

            assertTrue(listB.contains(99999), "scoreB should see the score added through scoreA");
            assertTrue(listC.contains(99999), "scoreC should see the score added through scoreA");

            // Verify all references point to same underlying list
            assertEquals(listA, listB, "scoreA and scoreB should reference the same list");
            assertEquals(listB, listC, "scoreB and scoreC should reference the same list");
        }
    }

    /**
     * Nested test class for lazy initialization tests.
     */
    @Nested
    @DisplayName("Lazy Initialization Tests")
    class LazyInitializationTests {

        /**
         * Test Case: ScoreSingletonTest.testLazyInitialization()
         * 
         * Tests that the singleton instance is created only when getInstance() is first
         * called.
         * 
         * Class and Method under test: Score.getInstance()
         * Test Inputs/Preconditions: Fresh test execution
         * Expected Outcome: First getInstance() call creates instance; subsequent calls
         * return it
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testLazyInitialization - Instance created on first getInstance() call")
        void testLazyInitialization() {
            // Arrange - Get first instance
            Score firstInstance = Score.getInstance();

            // Assert - Instance should not be null (created on demand)
            assertNotNull(firstInstance, "First getInstance() call should create instance (lazy init)");

            // Act - Get second instance
            Score secondInstance = Score.getInstance();

            // Assert - Second instance should be identical
            assertSame(firstInstance, secondInstance,
                    "Second getInstance() call should return existing instance");
        }
    }

    /**
     * Nested test class for edge case tests.
     */
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        /**
         * Test Case: ScoreSingletonTest.testSequentialAccess()
         * 
         * Tests that sequential rapid calls to getInstance() return same instance.
         * 
         * Class and Method under test: Score.getInstance()
         * Test Inputs/Preconditions: Rapid sequential calls (100 times)
         * Expected Outcome: All 100 calls return the same instance object
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testSequentialAccess - 100 sequential calls return same instance")
        void testSequentialAccess() {
            // Arrange
            Score firstInstance = Score.getInstance();
            Set<Score> uniqueInstances = new HashSet<>();

            // Act
            for (int i = 0; i < 100; i++) {
                uniqueInstances.add(Score.getInstance());
            }

            // Assert
            assertEquals(1, uniqueInstances.size(),
                    "100 sequential calls should return exactly 1 unique instance");
            assertTrue(uniqueInstances.contains(firstInstance),
                    "All calls should return the same instance");
        }

        /**
         * Test Case: ScoreSingletonTest.testMultipleReferencesPointToSameObject()
         * 
         * Tests that multiple variable assignments point to the same object.
         * 
         * Class and Method under test: Score.getInstance()
         * Test Inputs/Preconditions: 5 separate variable assignments from getInstance()
         * Expected Outcome: All variables reference identical object in memory
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testMultipleReferencesPointToSameObject - All references point to same object")
        void testMultipleReferencesPointToSameObject() {
            // Arrange & Act
            Score ref1 = Score.getInstance();
            Score ref2 = Score.getInstance();
            Score ref3 = Score.getInstance();
            Score ref4 = Score.getInstance();
            Score ref5 = Score.getInstance();

            // Assert
            assertSame(ref1, ref2, "ref1 and ref2 should be identical");
            assertSame(ref2, ref3, "ref2 and ref3 should be identical");
            assertSame(ref3, ref4, "ref3 and ref4 should be identical");
            assertSame(ref4, ref5, "ref4 and ref5 should be identical");
        }

        /**
         * Test Case: ScoreSingletonTest.testInstanceIsNotNull()
         * 
         * Tests that getInstance() consistently returns non-null objects.
         * 
         * Class and Method under test: Score.getInstance()
         * Test Inputs/Preconditions: Call getInstance() multiple times
         * Expected Outcome: Every call returns non-null instance
         * Testing Framework: JUnit 5
         */
        @Test
        @DisplayName("testInstanceIsNotNull - Every getInstance() call returns non-null")
        void testInstanceIsNotNull() {
            // Act & Assert
            for (int i = 0; i < 50; i++) {
                Score instance = Score.getInstance();
                assertNotNull(instance, "getInstance() call " + i + " should not return null");
            }
        }
    }
}
