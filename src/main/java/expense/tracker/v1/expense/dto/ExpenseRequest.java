package expense.tracker.v1.expense.dto;

import expense.tracker.v1.expense.Category;
import expense.tracker.v1.expense.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseRequest(

		@NotBlank(message = "Title is required")
		@Size(max = 100, message = "Title length must not be greater than 100")
		String title,

		@NotNull(message = "Price is required")
		@DecimalMin(value = "0.0", message = "Price must not be negative")
		BigDecimal amount,

		@NotNull(message = "Expense date is required")
		@PastOrPresent(message = "Expense date cannot be in future.")
		Instant expenseDate,

		String notes,

		@NotNull(message = "Invalid category")
		Category category,

		@NotNull(message = "Invalid payment method")
		PaymentMethod paymentMethod
) {

	public ExpenseRequest(String title, BigDecimal price, Instant expenseDate, Category category, PaymentMethod paymentMethod) {
		this(title, price, expenseDate, "", category, paymentMethod);
	}
}
