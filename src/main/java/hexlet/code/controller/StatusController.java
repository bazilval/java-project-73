package hexlet.code.controller;

import hexlet.code.dto.ErrorResponse;
import hexlet.code.dto.StatusDTO;
import hexlet.code.service.StatusService;
import hexlet.code.handler.FieldErrorHandler;
import hexlet.code.util.exception.EntityDeleteException;
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
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("${base-url}" + "/statuses")
@Tag(name = "Status Management", description = "Status management API")
@SecurityRequirement(name = "Bearer Authentication")
public class StatusController {
    @Autowired
    private StatusService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all statuses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found statuses",
                content = {@Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = StatusDTO.class)))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
    })
    public List<StatusDTO> getStatuses() {
        List<StatusDTO> statuses = service.findAll();

        LOGGER.info("All statuses returned!");
        return statuses;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get status by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = StatusDTO.class))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
        @ApiResponse(responseCode = "404", description = "Status with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public StatusDTO getStatus(
            @Parameter(description = "Status id") @PathVariable("id") Long id) {
        StatusDTO statusDTO = service.findById(id);

        LOGGER.info("Status with id=" + id + " returned!");
        return statusDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Status created",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = StatusDTO.class))
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
        @ApiResponse(responseCode = "409", description = "Status with this name exists",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "422", description = "Status data invalid",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public StatusDTO createStatus(
            @Parameter(description = "Status data to create") @RequestBody @Valid StatusDTO statusDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        StatusDTO savedStatusDTO = service.save(statusDTO);

        LOGGER.info("Status is saved with id=" + savedStatusDTO.getId());
        return savedStatusDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = StatusDTO.class))
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
        @ApiResponse(responseCode = "404", description = "Status with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "409", description = "Status with this name exists",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "422", description = "Status data invalid",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public StatusDTO updateStatus(
            @Parameter(description = "Status id") @PathVariable("id") Long id,
            @Parameter(description = "Status data to update") @RequestBody @Valid StatusDTO statusDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        StatusDTO updatedStatusDTO = service.update(id, statusDTO);

        LOGGER.info("Status with id=" + id + " updated!");
        return updatedStatusDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Status deleted",
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
        @ApiResponse(responseCode = "404", description = "Status with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "409", description = "Status can not be deleted",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public void deleteStatus(
            @Parameter(description = "Status id") @PathVariable("id") Long id) {
        try {
            service.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDeleteException("Status", id);
        }

        LOGGER.info("Status with id=" + id + " deleted!");
    }
}
