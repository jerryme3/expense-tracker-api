package expense.tracker.v1.exception.handler;

import expense.tracker.v1.exception.BaseException;
import expense.tracker.v1.exception.notfound.ResourceNotFoundException;
import expense.tracker.v1.exception.duplicate.DuplicationException;
import expense.tracker.v1.exception.duplicate.EmailAlreadyExistsException;
import expense.tracker.v1.exception.duplicate.UsernameAlreadyExistsException;
import expense.tracker.v1.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred";
	private static final Map<Class<? extends Exception>, String> DEFAULT_ERROR_MESSAGE =
			Map.ofEntries(
					Map.entry(ResourceNotFoundException.class, "There are no resources with the given information."),
					Map.entry(IllegalArgumentException.class, "Illegal argument."),
					Map.entry(DuplicationException.class, "Cannot have duplicated objects."),
					Map.entry(UsernameAlreadyExistsException.class, "This username is already taken."),
					Map.entry(EmailAlreadyExistsException.class, "This email is already registered."),
					Map.entry(Exception.class, INTERNAL_ERROR_MESSAGE)
			);

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalException(IllegalArgumentException e) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
		return ResponseEntity
				.status(e.getStatus())
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	private	static String catchNullMessage(Exception e) {
		return e.getMessage() != null && !e.getMessage().isBlank()
				? e.getMessage()
				: DEFAULT_ERROR_MESSAGE.getOrDefault(e.getClass(), INTERNAL_ERROR_MESSAGE);
	}

}
