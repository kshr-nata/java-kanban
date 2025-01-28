import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    TaskManager taskManager;
    Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);

    @BeforeEach
    void beforeEach(){
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

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

    @Test
    void elementsShouldBeUniqe(){
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        historyManager.add(task);
        final List<Task> historyAfterAdd = historyManager.getHistory();
        assertEquals(1, historyAfterAdd.size(), "Количество элементов в истории не равно 1");
    }

    @Test
    void elementShouldBeAddedInTail(){
        taskManager.makeNewTask(task);
        historyManager.add(task);
        Task secondTask = new Task("Test elementsShouldBeUniqe", "Test elementsShouldBeUniqe description", TaskStatus.NEW);
        taskManager.makeNewTask(secondTask);
        historyManager.add(secondTask);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Количество элементов в истории не равно 2");
        assertEquals(secondTask, history.get(1), "Элемент не был добавлен в конец истории");
    }

    @Test
    void shouldRemoveFromHistory(){
        taskManager.makeNewTask(task);
        historyManager.add(task);
        Task secondTask = new Task("Test elementsShouldBeUniqe", "Test elementsShouldBeUniqe description", TaskStatus.NEW);
        taskManager.makeNewTask(secondTask);
        historyManager.add(secondTask);
        Task thirdTask = new Task("Test shouldRemoveMidle", "Test shouldRemoveMidle description", TaskStatus.NEW);
        taskManager.makeNewTask(thirdTask);
        historyManager.add(thirdTask);

        //удаляем из серидины
        taskManager.removeTask(secondTask.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Количество элементов в истории не равно 2");
        assertEquals(task, history.get(0), "Первый элемент истории определен неверно");
        assertEquals(thirdTask, history.get(1), "Последний элемент истории определен неверно");

        //удаляем первый элемент
        taskManager.removeTask(task.getId());
        final List<Task> historyAfterRemoveFirst = historyManager.getHistory();
        assertEquals(1, historyAfterRemoveFirst.size(), "Количество элементов в истории не равно 1");
        assertEquals(thirdTask, historyAfterRemoveFirst.get(0), "Элемент истории определен неверно");

        //удаляем единственный элемент
        taskManager.removeTask(thirdTask.getId());
        final List<Task> historyAfterRemoveAll = historyManager.getHistory();
        assertEquals(0, historyAfterRemoveAll.size(), "Количество элементов в истории не равно 0");
    }
}