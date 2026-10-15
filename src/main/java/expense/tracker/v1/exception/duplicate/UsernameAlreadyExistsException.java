package expense.tracker.v1.exception.duplicate;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends DuplicationException {

	public UsernameAlreadyExistsException(String message) {
		super(message);
	}

	public UsernameAlreadyExistsException(String message, HttpStatus status) {
		super(message, status);
	}
}
