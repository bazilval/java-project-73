package hexlet.code.controller;

import hexlet.code.dto.AuthDTO;
import hexlet.code.service.AuthService;
import hexlet.code.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${base-url}")
@Tag(name = "Auth Management", description = "Auth management API")
public class AuthController {
    @Autowired
    private AuthService authService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "Login with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful, token returned",
                    content = @Content
            ),
        @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
                    }
            )
    })
            @PostMapping("/login")
    public String login(
            @Parameter(description = "Auth credentials") @RequestBody AuthDTO authDTO) {
        authService.authenticate(authDTO);

        LOGGER.info("Authentication of " + authDTO.getEmail() + " successful!");
        return authService.generateToken(authDTO);
    }
}
