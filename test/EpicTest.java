import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);
        TaskStatus status = epic.getStatus();

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
        assertEquals("Test addNewEpic", epic.getName(), "Имя эпика изменилось.");
        assertEquals("Test addNewEpic description", epic.getDescription(), "Описание эпика изменилось.");
        assertEquals(status, epic.getStatus(), "Статус эпика изменился.");

    }

    @Test
    void shouldRemoveEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        taskManager.removeEpic(epicId);
        assertEquals(0, taskManager.getAllEpics().size(),"Эпик не удаляется");

    }

    @Test
    void shouldClearEpics() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        taskManager.clearEpics();
        assertEquals(0, taskManager.getAllEpics().size(),"Эпики не очищаются");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test", "Test description");
        final int epicId = taskManager.makeNewEpic(epic);
        Epic secondEpic = new Epic("Test updateEpic", "Test updateEpic description", epicId);
        taskManager.updateEpic(secondEpic);
        assertEquals("Test updateEpic", epic.getName(), "Имя эпика не изменилось.");
        assertEquals("Test updateEpic description", epic.getDescription(), "Описание эпика не изменилось.");
    }

    @Test
    void shouldChangeEpicStatus() {
        Epic epic = new Epic("Test", "Test description");
        final int epicId = taskManager.makeNewEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "У эпика без сабтасков статус не равен NEW");
        Subtask subtask = new Subtask("Test", "Test description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "У эпика статус не равен NEW");

        Subtask updatedSubtask = new Subtask("Test", "Test description", TaskStatus.DONE, epic, subtaskId, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "У эпика статус не равен DONE");

        Subtask secondSubtask = new Subtask("Test", "Test description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,16,30), Duration.ofMinutes(30));
        final int secondSubtaskId = taskManager.makeNewSubtask(secondSubtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "У эпика статус не равен IN_PROGRESS");

        Subtask updatedSubtask2 = new Subtask("Test", "Test description", TaskStatus.IN_PROGRESS, epic, subtaskId, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask);
        Subtask updatedSecondSubtask = new Subtask("Test", "Test description", TaskStatus.IN_PROGRESS, epic, secondSubtaskId, LocalDateTime.of(2025, 1,20,16,30), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSecondSubtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "У эпика статус не равен IN_PROGRESS");
    }

    @Test
    void epicShouldEqualsEpicWithEqualsId() {
        Epic firstEpic = new Epic("Test first epic", "Test first epic description");
        firstEpic.setId(1);
        Epic secondEpic = new Epic("Test second epic", "Test second epic description");
        secondEpic.setId(1);
        assertEquals(firstEpic, secondEpic, "Эпики с одинаковым айди не равны.");
    }

    @Test
    void shouldNotChangeId() {
        Epic epic = new Epic("Test first epic", "Test first epic description", 1);
        epic.setId(50);
        assertEquals(1, epic.getId(), "Айди эпика изменился!");
    }

}