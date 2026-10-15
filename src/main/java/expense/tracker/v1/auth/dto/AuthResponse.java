package expense.tracker.v1.auth.dto;

import expense.tracker.v1.user.dto.UserResponse;

import java.time.Instant;

public record AuthResponse(
		String token,
		String tokenType,
		Instant expiration,
		UserResponse user
) {
}
