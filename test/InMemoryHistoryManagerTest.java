import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    TaskManager taskManager;
    Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW, LocalDateTime.of(2025, 1,20,6,30), Duration.ofMinutes(30));

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    void historyShouldBeEmpty() {
        assertEquals(0, historyManager.getHistory().size(), "История не пустая");
    }

    @Test
    void add() {
        String name = task.getName();
        String description = task.getDescription();
        int id = task.getId();
        TaskStatus status = task.getStatus();
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История равна null.");
        assertEquals(1, history.size(), "Количество записей в истории не равно 1.");
        String secondName = task.getName();
        String secondDescription = task.getDescription();
        int secondId = task.getId();
        TaskStatus secondStatus = task.getStatus();
        assertEquals(name, secondName, "Изменилось имя задачи");
        assertEquals(description, secondDescription, "Изменилось описание задачи");
        assertEquals(id, secondId, "Изменился айди задачи");
        assertEquals(status, secondStatus, "Изменился статус задачи");
    }

    @Test
    void elementsShouldBeUniqe() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        historyManager.add(task);
        final List<Task> historyAfterAdd = historyManager.getHistory();
        assertEquals(1, historyAfterAdd.size(), "Количество элементов в истории не равно 1");
    }

    @Test
    void elementShouldBeAddedInTail() {
        taskManager.makeNewTask(task);
        historyManager.add(task);
        Task secondTask = new Task("Test elementsShouldBeUniqe", "Test elementsShouldBeUniqe description", TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.makeNewTask(secondTask);
        historyManager.add(secondTask);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Количество элементов в истории не равно 2");
        assertEquals(secondTask, history.get(1), "Элемент не был добавлен в конец истории");
    }

    @Test
    void shouldRemoveFromHistory() throws NotFoundException {
        final int taskId = taskManager.makeNewTask(task);
        historyManager.add(taskManager.getTask(taskId));
        Task secondTask = new Task("Test elementsShouldBeUniqe", "Test elementsShouldBeUniqe description", TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.makeNewTask(secondTask);
        historyManager.add(secondTask);
        Task thirdTask = new Task("Test shouldRemoveMidle", "Test shouldRemoveMidle description", TaskStatus.NEW, LocalDateTime.of(2025, 1,20,16,30), Duration.ofMinutes(30));
        taskManager.makeNewTask(thirdTask);
        historyManager.add(thirdTask);
        Task fourthTask = new Task("Test shouldRemoveMidle", "Test shouldRemoveMidle description", TaskStatus.NEW, LocalDateTime.of(2025, 1,20,20,30), Duration.ofMinutes(30));
        taskManager.makeNewTask(fourthTask);
        historyManager.add(fourthTask);

        //удаляем из середины
        taskManager.removeTask(secondTask.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Количество элементов в истории не равно 3");
        assertEquals(task, history.get(0), "Первый элемент истории определен неверно");
        assertEquals(fourthTask, history.get(2), "Последний элемент истории определен неверно");

        //удаляем первый элемент
        taskManager.removeTask(task.getId());
        final List<Task> historyAfterRemoveMiddle = historyManager.getHistory();
        assertEquals(2, historyAfterRemoveMiddle.size(), "Количество элементов в истории не равно 2");
        assertEquals(thirdTask, historyAfterRemoveMiddle.get(0), "Первый элемент истории определен неверно");
        assertEquals(fourthTask, historyAfterRemoveMiddle.get(1), "Последний элемент истории определен неверно");

        //удаляем последний элемент
        taskManager.removeTask(fourthTask.getId());
        final List<Task> historyAfterRemoveFirstMiddle = historyManager.getHistory();
        assertEquals(1, historyAfterRemoveFirstMiddle.size(), "Количество элементов в истории не равно 1");
        assertEquals(thirdTask, historyAfterRemoveFirstMiddle.get(0), "Первый элемент истории определен неверно");

        //удаляем единственный элемент
        taskManager.removeTask(thirdTask.getId());
        final List<Task> historyAfterRemoveAll = historyManager.getHistory();
        assertEquals(0, historyAfterRemoveAll.size(), "Количество элементов в истории не равно 0");
    }
}