package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ResponseUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Timestamp createdAt;
}
