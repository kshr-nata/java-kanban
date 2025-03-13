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

public class HttpTaskManagerHistoryTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException, NotFoundException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        final int taskId = manager.makeNewTask(task);
        Task returnedTask = manager.getTask(taskId);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "Ответ от сервера не соответствует ожидаемому.");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Task> tasks = gson.fromJson(jsonArray, new TaskListTypeToken().getType());

        assertNotNull(tasks, "История не возвращается");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasks.get(0).getName(), "Некорректное имя задачи");
    }
}