package expense.tracker.v1.exception.invalid;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends InvalidInputException {

	public InvalidEmailException(String message) {
		super(message);
	}

	public InvalidEmailException(String message, HttpStatus status) {
		super(message, status);
	}
}
