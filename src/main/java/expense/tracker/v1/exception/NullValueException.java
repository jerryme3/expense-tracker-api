package expense.tracker.v1.exception;

import org.springframework.http.HttpStatus;

public class NullValueException extends BaseException {

	public NullValueException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

	public NullValueException(String message, HttpStatus status) {
		super(message, status);
	}
}
