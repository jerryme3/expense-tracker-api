package expense.tracker.v1.account.dto;

import java.time.Instant;

public record AccountResponse(
		Long id,
		String email,
		Instant createdAt,
		Instant updatedAt
) {
}
