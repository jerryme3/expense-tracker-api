package expense.tracker.v1.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenAccessException extends BaseException {
	public ForbiddenAccessException(String message) {
		super(message, HttpStatus.FORBIDDEN);
	}

	public ForbiddenAccessException(String message, HttpStatus status) {
		super(message, status);
	}
}
