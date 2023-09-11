package hexlet.code.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String createdAt;
}
