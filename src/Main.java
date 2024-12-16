public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        //создание задач
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Составить список дел", "Первая задача", 0);
        taskManager.makeNewTask(task1);
        Task task2 = new Task("Скорректировать список дел", "Вторая задача", 0);
        taskManager.makeNewTask(task2);
        Epic epic1 = new Epic("Спринт 4", "Описание спринта", 0);
        taskManager.makeNewEpic(epic1);
        Subtask subtask1 = new Subtask("Написать код", "Написать код подробно", 0, epic1);
        taskManager.makeNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Протестировать код", "Протестировать код подробно", 0, epic1);
        taskManager.makeNewSubtask(subtask2);
        Epic epic2 = new Epic("Переезд", "Описание переезда", 0);
        taskManager.makeNewEpic(epic2);
        Subtask subtask3 = new Subtask("Собрать коробки", "Собрать все коробки", 0, epic2);
        taskManager.makeNewSubtask(subtask3);
        printAll(taskManager);

        //меняем задачи
        Subtask subtask4 = new Subtask("Написать код", "Написать код подробно", 4, epic1);
        subtask4.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask4);
        Subtask subtask5 = new Subtask("Протестировать код", "Протестировать код подробно", 5, epic1);
        subtask5.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtask6 = new Subtask("Перееезд", "Сложить коробки в машину", 8, epic2);
        subtask6.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask6);
        Epic epic3 = new Epic("Спринт 4 с изменениями", "Описание спринта +", 3);
        epic3.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic3);
        Task task3 = new Task("Составить список дел", "Первая задача с изменениями", 1);
        taskManager.updateTask(task3);
        System.out.println("----------------------");
        System.out.println("После обновления задач");
        printAll(taskManager);

        //удаляем задачи
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

    private static void printAll(TaskManager taskManager){
        for (Task task : taskManager.getAllTasks()){
            System.out.println(task);
        }
        for (Subtask subtask : taskManager.getAllSubtasks()){
            System.out.println(subtask);
        }
        for (Epic epic : taskManager.getAllEpics()){
            System.out.println(epic);
            if (epic.getSubtasks().isEmpty()){
                System.out.println("Подзадачи эпика отсутствуют");
            } else {
                System.out.println("Подзадачи эпика: ");
                for (Subtask subtask : epic.getSubtasks()) {
                    System.out.println(subtask);
                }
            }
        }
    }
}
