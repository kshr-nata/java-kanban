import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Test
    void loadFromEmptyFile() throws IOException {
        File file = File.createTempFile("testEmptyFile-", ".csv");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(taskManager, "taskManager is null!");
        assertEquals(taskManager.getAllTasks().size(), 0, "Количество задач не равно 0");
        assertEquals(taskManager.getHistory().size(), 0, "Количество задач в истории не равно 0");
    }

    @Test
    public void testException() {
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("/invalid/path/task.csv"));
            taskManager.save();
        }, "Попытка сохранить файл должна приводить к ошибке");
    }

    @Test
    void saveToEmptyFile() throws IOException {
        File file = File.createTempFile("testEmptyFile-", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.save();
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(taskManagerFromFile, "taskManagerFromFile is null!");
        assertEquals(taskManagerFromFile.getAllTasks().size(), 0, "Количество задач не равно 0");
        assertEquals(taskManagerFromFile.getHistory().size(), 0, "Количество задач в истории не равно 0");
    }

    @Test
    void saveLoadFile() throws IOException, NotFoundException {
        File file = File.createTempFile("testEmptyFile-", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Task task = new Task("Test saveLoadFile", "Test saveLoadFile description", TaskStatus.NEW, LocalDateTime.of(2025, 1, 20, 8, 30), Duration.ofMinutes(30));
        final int taskId = taskManager.makeNewTask(task);
        //вызовем получение задачи для обновления истории
        taskManager.getTask(taskId);

        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(taskManagerFromFile, "taskManagerFromFile is null!");
        assertEquals(taskManagerFromFile.getAllTasks().size(), taskManager.getAllTasks().size(), "Количество задач в менеджерах не равно");
        assertEquals(taskManagerFromFile.getHistory().size(), taskManagerFromFile.getHistory().size(), "Количество задач в истории менеджеров не равно");
    }

    @Override
    TaskManager initTaskManager() {
        try {
            File file = File.createTempFile("testEmptyFile-", ".csv");
            return  FileBackedTaskManager.loadFromFile(file);
        } catch (IOException ignored) {
        }
        return null;
    }
}