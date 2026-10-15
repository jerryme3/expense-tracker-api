package expense.tracker.v1.utils;

import java.time.Instant;

public record ApiResponse<T>(
		T response,
		boolean successful,
		String message,
		Instant responseTime
) {

	public static <R> ApiResponse<R> failed(String message) {
		return new ApiResponse<>(null, false, message, Instant.now());
	}

	public static <R> ApiResponse<R> successful(R response, String message) {
		return new ApiResponse<>(response, true, message, Instant.now());
	}

}
