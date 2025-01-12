import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int currentId;
    private final HistoryManager historyManager;

    InMemoryTaskManager(){
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
        for(Task task : tasks.values()){
            taskArray.add(task);
        }
        return taskArray;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicArray = new ArrayList<>();
        for(Epic epic : epics.values()){
            epicArray.add(epic);
        }
        return epicArray;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> SubtasksArray = new ArrayList<>();
        for(Subtask subtask : subtasks.values()){
            SubtasksArray.add(subtask);
        }
        return SubtasksArray;
    }

    //удаление всех задач
    @Override
    public void clearTasks(){
        tasks.clear();
    }

    @Override
    public void clearEpics(){
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks(){
        subtasks.clear();
        for (Epic epic : epics.values()){
            epic.clearSubtasks();
            epic.updateStatus();
        }
    }

    //получение по идентификатору
    @Override
    public Task getTask(Integer id){
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id){
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpic(Integer id){
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    //создание новых задач
    @Override
    public int makeNewTask(Task task){
        int id = getNewId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int makeNewEpic(Epic task){
        int id = getNewId();
        task.setId(id);
        epics.put(id, task);
        return id;
    }

    @Override
    public int makeNewSubtask(Subtask task){
        if (epics.containsKey(task.getEpic().getId())) {
            int id = getNewId();
            task.setId(id);
            subtasks.put(id, task);
            Epic epic = task.getEpic();
            epic.addSubtaskToEpic(task);
            epic.updateStatus();
            return id;
        }
        return 0;
    }

    //обновление задач
    @Override
    public void updateTask(Task newTask){
        int id = newTask.getId();
        Task updatedTask = getTask(id);
        if (updatedTask != null) {
            updatedTask.setName(newTask.getName());
            updatedTask.setDescription(newTask.getDescription());
            updatedTask.setStatus(newTask.getStatus());
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
            Epic epic = updatedSubtask.getEpic();
            epic.updateStatus();
        }
    }

    @Override
    public void updateEpic(Epic newEpic){
        int id = newEpic.getId();
        Epic updatedEpic = getEpic(id);
        if (updatedEpic != null) {
            updatedEpic.setName(newEpic.getName());
            updatedEpic.setDescription(newEpic.getDescription());
        }
    }

    //удаление задачи по айди
    @Override
    public void removeTask(int id){
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(int id){
        Subtask subtask = getSubtask(id);
        if (subtask != null){
            Epic epic = subtask.getEpic();
            epic.removeSubtask(subtask);
            epic.updateStatus();
        }
        subtasks.remove(id);
    }

    @Override
    public void removeEpic(int id){
        Epic epic = epics.get(id);
        if (epic != null){
            ArrayList<Subtask> epicsSubtasks = epic.getSubtasks();
            ArrayList<Integer> removedSubtaskIds = new ArrayList<>();
            for (Subtask subtask : epicsSubtasks){
                int subtaskId = subtask.getId();
                removedSubtaskIds.add(subtaskId);
            }
            for (int subtaskId : removedSubtaskIds) {
                removeSubtask(subtaskId);
            }
        }
        epics.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
    //получение всех подзадач эпика
    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic){
        return epic.getSubtasks();
    }

    //генерация нового айди
    private int getNewId(){
        currentId += 1;
        return currentId;
    }

}


