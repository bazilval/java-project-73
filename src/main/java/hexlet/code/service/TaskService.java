package hexlet.code.service;

import hexlet.code.dto.task.CreateTaskDTO;
import hexlet.code.dto.task.ResponseTaskDTO;
import hexlet.code.dto.task.TaskFilterDTO;
import hexlet.code.dto.task.UpdateTaskDTO;
import hexlet.code.mapper.TaskMapperImpl;
import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecifications;
import hexlet.code.util.exception.EntityNotFoundByNameException;
import hexlet.code.util.exception.EntityNotFoundException;
import hexlet.code.util.exception.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private TaskMapperImpl mapper;

    private final String entityName = "Task";

    @Transactional
    public ResponseTaskDTO save(CreateTaskDTO dto) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        Task task = mapper.map(dto);
        task.setAuthor(authService.getCurrentUser());

        Long executorId = dto.getExecutorId();
        if (executorId != null) {
            User executor = userRepository.findById(executorId)
                    .orElseThrow(() -> new EntityNotFoundException("Executor", executorId));
            task.setExecutor(executor);
        }

        Long statusId = dto.getTaskStatusId();
        if (statusId != null) {
            Status status = statusRepository.findById(statusId)
                    .orElseThrow(() -> new EntityNotFoundException("Status", statusId));
            task.setTaskStatus(status);
        }

        Set<Long> labelsId = dto.getLabelIds();
        if (labelsId != null) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : labelsId) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new EntityNotFoundException("Label", labelId));
                labels.add(label);
            }

            task.setLabels(labels);
        }

        taskRepository.save(task);
        return mapper.map(task);
    }

    public List<ResponseTaskDTO> findAll(TaskFilterDTO taskFilter) {
        List<Task> tasks = taskRepository.findAll(TaskSpecifications.toSpecification(taskFilter));

        if (tasks.isEmpty()) {
            return List.of();
        }

        List<ResponseTaskDTO> tasksDTO = tasks.stream()
                .map(task -> mapper.map(task))
                .collect(Collectors.toList());

        return tasksDTO;
    }

    public ResponseTaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        return mapper.map(task);
    }

    public ResponseTaskDTO findByName(String name) {
        Task task = taskRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundByNameException(entityName, name));

        return mapper.map(task);
    }

    @Transactional
    public ResponseTaskDTO update(Long id, UpdateTaskDTO data) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        if (data.getExecutorId() != null) {
            Long executorId = data.getExecutorId().get();
            User executor = userRepository.findById(executorId)
                    .orElseThrow(() -> new EntityNotFoundException("Executor", executorId));
            task.setExecutor(executor);
        }

        if (data.getTaskStatusId() != null) {
            Long statusId = data.getTaskStatusId().get();
            Status status = statusRepository.findById(statusId)
                    .orElseThrow(() -> new EntityNotFoundException("Status", statusId));
            task.setTaskStatus(status);
        }

        if (data.getLabelIds() != null) {
            Set<Long> labelsId = data.getLabelIds().get();
            Set<Label> labels = new HashSet<>();
            for (Long labelId : labelsId) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new EntityNotFoundException("Label", labelId));
                labels.add(label);
            }

            task.setLabels(labels);
        }

        mapper.update(data, task);
        Task updatedtask = taskRepository.findById(id).get();

        return mapper.map(updatedtask);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        if (!authService.hasPermissions(task.getAuthor())) {
            throw new PermissionDeniedException();
        }

        taskRepository.delete(task);
    }

}
