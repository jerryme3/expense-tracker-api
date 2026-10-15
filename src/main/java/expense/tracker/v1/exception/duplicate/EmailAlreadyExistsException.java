package expense.tracker.v1.exception.duplicate;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends DuplicationException {
	public EmailAlreadyExistsException(String message) {
		super(message);
	}

	public EmailAlreadyExistsException(String message, HttpStatus status) {
		super(message, status);
	}
}
