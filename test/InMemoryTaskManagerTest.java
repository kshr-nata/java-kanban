import java.io.File;
import java.io.IOException;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    TaskManager initTaskManager() {
        return new InMemoryTaskManager();
    }
}
