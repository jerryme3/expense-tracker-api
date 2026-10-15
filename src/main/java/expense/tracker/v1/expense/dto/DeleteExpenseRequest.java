package expense.tracker.v1.expense.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteExpenseRequest(
		@NotNull(message = "Invalid expense ID")
		Long id
) {
}
