package hexlet.code.util.exception;

public class PermissionDeniedException extends RuntimeException  {
    public PermissionDeniedException() {
        super("Permission denied!");
    }
}
