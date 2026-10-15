package expense.tracker.v1.exception.notfound;

import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends ResourceNotFoundException {

	public EmailNotFoundException(String message) {
		super(message);
	}

	public EmailNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
