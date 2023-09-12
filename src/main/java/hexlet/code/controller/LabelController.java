package hexlet.code.controller;

import hexlet.code.dto.ErrorResponse;
import hexlet.code.dto.LabelDTO;
import hexlet.code.handler.FieldErrorHandler;
import hexlet.code.service.LabelService;
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
@RequestMapping("${base-url}" + "/labels")
@Tag(name = "Label Management", description = "Label management API")
@SecurityRequirement(name = "Bearer Authentication")
public class LabelController {
    @Autowired
    private LabelService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(LabelController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all labels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found labels",
                content = {@Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = LabelDTO.class)))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content
            )
    })
    public List<LabelDTO> getLabels() {
        List<LabelDTO> labels = service.findAll();

        LOGGER.info("All labels returned!");
        return labels;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get label by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = LabelDTO.class))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content
            ),
        @ApiResponse(responseCode = "404", description = "Label with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public LabelDTO getLabel(
            @Parameter(description = "Label id") @PathVariable("id") Long id) {
        LabelDTO labelDTO = service.findById(id);

        LOGGER.info("Label with id=" + id + " returned!");
        return labelDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create label")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Label created",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = LabelDTO.class))
                }
            ),
        @ApiResponse(responseCode = "401", description = "Unauthorized user can not do this",
                content = @Content
            ),
        @ApiResponse(responseCode = "409", description = "Label with this name exists",
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
    public LabelDTO createLabel(
            @Parameter(description = "Label data to create") @RequestBody @Valid LabelDTO labelDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        LabelDTO savedLabelDTO = service.save(labelDTO);

        LOGGER.info("Label is saved with id=" + savedLabelDTO.getId());
        return savedLabelDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update label")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label updated",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = LabelDTO.class))
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
        @ApiResponse(responseCode = "404", description = "Label with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "409", description = "Label with this name exists",
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
    public LabelDTO updateLabel(
            @Parameter(description = "Label id") @PathVariable("id") Long id,
            @Parameter(description = "Label data to update") @RequestBody @Valid LabelDTO labelDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        LabelDTO updatedLabelDTO = service.update(id, labelDTO);

        LOGGER.info("Label with id=" + id + " updated!");
        return updatedLabelDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete label")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Label deleted",
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
        @ApiResponse(responseCode = "404", description = "Label with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "409", description = "Label can not be deleted",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public void deleteLabel(@PathVariable("id") Long id) {

        try {
            service.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDeleteException("Label", id);
        }

        LOGGER.info("Label with id=" + id + " deleted!");
    }
}
