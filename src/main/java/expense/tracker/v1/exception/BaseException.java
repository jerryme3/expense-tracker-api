package expense.tracker.v1.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}


}
