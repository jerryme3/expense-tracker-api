package expense.tracker.v1.expense.dto;

import jakarta.validation.constraints.NotNull;

public record GetAndSortExpenseRequest(

		@NotNull(message = "Invalid page number")
		Integer pageNumber,

		@NotNull(message = "Invalid page size")
		Integer pageSize,

		@NotNull(message = "Invalid property")
		String property,

		@NotNull(message = "Invalid order")
		Boolean ascending

) {
}
