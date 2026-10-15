package expense.tracker.v1.exception.duplicate;

import org.springframework.http.HttpStatus;

public class ExpenseTitleAlreadyExists extends DuplicationException {

	public ExpenseTitleAlreadyExists(String message) {
		super(message);
	}

	public ExpenseTitleAlreadyExists(String message, HttpStatus status) {
		super(message, status);
	}
}
