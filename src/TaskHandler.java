import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            getTasksHandle(exchange, gson);
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            getTaskHandle(exchange, gson, pathParts);
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            postTaskHandle(exchange, gson);
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            deleteTaskHandle(exchange, pathParts);
        } else {
            sendNotFound(exchange, "Метод не найден");
        }
    }

    private void getTasksHandle(HttpExchange exchange, Gson gson) throws IOException {
        try {
            List<Task> tasks = taskManager.getAllTasks();
            String text = gson.toJson(tasks);
            sendText(exchange, text);
        } catch (Exception exp) {
            sendNotFound(exchange, "При выполнении запроса возникла ошибка " + exp.getMessage());
        }
    }

    private void getTaskHandle(HttpExchange exchange, Gson gson, String[] pathParts) throws IOException {
        try {
            int id = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTask(id);
            sendText(exchange, gson.toJson(task));
        } catch (Exception e) {
            sendNotFound(exchange, "Задача с айди " + pathParts[2] + " не найдена");
        }
    }

    private void postTaskHandle(HttpExchange exchange, Gson gson) throws IOException {
        try {
            InputStream bodyInputStream = exchange.getRequestBody();
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task taskDeserialized = gson.fromJson(body, Task.class);
            if (taskDeserialized == null) {
                sendNotFound(exchange, "Не удалось преобразовать тело запроса в задачу!");
                return;
            }
            if (taskManager.isTaskIntersection(taskDeserialized)) {
                sendHasInteractions(exchange);
                return;
            }
            if (taskDeserialized.getId() == null || taskDeserialized.getId() == 0) {
                taskManager.makeNewTask(taskDeserialized);
                sendSuccessWithoutBody(exchange);
            } else {
                if (taskManager.getTask(taskDeserialized.getId()) == null) {
                    sendNotFound(exchange, "Не найдена задача с айди " + taskDeserialized.getId());
                } else {
                    taskManager.updateTask(taskDeserialized);
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
            Task task = taskManager.getTask(id);
            taskManager.removeTask(task.getId());
            sendText(exchange, "Задача успешно удалена");
        } catch (Exception e) {
            sendNotFound(exchange, "Задача с айди " + pathParts[2] + " не найдена");
        }
    }
}