package com.kidbank;

import com.kidbank.model.SavingsGoal;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD unit tests for the {@link SavingsGoal} class.
 */
@DisplayName("SavingsGoal Tests")
class SavingsGoalTest {

    private SavingsGoal goal;

    @BeforeEach
    void setUp() {
        goal = new SavingsGoal("g1", "Bicycle", 100.0, "tommy");
    }

    @Test
    @DisplayName("New goal starts at 0% progress")
    void testInitialProgress() {
        assertEquals(0.0, goal.getProgressPercent(), 0.001);
        assertFalse(goal.isCompleted());
    }

    @Test
    @DisplayName("Contribution updates saved amount")
    void testContribute() {
        goal.contribute(30.0);
        assertEquals(30.0, goal.getSavedAmount(), 0.001);
        assertEquals(30.0, goal.getProgressPercent(), 0.001);
    }

    @Test
    @DisplayName("Contribution beyond target caps at target")
    void testContributeOverTarget() {
        goal.contribute(150.0);
        assertEquals(100.0, goal.getSavedAmount(), 0.001);
        assertEquals(100.0, goal.getProgressPercent(), 0.001);
        assertTrue(goal.isCompleted());
    }

    @Test
    @DisplayName("Goal marked complete when target reached exactly")
    void testCompletedAtExactTarget() {
        goal.contribute(100.0);
        assertTrue(goal.isCompleted());
    }

    @Test
    @DisplayName("Remaining decreases as money is saved")
    void testRemaining() {
        goal.contribute(40.0);
        assertEquals(60.0, goal.getRemaining(), 0.001);
    }

    @Test
    @DisplayName("Zero contribution throws exception")
    void testZeroContributionThrows() {
        assertThrows(IllegalArgumentException.class, () -> goal.contribute(0));
    }

    @Test
    @DisplayName("Negative target throws exception")
    void testNegativeTargetThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new SavingsGoal("g2", "Test", -10.0, "user"));
    }

    @Test
    @DisplayName("JSON round-trip preserves all fields")
    void testJsonRoundTrip() {
        goal.contribute(25.0);
        SavingsGoal restored = SavingsGoal.fromJson(goal.toJson());
        assertEquals(goal.getGoalId(),       restored.getGoalId());
        assertEquals(goal.getName(),          restored.getName());
        assertEquals(goal.getTargetAmount(), restored.getTargetAmount(), 0.001);
        assertEquals(goal.getSavedAmount(),  restored.getSavedAmount(), 0.001);
        assertEquals(goal.isCompleted(),     restored.isCompleted());
    }
}
