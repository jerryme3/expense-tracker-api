package expense.tracker.v1.expense.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateExpenseRequest<T>(

		@NotNull(message = "Invalid argument")
		T toApply,

		@NotNull(message = "Invalid Task ID")
		Long expenseId
) {
}
