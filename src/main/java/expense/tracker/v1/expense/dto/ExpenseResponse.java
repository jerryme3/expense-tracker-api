package expense.tracker.v1.expense.dto;

import expense.tracker.v1.expense.Category;
import expense.tracker.v1.expense.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseResponse(
		Long id,
		String title,
		BigDecimal price,
		Instant expenseDate,
		String notes,
		Category category,
		PaymentMethod paymentMethod,
		Long userId
) {
}
