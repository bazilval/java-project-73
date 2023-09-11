package hexlet.code.controller;

import hexlet.code.dto.task.CreateTaskDTO;
import hexlet.code.dto.task.ResponseTaskDTO;
import hexlet.code.dto.task.TaskCriteriaDTO;
import hexlet.code.dto.task.TaskFilterDTO;
import hexlet.code.dto.task.UpdateTaskDTO;
import hexlet.code.service.TaskService;
import hexlet.code.handler.FieldErrorHandler;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${base-url}" + "/tasks")
@EnableSpringDataWebSupport
public class TaskController {
    @Autowired
    private TaskService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseTaskDTO> getTasks(TaskCriteriaDTO taskCriteria) {
        List<ResponseTaskDTO> tasks = service.findAll(new TaskFilterDTO(taskCriteria));

        LOGGER.info("Tasks returned!");
        return tasks;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseTaskDTO getTask(@PathVariable("id") Long id) {
        ResponseTaskDTO taskDTO = service.findById(id);

        LOGGER.info("Task with id=" + id + " returned!");
        return taskDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseTaskDTO createTask(@RequestBody @Valid CreateTaskDTO taskDTO, BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseTaskDTO savedTaskDTO = service.save(taskDTO);

        LOGGER.info("Task is saved with id=" + savedTaskDTO.getId());
        return savedTaskDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseTaskDTO updateTask(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateTaskDTO taskDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseTaskDTO updatedTaskDTO = service.update(id, taskDTO);

        LOGGER.info("Task with id=" + id + " updated!");
        return updatedTaskDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStatus(@PathVariable("id") Long id) {
        service.deleteById(id);

        LOGGER.info("Task with id=" + id + " deleted!");
    }
}
