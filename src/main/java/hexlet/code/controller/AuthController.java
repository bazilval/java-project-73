package hexlet.code.controller;

import hexlet.code.dto.AuthDTO;
import hexlet.code.dto.TokenDTO;
import hexlet.code.service.AuthService;
import hexlet.code.util.UserErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${base-url}")
public class AuthController {
    @Autowired
    private AuthService authService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public TokenDTO login(@RequestBody AuthDTO authDTO) {
        authService.authenticate(authDTO);

        LOGGER.info("Authentication of " + authDTO.getUsername() + " successful!");
        return authService.generateTokenDTO(authDTO);
    }

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(AuthenticationException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis());

        LOGGER.info(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
