import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = initTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",  TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
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
        Task task = new Task("Test addNewTask", "Test addNewTask description",  TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int taskId = taskManager.makeNewTask(task);
        taskManager.removeTask(taskId);
        assertEquals(0, taskManager.getAllTasks().size(),"Задача не удаляется");
    }

    @Test
    void shouldClearTasks() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",  TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int taskId = taskManager.makeNewTask(task);
        taskManager.clearTasks();
        assertEquals(0, taskManager.getAllTasks().size(),"Задачи не очищаются");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test", "Test description",  TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int taskId = taskManager.makeNewTask(task);
        Task secondTask = new Task("Test updateTask", "Test updateTask description", TaskStatus.IN_PROGRESS, taskId, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.updateTask(secondTask);
        assertEquals("Test updateTask", taskManager.getTask(taskId).getName(), "Имя задачи не изменилось.");
        assertEquals("Test updateTask description", taskManager.getTask(taskId).getDescription(), "Описание задачи не изменилось.");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(taskId).getStatus(), "Статус задачи не изменился");
    }

    @Test
    void taskShouldEqualsTaskWithEqualsId() {
        Task firstTask = new Task("Test first task", "Test first task description", TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        firstTask.setId(1);
        Task secondTask = new Task("Test second task", "Test second task description", TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        secondTask.setId(1);
        assertEquals(firstTask, secondTask, "Задачи с одинаковым айди не равны.");
    }

    @Test
    void shouldNotChangeId() {
        Task task = new Task("Test first task", "Test first task description", TaskStatus.NEW, 1, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        task.setId(50);
        assertEquals(1, task.getId(), "Айди задачи изменился!");
    }

    @Test
    void TaskShouldNotBeAddedIfTimeIntersection() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test TaskShouldNotBeAddedIfTimeIntersection", "Test TaskShouldNotBeAddedIfTimeIntersection description",  TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int taskId = taskManager.makeNewTask(task);
        Task secondTask = new Task("Test TaskShouldNotBeAddedIfTimeIntersection", "Test TaskShouldNotBeAddedIfTimeIntersection description",  TaskStatus.NEW, LocalDateTime.of(2025, 1,20,8,15), Duration.ofMinutes(30));
        final int secondTaskId = taskManager.makeNewTask(secondTask);
        assertEquals(1, taskManager.getAllTasks().size(), "количество задач не равно 1!");
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
        assertEquals("Test updateEpic", taskManager.getEpic(epicId).getName(), "Имя эпика не изменилось.");
        assertEquals("Test updateEpic description", taskManager.getEpic(epicId).getDescription(), "Описание эпика не изменилось.");
    }

    @Test
    void shouldChangeEpicStatus() {
        Epic epic = new Epic("Test", "Test description");
        final int epicId = taskManager.makeNewEpic(epic);
        assertEquals(TaskStatus.NEW, taskManager.getEpic(epicId).getStatus(), "У эпика без сабтасков статус не равен NEW");
        Subtask subtask = new Subtask("Test", "Test description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "У эпика статус не равен NEW");

        Subtask updatedSubtask = new Subtask("Test", "Test description", TaskStatus.DONE, epic, subtaskId, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epicId).getStatus(), "У эпика статус не равен DONE");

        Subtask secondSubtask = new Subtask("Test", "Test description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,16,30), Duration.ofMinutes(30));
        final int secondSubtaskId = taskManager.makeNewSubtask(secondSubtask);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(), "У эпика статус не равен IN_PROGRESS");

        Subtask updatedSubtask2 = new Subtask("Test", "Test description", TaskStatus.IN_PROGRESS, epic, subtaskId, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask);
        Subtask updatedSecondSubtask = new Subtask("Test", "Test description", TaskStatus.IN_PROGRESS, epic, secondSubtaskId, LocalDateTime.of(2025, 1,20,16,30), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSecondSubtask);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(), "У эпика статус не равен IN_PROGRESS");
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
    void epicShouldNotChangeId() {
        Epic epic = new Epic("Test first epic", "Test first epic description", 1);
        epic.setId(50);
        assertEquals(1, epic.getId(), "Айди эпика изменился!");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewSubtask", "Test addNewSubtask description");
        final int epicId = taskManager.makeNewEpic(epic);

        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int subtaskId = taskManager.makeNewSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Сабтаск не найден.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
        assertEquals(1, epic.getSubtasks().size(), "Неверное количество сабтасков у эпика.");
        assertEquals("Test addNewSubtask", subtask.getName(), "Имя сабтаска изменилось.");
        assertEquals("Test addNewSubtask description", subtask.getDescription(), "Описание сабтаска изменилось.");
        assertEquals(TaskStatus.NEW, subtask.getStatus(), "Статус сабтаска изменился.");
        assertEquals(epic, subtask.getEpic(), "Эпик сабтаска изменился");
    }

    @Test
    void shouldRemoveSubtask() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        taskManager.removeSubtask(subtaskId);
        assertEquals(0, taskManager.getAllSubtasks().size(),"Сабтаск не удаляется");

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        Subtask foundSubtask = null;
        for (Subtask currentSubtask : subtasks) {
            if(currentSubtask.equals(subtask)) {
                foundSubtask = currentSubtask;
            }
        }
        assertEquals(null, foundSubtask, "Сабтаск не удаляется из подзадач эпика" );
    }

    @Test
    void shouldClearSubtasks() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        taskManager.clearSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size(),"Сабтаски не очищаются");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Test", "Test description");
        final int epicId = taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test", "Test description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        Subtask secondSubtask = new Subtask("Test updateSubtask", "Test updateSubtask description", TaskStatus.IN_PROGRESS, epic, subtaskId, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        taskManager.updateSubtask(secondSubtask);
        assertEquals("Test updateSubtask", taskManager.getSubtask(subtaskId).getName(), "Имя сабтаска не изменилось.");
        assertEquals("Test updateSubtask description", taskManager.getSubtask(subtaskId).getDescription(), "Описание сабтаска не изменилось.");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtask(subtaskId).getStatus(), "Статус сабтаска не изменился");
    }

    @Test
    void SubtaskShouldEqualsSubtaskWithEqualsId() {
        Epic firstEpic = new Epic("Test first epic", "Test first epic description");
        firstEpic.setId(0);
        Subtask firstSubtask = new Subtask("Test first subtask", "Test first epic description", TaskStatus.NEW, firstEpic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        firstSubtask.setId(1);
        Subtask secondSubtask = new Subtask("Test second subtask", "Test second subtask description", TaskStatus.NEW, firstEpic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        secondSubtask.setId(1);
        assertEquals(firstSubtask, secondSubtask, "Сабтакски с одинаковым айди не равны.");
    }

    @Test
    void subtaskShouldNotChangeId() {
        Epic epic = new Epic("Test first epic", "Test first epic description", 0);
        Subtask subtask = new Subtask("Test first subtask", "Test first epic description", TaskStatus.NEW, epic, 1, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        subtask.setId(50);
        assertEquals(1, subtask.getId(), "Айди сабтаска изменился!");
    }

    @Test
    void shouldEqualsSubtaskFromEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatus.NEW, epic, LocalDateTime.of(2025, 1,20,8,30), Duration.ofMinutes(30));
        final int subtaskId = taskManager.makeNewSubtask(subtask);
        assertEquals(taskManager.getSubtask(subtaskId).getEpic(), epic);
        assertEquals(taskManager.getSubtask(subtaskId), taskManager.getSubtasksByEpic(epic).getFirst());
    }

    TaskManager initTaskManager() {
        return  Managers.getDefault();
    }
}
