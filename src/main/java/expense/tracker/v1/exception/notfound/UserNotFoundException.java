package expense.tracker.v1.exception.notfound;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ResourceNotFoundException {

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
