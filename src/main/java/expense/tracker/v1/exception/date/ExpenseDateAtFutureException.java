package expense.tracker.v1.exception.date;

import org.springframework.http.HttpStatus;

public class ExpenseDateAtFutureException extends DateTimeException {
	public ExpenseDateAtFutureException(String message) {
		super(message);
	}

	public ExpenseDateAtFutureException(String message, HttpStatus status) {
		super(message, status);
	}
}
