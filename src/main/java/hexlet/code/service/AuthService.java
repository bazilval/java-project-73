package hexlet.code.service;

import hexlet.code.dto.AuthDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.security.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private UserRepository userRepository;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, JWTUtils jwtUtils, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    public void authenticate(AuthDTO dto) {
        var auth = new UsernamePasswordAuthenticationToken(
                dto.getUsername(),
                dto.getPassword());
        authenticationManager.authenticate(auth);
    }

    public String generateToken(AuthDTO dto) {
        String token = jwtUtils.generateToken(dto.getUsername());
        return token;
    }

    public boolean hasPermissions(User user) {
        return getCurrentUser().equals(user);
    }
    public boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
