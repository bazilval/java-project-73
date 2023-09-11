package hexlet.code.specification;

import hexlet.code.dto.task.TaskFilterDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class TaskSpecifications {

    public static Specification<Task> toSpecification(TaskFilterDTO taskFilter) {
        Specification specification = Specification
                .where(taskFilter.getTaskStatus().map(TaskSpecifications::hasTaskStatus).orElse(null))
                .and(taskFilter.getExecutorId().map(TaskSpecifications::hasExecutor).orElse(null))
                .and(taskFilter.getLabelsId().map(TaskSpecifications::hasLabel).orElse(null))
                .and(taskFilter.getAuthorId().map(TaskSpecifications::hasAuthor).orElse(null));
        return specification;
    }
    public static Specification<Task> hasTaskStatus(Long id) {
        return (root, query, cb) -> cb.equal(root.get("taskStatus").get("id"), id);
    }

    public static Specification<Task> hasExecutor(Long id) {
        return (root, query, cb) -> cb.equal(root.get("executor").get("id"), id);
    }

    public static Specification<Task> hasAuthor(Long id) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), id);
    }

    public static Specification<Task> hasLabel(Long id) {
        return (root, query, cb) -> {
            Join<Task, Label> labels = root.join("labels");
            return cb.equal(labels.get("id"), id);
        };
    }
}
