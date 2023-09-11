package hexlet.code.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Set;

@Getter
@Setter
public class CreateTaskDTO {
    @NotBlank(message = "Name cannot be empty")
    @Length(min = 1, message = "Task name has to contain at least 1 symbol")
    private String name;

    private String description;

    private Long executorId;

    private Set<Long> labelIds;

    @NotNull(message = "Task status id cannot be empty")
    private Long taskStatusId;

}
