package hexlet.code.util.exception;

public class EntityNotFoundByNameException extends RuntimeException {
    public EntityNotFoundByNameException(String entityName, String name) {
        super(entityName + " '" + name + "' not found");
    }
}
