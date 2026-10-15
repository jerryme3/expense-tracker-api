package expense.tracker.v1.expense;

import expense.tracker.v1.user.User;
import expense.tracker.v1.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
public class Expense extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(name = "expense_at", nullable = false)
	private Instant expenseDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false, length = 50)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", length = 50)
	private PaymentMethod paymentMethod;

	@Column(name = "notes", columnDefinition = "TEXT")
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Expense(String title, BigDecimal amount, Instant expenseDate, Category category, PaymentMethod paymentMethod, String notes, User user) {
		this.title = title;
		this.amount = amount;
		this.expenseDate = expenseDate;
		this.category = category;
		this.paymentMethod = paymentMethod;
		this.notes = notes;
		this.user = user;
	}
}
