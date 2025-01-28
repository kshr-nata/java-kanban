import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",  TaskStatus.NEW);
        final int taskId = taskManager.makeNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals("Test addNewTask", task.getName(), "Имя задачи изменилось.");
        assertEquals("Test addNewTask description", task.getDescription(), "Описание задачи изменилось.");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Статус задачи изменился.");
    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",  TaskStatus.NEW);
        final int taskId = taskManager.makeNewTask(task);
        taskManager.removeTask(taskId);
        assertEquals(0, taskManager.getAllTasks().size(),"Задача не удаляется");
    }

    @Test
    void shouldClearTasks() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",  TaskStatus.NEW);
        final int taskId = taskManager.makeNewTask(task);
        taskManager.clearTasks();
        assertEquals(0, taskManager.getAllTasks().size(),"Задачи не очищаются");
    }

    void updateTask() {
        Task task = new Task("Test", "Test description",  TaskStatus.NEW);
        final int taskId = taskManager.makeNewTask(task);
        Task secondTask = new Task("Test updateTask", "Test updateTask description", TaskStatus.IN_PROGRESS, taskId);
        taskManager.updateTask(secondTask);
        assertEquals("Test updateTask", task.getName(), "Имя задачи не изменилось.");
        assertEquals("Test updateTask description", task.getDescription(), "Описание задачи не изменилось.");
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus(), "Статус задачи не изменился");
    }

    @Test
    void taskShouldEqualsTaskWithEqualsId() {
        Task firstTask = new Task("Test first task", "Test first task description", TaskStatus.NEW);
        firstTask.setId(1);
        Task secondTask = new Task("Test second task", "Test second task description", TaskStatus.NEW);
        secondTask.setId(1);
        assertEquals(firstTask, secondTask, "Задачи с одинаковым айди не равны.");
    }

    @Test
    void shouldNotChangeId() {
        Task task = new Task("Test first task", "Test first task description", TaskStatus.NEW, 1);
        task.setId(50);
        assertEquals(1, task.getId(), "Айди задачи изменился!");
    }
}