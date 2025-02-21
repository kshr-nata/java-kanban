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
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskArray = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskArray.add(task);
        }
        return taskArray;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicArray = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicArray.add(epic);
        }
        return epicArray;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasksArray = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtasksArray.add(subtask);
        }
        return subtasksArray;
    }

    //удаление всех задач
    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
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
            prioritizedTasks.remove(subtask);
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
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    //создание новых задач
    @Override
    public int makeNewTask(Task task) {
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
        epics.put(id, task);
        return id;
    }

    @Override
    public int makeNewSubtask(Subtask task) {
        if (epics.containsKey(task.getEpic().getId())) {
            int id = getNewId();
            task.setId(id);
            subtasks.put(id, task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
            Epic epic = task.getEpic();
            epic.addSubtaskToEpic(task);
            return id;
        }
        return 0;
    }

    //обновление задач
    @Override
    public void updateTask(Task newTask) {
        int id = newTask.getId();
        Task updatedTask = getTask(id);
        if (updatedTask != null) {
            updatedTask.setName(newTask.getName());
            updatedTask.setDescription(newTask.getDescription());
            updatedTask.setStatus(newTask.getStatus());
            updatedTask.setDuration(newTask.getDuration());
            updatedTask.setStartTime(newTask.getStartTime());
            prioritizedTasks.remove(updatedTask);
            if (updatedTask.getStartTime() != null) {
                prioritizedTasks.add(updatedTask);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask newTask) {
        int id = newTask.getId();
        Subtask updatedSubtask = getSubtask(id);
        if (updatedSubtask != null) {
            updatedSubtask.setDescription(newTask.getDescription());
            updatedSubtask.setName(newTask.getName());
            updatedSubtask.setStatus(newTask.getStatus());
            updatedSubtask.setDuration(newTask.getDuration());
            updatedSubtask.setStartTime(newTask.getStartTime());
            prioritizedTasks.remove(updatedSubtask);
            if (updatedSubtask.getStartTime() != null) {
                prioritizedTasks.add(updatedSubtask);
            }
            Epic epic = updatedSubtask.getEpic();
            epic.updateStatus();
            epic.changeTerms();
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        int id = newEpic.getId();
        Epic updatedEpic = getEpic(id);
        if (updatedEpic != null) {
            updatedEpic.setName(newEpic.getName());
            updatedEpic.setDescription(newEpic.getDescription());
        }
    }

    //удаление задачи по айди
    @Override
    public void removeTask(int id) {
        Task task = getTask(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = getSubtask(id);
        if (subtask != null) {
            Epic epic = subtask.getEpic();
            epic.removeSubtask(subtask);
            prioritizedTasks.remove(subtask);
        }
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            ArrayList<Subtask> epicsSubtasks = epic.getSubtasks();
            ArrayList<Integer> removedSubtaskIds = new ArrayList<>();
            for (Subtask subtask : epicsSubtasks) {
                int subtaskId = subtask.getId();
                removedSubtaskIds.add(subtaskId);
            }
            for (int subtaskId : removedSubtaskIds) {
                removeSubtask(subtaskId);
            }
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
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    //генерация нового айди
    private int getNewId() {
        currentId += 1;
        return currentId;
    }

}


