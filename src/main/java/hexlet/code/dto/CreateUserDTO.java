package hexlet.code.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;

@Getter
@Setter
public class CreateUserDTO {
    private Long id;
    @NotBlank(message = "Firstname cannot be empty")
    @Length(min = 1, message = "Firstname has to contain at least 1 symbol")
    private String firstName;

    @NotBlank(message = "Lastname cannot be empty")
    @Length(min = 1, message = "Lastname has to contain at least 1 symbol")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email has to be correct")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Length(min = 3, message = "Password has to contain at least 3 symbols")
    private String password;

    private Timestamp createdAt;
}
