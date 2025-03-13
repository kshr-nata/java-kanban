import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("epics")) {
            getTasksHandle(exchange, gson);
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && pathParts[1].equals("epics")) {
            getTaskHandle(exchange, gson, pathParts);
        } else if (requestMethod.equals("GET") && pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            getSubtasksHandle(exchange, gson, pathParts);
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals("epics")) {
            postTaskHandle(exchange, gson);
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3 && pathParts[1].equals("epics")) {
            deleteTaskHandle(exchange, pathParts);
        } else {
            sendNotFound(exchange, "Метод не найден");
        }
    }

    private void getTasksHandle(HttpExchange exchange, Gson gson) throws IOException {
        try {
            List<Epic> tasks = taskManager.getAllEpics();
            String text = gson.toJson(tasks);
            sendText(exchange, text);
        } catch (Exception exp) {
            sendNotFound(exchange, "При выполнении запроса возникла ошибка " + exp.getMessage());
        }
    }

    private void getSubtasksHandle(HttpExchange exchange, Gson gson, String[] pathParts) throws IOException {
        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic task = taskManager.getEpic(id);
            List<Subtask> tasks = taskManager.getSubtasksByEpic(task);
            String text = gson.toJson(tasks);
            sendText(exchange, text);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Эпик с айди " + pathParts[2] + " не найден");
        } catch (Exception e) {
            sendNotFound(exchange, "При выполнении запроса возникла ошибка " + e.getMessage());
        }
    }

    private void getTaskHandle(HttpExchange exchange, Gson gson, String[] pathParts) throws IOException {
        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic task = taskManager.getEpic(id);
            sendText(exchange, gson.toJson(task));
        } catch (Exception e) {
            sendNotFound(exchange, "Эпик с айди " + pathParts[2] + " не найден");
        }
    }

    private void postTaskHandle(HttpExchange exchange, Gson gson) throws IOException {
        try {
            InputStream bodyInputStream = exchange.getRequestBody();
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic taskDeserialized = gson.fromJson(body, Epic.class);
            if (taskDeserialized == null) {
                sendNotFound(exchange, "Не удалось преобразовать тело запроса в эпик!");
                return;
            }
            if (taskDeserialized.getId() == null || taskDeserialized.getId() == 0) {
                taskManager.makeNewEpic(taskDeserialized);
                sendSuccessWithoutBody(exchange);
            } else {
                if (taskManager.getEpic(taskDeserialized.getId()) == null) {
                    sendNotFound(exchange, "Не найден эпик с айди " + taskDeserialized.getId());
                } else {
                    taskManager.updateEpic(taskDeserialized);
                    sendSuccessWithoutBody(exchange);
                }
            }
        } catch (Exception exp) {
            sendNotFound(exchange, "При выполнении запроса возникла ошибка " + exp.getMessage());
        }
    }

    private void deleteTaskHandle(HttpExchange exchange, String[] pathParts) throws IOException {
        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic task = taskManager.getEpic(id);
            taskManager.removeEpic(task.getId());
            sendText(exchange, "Эпик успешно удален");
        } catch (Exception e) {
            sendNotFound(exchange, "Эпик с айди " + pathParts[2] + " не найден");
        }
    }
}