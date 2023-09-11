package hexlet.code.util.exception;

public class EntityDeleteException extends RuntimeException {
    public EntityDeleteException(String entityName, Long id) {
        super(entityName + " with id=" + id + " can not be deleted!");
    }
}
