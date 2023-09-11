package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskRepository taskRepository;
    public void run(ApplicationArguments args) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Testov");
        user.setEmail("test@mail.com");
        user.setPassword("test");

        Task task = new Task();
        task.setName("Test");
        task.setDescription("Test");

        User savedUser;
        if (userRepository.findByEmail("test@mail.com").isEmpty()) {
            savedUser = userRepository.save(user);
            task.setAuthor(savedUser);
            task.setExecutor(savedUser);
        }

        Status savedStatus;
        Status status = new Status("Test");
        if (statusRepository.findByName("Test").isEmpty()) {
            savedStatus = statusRepository.save(status);
            task.setTaskStatus(savedStatus);
        }

        Label savedLabel;
        Label label = new Label("Test");
        if (labelRepository.findByName("Test").isEmpty()) {
            savedLabel = labelRepository.save(label);
            task.setLabels(Set.of(savedLabel));
        }

        if (taskRepository.findByName("Test").isEmpty()) {
            taskRepository.save(task);
        }
    }
}
