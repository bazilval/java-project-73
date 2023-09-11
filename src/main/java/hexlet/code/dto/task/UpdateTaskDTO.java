package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class UpdateTaskDTO {
    @Length(min = 1, message = "Name has to contain at least 1 symbol")
    private JsonNullable<String> name;
    private JsonNullable<String> description;
    private JsonNullable<Long> authorId;
    private JsonNullable<Long> executorId;
    private JsonNullable<Long> taskStatusId;
    private JsonNullable<Set<Long>> labelIds;
}
