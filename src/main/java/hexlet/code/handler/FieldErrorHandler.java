package hexlet.code.handler;

import hexlet.code.util.exception.BadDataException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class FieldErrorHandler {

    public static void handleErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = collectMessages(bindingResult);
            throw new BadDataException(errorMessage);
        }
    };
    private static String collectMessages(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();

        for (FieldError error : errors) {
            errorMsg.append(error.getField())
                    .append(" - ").append(error.getDefaultMessage())
                    .append(";");
        }

        return errorMsg.toString();
    }
}
