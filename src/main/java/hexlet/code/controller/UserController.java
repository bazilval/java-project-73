package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import hexlet.code.dto.ErrorResponse;
import hexlet.code.dto.user.CreateUserDTO;
import hexlet.code.dto.user.ResponseUserDTO;
import hexlet.code.dto.user.UpdateUserDTO;
import hexlet.code.service.UserService;
import hexlet.code.handler.FieldErrorHandler;

import hexlet.code.util.exception.EntityDeleteException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

@RestController
@RequestMapping("${base-url}" + "/users")
@Tag(name = "User Management", description = "User management API")
@SecurityScheme(type = SecuritySchemeType.OAUTH2)
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private Rollbar rollbar;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found users",
                content = {@Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = ResponseUserDTO.class)))
                }
            )
    })
    public List<ResponseUserDTO> getUsers() {
        List<ResponseUserDTO> users = service.findAll();

        LOGGER.info("All users returned!");
        return users;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseUserDTO.class))
                }
            ),
        @ApiResponse(responseCode = "404", description = "User with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public ResponseUserDTO getUser(
            @Parameter(description = "User id") @PathVariable("id") Long id) {
        ResponseUserDTO userDTO = service.findById(id);

        LOGGER.info("User with id=" + id + " returned!");
        return userDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseUserDTO.class))
                }
            ),
        @ApiResponse(responseCode = "409", description = "User with this email exists",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "422", description = "User data invalid",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public ResponseUserDTO createUser(
            @Parameter(description = "User data to create") @RequestBody @Valid CreateUserDTO userDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseUserDTO savedUserDTO = service.save(userDTO);

        LOGGER.info("User is saved with id=" + savedUserDTO.getId());
        return savedUserDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseUserDTO.class))
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
        @ApiResponse(responseCode = "404", description = "User with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "409", description = "User with this email exists",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "422", description = "User data invalid",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public ResponseUserDTO updateUser(
            @Parameter(description = "User id") @PathVariable("id") Long id,
            @Parameter(description = "User data to update") @RequestBody @Valid UpdateUserDTO userDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseUserDTO updatedUserDTO = service.update(id, userDTO);

        LOGGER.info("User with id=" + id + " updated!");
        return updatedUserDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted",
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
        @ApiResponse(responseCode = "404", description = "User with this id not found",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            ),
        @ApiResponse(responseCode = "409", description = "User can not be deleted",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
                }
            )
    })
    public void deleteUser(
            @Parameter(description = "User id") @PathVariable("id") Long id) {

        try {
            service.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDeleteException("User", id);
        }

        LOGGER.info("User with id=" + id + " deleted!");
    }
}
