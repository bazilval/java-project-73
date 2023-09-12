package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.ErrorResponse;
import hexlet.code.dto.task.CreateTaskDTO;
import hexlet.code.dto.task.ResponseTaskDTO;
import hexlet.code.dto.task.UpdateTaskDTO;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import hexlet.code.handler.FieldErrorHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
@Tag(name = "Task Management", description = "Task management API")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {
    @Autowired
    private TaskService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found tasks",
                content = {@Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = ResponseTaskDTO.class)))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
    })
    public List<ResponseTaskDTO> getTasks(
            @QuerydslPredicate(root = Task.class) Predicate predicate) {
        List<ResponseTaskDTO> tasks = service.findAll(predicate);

        LOGGER.info("Tasks returned!");
        return tasks;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseTaskDTO.class))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
        @ApiResponse(responseCode = "404", description = "Task with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public ResponseTaskDTO getTask(
            @Parameter(description = "Task id") @PathVariable("id") Long id) {
        ResponseTaskDTO taskDTO = service.findById(id);

        LOGGER.info("Task with id=" + id + " returned!");
        return taskDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseTaskDTO.class))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
        @ApiResponse(responseCode = "403", description = "Permission denied",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "404",
                description = "Resource of task (status, author, label or executor) not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "422", description = "Task data invalid",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public ResponseTaskDTO createTask(
            @Parameter(description = "Task data to create") @RequestBody @Valid CreateTaskDTO taskDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseTaskDTO savedTaskDTO = service.save(taskDTO);

        LOGGER.info("Task is saved with id=" + savedTaskDTO.getId());
        return savedTaskDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseTaskDTO.class))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
        @ApiResponse(responseCode = "403", description = "Permission denied",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "404",
                description = "Task with this id or task resource (status, author, label or executor) not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "422", description = "Label data invalid",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public ResponseTaskDTO updateTask(
            @Parameter(description = "Task id") @PathVariable("id") Long id,
            @Parameter(description = "Task data to update") @RequestBody @Valid UpdateTaskDTO taskDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseTaskDTO updatedTaskDTO = service.update(id, taskDTO);

        LOGGER.info("Task with id=" + id + " updated!");
        return updatedTaskDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted",
                content = @Content
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
        @ApiResponse(responseCode = "403", description = "Permission denied",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "404", description = "Task with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public void deleteStatus(
            @Parameter(description = "Task id") @PathVariable("id") Long id) {
        service.deleteById(id);

        LOGGER.info("Task with id=" + id + " deleted!");
    }
}
