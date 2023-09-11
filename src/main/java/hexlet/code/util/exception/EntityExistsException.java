package hexlet.code.util.exception;

public class EntityExistsException extends RuntimeException {
    public EntityExistsException(String entityName, String name) {
        super(entityName + " '" + name + "' already exists!");
    }
}
