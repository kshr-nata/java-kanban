import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected int currentId;
    protected final HistoryManager historyManager;
    protected TreeSet<Task> prioritizedTasks;

    InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>();
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
        currentId = 0;
        historyManager = Managers.getDefaultHistory();
    }

    //получение списка всех задач
    @Override
    public List<Task> getAllTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtasks.values().stream().toList();
    }

    //удаление всех задач
    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        }
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
        }
    }

    //получение по идентификатору
    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с айди " + id + " не найдена.");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с айди " + id + " не найдена.");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с айди " + id + " не найден.");
        }
        historyManager.add(epic);
        return epic;
    }

    //создание новых задач
    @Override
    public int makeNewTask(Task task) {
        if (task.getStartTime() != null && isTaskIntersection(task)) {
            return 0;
        }
        int id = getNewId();
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return id;
    }

    @Override
    public int makeNewEpic(Epic task) {
        int id = getNewId();
        task.setId(id);
        task.updateStatus();
        epics.put(id, task);
        return id;
    }

    @Override
    public int makeNewSubtask(Subtask task) {
        if (epics.containsKey(task.getEpicId())) {
            if (task.getStartTime() != null && isTaskIntersection(task)) {
                return 0;
            }
            int id = getNewId();
            task.setId(id);
            subtasks.put(id, task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
            Epic epic = getEpic(task.getEpicId());
            epic.addSubtaskToEpic(task);
            epics.put(epic.getId(), epic);
            return id;
        }
        return 0;
    }

    //обновление задач
    @Override
    public void updateTask(Task newTask) {
        if (newTask.getStartTime() != null && isTaskIntersection(newTask)) {
            return;
        }
        int id = newTask.getId();
        Task oldTask = getTask(id);
        if (oldTask.getStartTime() != null) {
            prioritizedTasks.remove(oldTask);
        }
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
        tasks.put(id, newTask);
    }

    @Override
    public void updateSubtask(Subtask newTask) {
        if (newTask.getStartTime() != null && isTaskIntersection(newTask)) {
            return;
        }
        int id = newTask.getId();

        Subtask updatedSubtask = getSubtask(id);
        if (updatedSubtask.getStartTime() != null) {
            prioritizedTasks.remove(updatedSubtask);
        }
        updatedSubtask.setDescription(newTask.getDescription());
        updatedSubtask.setName(newTask.getName());
        updatedSubtask.setStatus(newTask.getStatus());
        updatedSubtask.setDuration(newTask.getDuration());
        updatedSubtask.setStartTime(newTask.getStartTime());
        if (updatedSubtask.getStartTime() != null) {
            prioritizedTasks.add(updatedSubtask);
        }
        Epic epic = getEpic(updatedSubtask.getEpicId());
        epic.updateStatus();
        epic.changeTerms();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        int id = newEpic.getId();
        Epic updatedEpic = getEpic(id);
        updatedEpic.setName(newEpic.getName());
        updatedEpic.setDescription(newEpic.getDescription());
    }

    //удаление задачи по айди
    @Override
    public void removeTask(int id) {
        Task task = getTask(id);
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = getSubtask(id);
        Epic epic = getEpic(subtask.getEpicId());
        epic.removeSubtask(subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtask);
        }
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = getEpic(id);
        List<Integer> removedSubtaskIds = epic.getSubtasks().stream()
                .map(Subtask::getId).toList();
        for (int subtaskId : removedSubtaskIds) {
            removeSubtask(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //получение всех подзадач эпика
    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return getEpic(epic.getId()).getSubtasks().stream().toList();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean isTaskIntersection(Task task) {
        if (task.getStartTime() != null) {
            for (Task chekTask : getPrioritizedTasks()) {
                if (!Objects.equals(task.getId(), chekTask.getId()) && task.getStartTime().isBefore(chekTask.getEndTime())
                        && task.getEndTime().isAfter(chekTask.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    //генерация нового айди
    private int getNewId() {
        currentId += 1;
        return currentId;
    }

}


