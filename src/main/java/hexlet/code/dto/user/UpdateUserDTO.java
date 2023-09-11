package hexlet.code.dto.user;

import org.hibernate.validator.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UpdateUserDTO {
    @Length(min = 1, message = "Firstname has to contain at least 1 symbol")
    private JsonNullable<String> firstName;

    @Length(min = 1, message = "Lastname has to contain at least 1 symbol")
    private JsonNullable<String> lastName;

    @Email(message = "Email has to be correct")
    private JsonNullable<String> email;

    @Length(min = 3, message = "Password has to contain at least 3 symbols")
    private JsonNullable<String> password;
}
