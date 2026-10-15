package expense.tracker.v1.exception.invalid;

import expense.tracker.v1.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidInputException extends BaseException {

	public InvalidInputException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

	public InvalidInputException(String message, HttpStatus status) {
		super(message, status);
	}
}
