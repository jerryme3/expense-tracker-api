package expense.tracker.v1.exception.notfound;

import expense.tracker.v1.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

	public ResourceNotFoundException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

	public ResourceNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
