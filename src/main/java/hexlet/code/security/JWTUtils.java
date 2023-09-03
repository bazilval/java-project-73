package hexlet.code.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
@Component
public class JWTUtils {
    @Value("${jwt-secret}")
    private String secret;

    @Value("${jwt-token-alive-minutes}")
    private int tokenAliveMinutes;

    @Value("${spring.application.name}")
    private String issuer;

    @Autowired
    private JwtEncoder encoder;

    public String generateToken(String username) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(tokenAliveMinutes, ChronoUnit.MINUTES))
                .subject(username)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
