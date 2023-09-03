package hexlet.code.service;

import hexlet.code.dto.AuthDTO;
import hexlet.code.dto.TokenDTO;
import hexlet.code.model.User;
import hexlet.code.security.JWTUtils;
import hexlet.code.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private UserUtils userUtils;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, JWTUtils jwtUtils, UserUtils userUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userUtils = userUtils;
    }

    public void authenticate(AuthDTO dto) {
        var auth = new UsernamePasswordAuthenticationToken(
                dto.getUsername(),
                dto.getPassword());
        authenticationManager.authenticate(auth);
    }

    public TokenDTO generateTokenDTO(AuthDTO dto) {
        String token = jwtUtils.generateToken(dto.getUsername());
        TokenDTO tokenDTO = new TokenDTO(token);
        return tokenDTO;
    }

    public boolean hasPermissions(User user) {
        return userUtils.getCurrentUser().equals(user);
    }
}
