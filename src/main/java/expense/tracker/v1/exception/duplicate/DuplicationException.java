package expense.tracker.v1.exception.duplicate;

import expense.tracker.v1.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicationException extends BaseException {
	public DuplicationException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

	public DuplicationException(String message, HttpStatus status) {
		super(message, status);
	}
}
