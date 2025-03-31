import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void makeCustomScript(TaskManager taskManager, boolean removeTasks) {

        System.out.println("Поехали!");
        //создание задач
        Task task1 = new Task("Составить список дел", "Первая задача", TaskStatus.NEW, LocalDateTime.of(2025, 1, 20, 8, 30), Duration.ofMinutes(30));
        taskManager.makeNewTask(task1);
        Task task2 = new Task("Скорректировать список дел", "Вторая задача", TaskStatus.NEW, LocalDateTime.of(2025, 2, 20, 8, 30), Duration.ofMinutes(35));
        taskManager.makeNewTask(task2);
        Epic epic1 = new Epic("Спринт 4", "Описание спринта");
        int epicId1 = taskManager.makeNewEpic(epic1);
        Subtask subtask1 = new Subtask("Написать код", "Написать код подробно", TaskStatus.NEW, taskManager.getEpic(epicId1), null, Duration.ofMinutes(100));
        taskManager.makeNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Протестировать код", "Протестировать код подробно", TaskStatus.NEW, taskManager.getEpic(epicId1), LocalDateTime.of(2025, 1, 20, 16, 30), Duration.ofMinutes(60));
        taskManager.makeNewSubtask(subtask2);
        Epic epic2 = new Epic("Переезд", "Описание переезда");
        int epicId2 = taskManager.makeNewEpic(epic2);
        Subtask subtask3 = new Subtask("Собрать коробки", "Собрать все коробки", TaskStatus.NEW, taskManager.getEpic(epicId2), LocalDateTime.of(2026, 1, 20, 8, 30), Duration.ofMinutes(30));
        taskManager.makeNewSubtask(subtask3);
        printAll(taskManager);

        //меняем задачи
        Subtask subtask4 = new Subtask("Написать код", "Написать код подробно", TaskStatus.DONE, epic1, 4, LocalDateTime.of(2025, 1, 20, 8, 30), Duration.ofMinutes(30));
        taskManager.updateSubtask(subtask4);
        Subtask subtask5 = new Subtask("Протестировать код", "Протестировать код подробно", TaskStatus.IN_PROGRESS, epic1, 5, LocalDateTime.of(2025, 1, 22, 8, 30), Duration.ofMinutes(30));
        taskManager.updateSubtask(subtask5);
        Epic epic3 = new Epic("Спринт 4 с изменениями", "Описание спринта +", 3);
        taskManager.updateEpic(epic3);
        Task task3 = new Task("Составить список дел", "Первая задача с изменениями", TaskStatus.NEW, 1, LocalDateTime.of(2025, 1, 20, 8, 30), Duration.ofMinutes(30));
        taskManager.updateTask(task3);
        System.out.println("----------------------");
        System.out.println("После обновления задач");
        printAll(taskManager);

        //создаем еще один таскменеджер из файла, создаем его до печати всех задач, так как печать меняет историю просмотров
        TaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(new File("resources/task.csv"));
        System.out.println("----------------------");
        System.out.println("Сохранили этот результат в отдельный менеджер задач (Менеджер v2)");

        if (removeTasks) {
            taskManager.removeTask(1);
            taskManager.removeTask(10);
            taskManager.removeSubtask(5);
            taskManager.removeEpic(6);
            System.out.println("----------------------");
            System.out.println("После удаления задач");
            printAll(taskManager);

            //очищаем задачи и подзадачи
            taskManager.clearTasks();
            taskManager.clearSubtasks();
            System.out.println("----------------------");
            System.out.println("После удаления всех задач и подзадач");
            printAll(taskManager);
        }
        //печатаем задачи из менеджера, сохраненного из файла
        System.out.println("----------------------");
        System.out.println("Выводим задачи, сохраненные в Менеджер v2");
        printAll(taskManagerFromFile);
    }


    private static void printAll(TaskManager taskManager) {
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
            if (taskManager.getSubtasksByEpic(epic).isEmpty()) {
                System.out.println("Подзадачи эпика отсутствуют");
            } else {
                System.out.println("Подзадачи эпика: ");
                for (Subtask subtask : taskManager.getSubtasksByEpic(epic)) {
                    System.out.println(subtask);
                }
            }
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Приоритеты:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task + " " + task.getStartTime());
        }
    }
}
