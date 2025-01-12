import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);

    @Test
    void add() {
        String name = task.getName();
        String description = task.getDescription();
        int id = task.getId();
        TaskStatus status = task.getStatus();
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        String secondName = task.getName();
        String secondDescription = task.getDescription();
        int secondId = task.getId();
        TaskStatus secondStatus = task.getStatus();
        assertEquals(name, secondName, "Изменилось имя задачи");
        assertEquals(description, secondDescription, "Изменилось описание задачи");
        assertEquals(id, secondId, "Изменился айди задачи");
        assertEquals(status, secondStatus, "Изменился статус задачи");
    }

}