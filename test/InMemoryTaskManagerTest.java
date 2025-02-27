
public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    TaskManager initTaskManager() {
        return new InMemoryTaskManager();
    }
}
