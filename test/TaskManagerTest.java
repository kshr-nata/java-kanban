import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    @Test
    void shouldNotChangeId() {
        Task task = new Task("Test first task", "Test first task description", TaskStatus.NEW, 1, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        task.setId(50);
        assertEquals(1, task.getId(), "Айди задачи изменился!");
    }
}
