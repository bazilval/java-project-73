package hexlet.code.dto.task;

import lombok.Data;
import java.util.Optional;

@Data
public class TaskFilterDTO {
    private Optional<Long> taskStatus;
    private Optional<Long> executorId;
    private Optional<Long> labelsId;
    private Optional<Long> authorId;

    public TaskFilterDTO(TaskCriteriaDTO criteria) {
        this.taskStatus = Optional.ofNullable(criteria.getTaskStatus());
        this.executorId = Optional.ofNullable(criteria.getExecutorId());
        this.labelsId = Optional.ofNullable(criteria.getLabelsId());
        this.authorId = Optional.ofNullable(criteria.getAuthorId());
    }
}
