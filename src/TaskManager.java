import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int currentId;

    TaskManager(){
        tasks = new HashMap<Integer, Task>();
        epics = new HashMap<Integer, Epic>();
        subtasks = new HashMap<Integer, Subtask>();
        currentId = 0;
    }

    //получение списка всех задач
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskArray = new ArrayList<>();
        for(Task task : tasks.values()){
            taskArray.add(task);
        }
        return taskArray;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicArray = new ArrayList<>();
        for(Epic epic : epics.values()){
            epicArray.add(epic);
        }
        return epicArray;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> SubtasksArray = new ArrayList<>();
        for(Subtask subtask : subtasks.values()){
            SubtasksArray.add(subtask);
        }
        return SubtasksArray;
    }

    //удаление всех задач
    public void clearTasks(){
        tasks.clear();
    }

    public void clearEpics(){
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks(){
        subtasks.clear();
        for (Epic epic : epics.values()){
            epic.clearSubtasks();
            epic.updateStatus();
        }
    }

    //получение по идентификатору
    public Task getTaskById(Integer id){
        return tasks.get(id);
    }

    public Subtask getSubtaskById(Integer id){
        return subtasks.get(id);
    }

    public Epic getEpicById(Integer id){
        return epics.get(id);
    }

    //создание новых задач
    public void makeNewTask(Task task){
        int id = getNewId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void makeNewEpic(Epic task){
        int id = getNewId();
        task.setId(id);
        epics.put(id, task);
    }

    public void makeNewSubtask(Subtask task){
        if (epics.containsKey(task.getEpic().getId())) {
            int id = getNewId();
            task.setId(id);
            subtasks.put(id, task);
            Epic epic = task.getEpic();
            epic.addSubtaskToEpic(task);
            epic.updateStatus();
        }
    }

    //обновление задач
    public void updateTask(Task newTask){
        if (tasks.containsKey(newTask.getId())) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    public void updateSubtask(Subtask newTask){
        if(subtasks.containsKey(newTask.getId())) {
            Epic epic = newTask.getEpic();
            epic.changeSubtask(newTask);
            epic.updateStatus();
            subtasks.put(newTask.getId(), newTask);
        }
    }

    public void updateEpic(Epic newEpic){
        if(epics.containsKey(newEpic.getId())) {
            Epic oldEpic = getEpicById(newEpic.getId());
            if (oldEpic != null) {
                ArrayList<Subtask> epicsSubtasks = oldEpic.getSubtasks();
                for (Subtask subtask : epicsSubtasks) {
                    subtask.setEpic(newEpic);
                    newEpic.addSubtaskToEpic(subtask);
                }
            }
            epics.put(newEpic.getId(), newEpic);
            newEpic.updateStatus();
        }
    }

    //удаление задачи по айди
    public void removeTask(int id){
        tasks.remove(id);
    }

    public void removeSubtask(int id){
        Subtask subtask = getSubtaskById(id);
        if (subtask != null){
            Epic epic = subtask.getEpic();
            epic.removeSubtask(subtask);
            epic.updateStatus();
        }
        subtasks.remove(id);
    }

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

    //получение всех подзадач эпика
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic){
        return epic.getSubtasks();
    }

    //генерация нового айди
    private int getNewId(){
        currentId += 1;
        return currentId;
    }

}


