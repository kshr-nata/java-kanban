import java.util.List;
import java.util.Set;

public interface TaskManager {
    //получение списка всех задач
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    //удаление всех задач
    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    //получение по идентификатору
    Task getTask(Integer id) throws NotFoundException;

    Subtask getSubtask(Integer id) throws NotFoundException;

    Epic getEpic(Integer id) throws NotFoundException;

    //создание новых задач
    int makeNewTask(Task task);

    int makeNewEpic(Epic task);

    int makeNewSubtask(Subtask task);

    //обновление задач
    void updateTask(Task newTask) throws NotFoundException;

    void updateSubtask(Subtask newTask) throws NotFoundException;

    void updateEpic(Epic newEpic) throws NotFoundException;

    //удаление задачи по айди
    void removeTask(int id) throws NotFoundException;

    void removeSubtask(int id) throws NotFoundException;

    void removeEpic(int id) throws NotFoundException;

    List<Task> getHistory();

    HistoryManager getHistoryManager();

    //получение всех подзадач эпика
    List<Subtask> getSubtasksByEpic(Epic epic) throws NotFoundException;

    Set<Task> getPrioritizedTasks();

    boolean isTaskIntersection(Task task);
}

