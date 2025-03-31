import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            getTasksHandle(exchange, gson);
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            getTaskHandle(exchange, gson, pathParts);
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            postTaskHandle(exchange, gson);
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            deleteTaskHandle(exchange, pathParts);
        } else {
            sendNotFound(exchange, "Метод не найден");
        }
    }

    private void getTasksHandle(HttpExchange exchange, Gson gson) throws IOException {
        try {
            List<Subtask> tasks = taskManager.getAllSubtasks();
            String text = gson.toJson(tasks);
            sendText(exchange, text);
        } catch (Exception exp) {
            sendNotFound(exchange, "При выполнении запроса возникла ошибка " + exp.getMessage());
        }
    }

    private void getTaskHandle(HttpExchange exchange, Gson gson, String[] pathParts) throws IOException {
        try {
            int id = Integer.parseInt(pathParts[2]);
            Subtask task = taskManager.getSubtask(id);
            sendText(exchange, gson.toJson(task));
        } catch (Exception e) {
            sendNotFound(exchange, "Подзадача с айди " + pathParts[2] + " не найдена");
        }
    }

    private void postTaskHandle(HttpExchange exchange, Gson gson) throws IOException {
        try {
            InputStream bodyInputStream = exchange.getRequestBody();
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask taskDeserialized = gson.fromJson(body, Subtask.class);
            if (taskDeserialized == null) {
                sendNotFound(exchange, "Не удалось преобразовать тело запроса в задачу!");
                return;
            }
            if (taskManager.isTaskIntersection(taskDeserialized)) {
                sendHasInteractions(exchange);
                return;
            }
            if (taskDeserialized.getId() == null || taskDeserialized.getId() == 0) {
                int id = taskManager.makeNewSubtask(taskDeserialized);
                if (id == 0) {
                    sendNotFound(exchange, "Не удалось создать задачу, возможно неверно заполнено поле epicId");
                } else {
                    sendSuccessWithoutBody(exchange);
                }
            } else {
                if (taskManager.getSubtask(taskDeserialized.getId()) == null) {
                    sendNotFound(exchange, "Не найдена задача с айди " + taskDeserialized.getId());
                } else {
                    taskManager.updateSubtask(taskDeserialized);
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
            Subtask task = taskManager.getSubtask(id);
            taskManager.removeSubtask(task.getId());
            sendText(exchange, "Подзадача успешно удалена");
        } catch (Exception e) {
            sendNotFound(exchange, "Подзадача с айди " + pathParts[2] + " не найдена");
        }
    }
}