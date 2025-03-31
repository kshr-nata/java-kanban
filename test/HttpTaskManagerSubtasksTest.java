import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    Epic epic = new Epic("Epic 1", "Test description");

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        final int epicId = manager.makeNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask("Test 2", "Testing task 2",
                TaskStatus.NEW, manager.getEpic(epicId), LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        final int epicId = manager.makeNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask("Test 2", "Testing task 2",
                TaskStatus.NEW, manager.getEpic(epicId), LocalDateTime.now(), Duration.ofMinutes(5));
        manager.makeNewSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(),"Ответ от сервера не соответствует ожидаемому.");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Task> tasks = gson.fromJson(jsonArray, new TaskListTypeToken().getType());

        //проверяем, что вернулась одна задача с корректным именем
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        final int epicId = manager.makeNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask("Test 2", "Testing task 2",
                TaskStatus.NEW, manager.getEpic(epicId), LocalDateTime.now(), Duration.ofMinutes(5));
        final int taskId = manager.makeNewSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(),"Ответ от сервера не соответствует ожидаемому.");
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task responseTask = gson.fromJson(jsonObject, Subtask.class);

        //проверяем, что вернулась одна задача с корректным именем
        assertNotNull(responseTask, "Задача не возвращается");
        assertEquals("Test 2", responseTask.getName(), "Некорректное имя задачи");
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        final int epicId = manager.makeNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask("Test 2", "Testing task 2",
                TaskStatus.NEW, manager.getEpic(epicId), LocalDateTime.now(), Duration.ofMinutes(5));
        final int taskId = manager.makeNewSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllSubtasks().size(), "Задача не удаляется");
    }
}