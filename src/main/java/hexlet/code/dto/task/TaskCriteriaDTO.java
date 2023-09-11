package hexlet.code.dto.task;

import lombok.Data;

@Data
public class TaskCriteriaDTO {
    private Long taskStatus;
    private Long executorId;
    private Long labelsId;
    private Long authorId;
}
