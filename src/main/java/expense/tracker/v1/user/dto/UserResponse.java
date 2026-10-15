package expense.tracker.v1.user.dto;

import expense.tracker.v1.user.UserRole;

import java.time.Instant;

public record UserResponse(
		Long id,
		String username,
		String email,
		UserRole role,
		Instant createdAt,
		Instant updatedAt
) {

	public UserResponse(Long id, String username, UserRole role, Instant createdAt, Instant updatedAt) {
		this(id, username, "", role, createdAt, updatedAt);
	}
}
