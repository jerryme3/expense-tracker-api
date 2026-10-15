package expense.tracker.v1.exception.date;

import expense.tracker.v1.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DateTimeException extends BaseException {
	public DateTimeException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

	public DateTimeException(String message, HttpStatus status) {
		super(message, status);
	}
}
