package expense.tracker.v1.exception.invalid;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends InvalidInputException {

	public InvalidPasswordException(String message) {
		super(message);
	}

	public InvalidPasswordException(String message, HttpStatus status) {
		super(message, status);
	}
}
