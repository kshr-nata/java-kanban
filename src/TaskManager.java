import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //получение списка всех задач
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    //удаление всех задач
    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    //получение по идентификатору
    Task getTask(Integer id);

    Subtask getSubtask(Integer id);

    Epic getEpic(Integer id);

    //создание новых задач
    int makeNewTask(Task task);

    int makeNewEpic(Epic task);

    int makeNewSubtask(Subtask task);

    //обновление задач
    void updateTask(Task newTask);

    void updateSubtask(Subtask newTask);

    void updateEpic(Epic newEpic);

    //удаление задачи по айди
    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    List<Task> getHistory();

    HistoryManager getHistoryManager();

    //получение всех подзадач эпика
    ArrayList<Subtask> getSubtasksByEpic(Epic epic);
}

