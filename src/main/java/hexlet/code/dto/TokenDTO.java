package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class TokenDTO {
    private String jwtToken;

    public TokenDTO(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
