package com.kidbank;

import com.kidbank.model.Task;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD unit tests for the {@link Task} model and its state machine.
 */
@DisplayName("Task Tests")
class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("t1", "Clean bedroom", "Tidy up", 2.50, "parent1", "child1");
    }

    @Test
    @DisplayName("New task starts PENDING")
    void testInitialStatus() {
        assertEquals(Task.Status.PENDING, task.getStatus());
    }

    @Test
    @DisplayName("Child can mark PENDING task as complete")
    void testMarkComplete() {
        task.markCompletedByChild();
        assertEquals(Task.Status.COMPLETED_BY_CHILD, task.getStatus());
    }

    @Test
    @DisplayName("Parent can approve a COMPLETED_BY_CHILD task")
    void testApprove() {
        task.markCompletedByChild();
        task.approve();
        assertEquals(Task.Status.APPROVED, task.getStatus());
    }

    @Test
    @DisplayName("Parent can reject a COMPLETED_BY_CHILD task → returns to PENDING")
    void testReject() {
        task.markCompletedByChild();
        task.reject();
        assertEquals(Task.Status.PENDING, task.getStatus());
    }

    @Test
    @DisplayName("Cannot mark an already-completed task as complete again")
    void testDoubleCompleteThrows() {
        task.markCompletedByChild();
        assertThrows(IllegalStateException.class, task::markCompletedByChild);
    }

    @Test
    @DisplayName("Cannot approve a PENDING task directly")
    void testApproveFromPendingThrows() {
        assertThrows(IllegalStateException.class, task::approve);
    }

    @Test
    @DisplayName("Cannot reject a PENDING task")
    void testRejectFromPendingThrows() {
        assertThrows(IllegalStateException.class, task::reject);
    }

    @Test
    @DisplayName("Negative reward throws exception")
    void testNegativeRewardThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new Task("t2", "Bad task", "", -1.0, "p", "c"));
    }

    @Test
    @DisplayName("JSON round-trip preserves state")
    void testJsonRoundTrip() {
        task.markCompletedByChild();
        Task restored = Task.fromJson(task.toJson());
        assertEquals(task.getTaskId(),      restored.getTaskId());
        assertEquals(task.getTitle(),        restored.getTitle());
        assertEquals(task.getStatus(),       restored.getStatus());
        assertEquals(task.getRewardAmount(), restored.getRewardAmount(), 0.001);
    }
}
