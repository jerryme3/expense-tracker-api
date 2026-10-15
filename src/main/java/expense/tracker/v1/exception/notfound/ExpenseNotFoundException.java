package expense.tracker.v1.exception.notfound;

import org.springframework.http.HttpStatus;

public class ExpenseNotFoundException extends ResourceNotFoundException {

	public ExpenseNotFoundException(String message) {
		super(message);
	}

	public ExpenseNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
