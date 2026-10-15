package expense.tracker.v1.expense;

import expense.tracker.v1.exception.ForbiddenAccessException;
import expense.tracker.v1.exception.NullValueException;
import expense.tracker.v1.exception.date.ExpenseDateAtFutureException;
import expense.tracker.v1.exception.duplicate.ExpenseTitleAlreadyExists;
import expense.tracker.v1.exception.notfound.ExpenseNotFoundException;
import expense.tracker.v1.exception.notfound.UserNotFoundException;
import expense.tracker.v1.expense.dto.*;
import expense.tracker.v1.user.UserRepository;
import expense.tracker.v1.user.UserService;
import expense.tracker.v1.utils.ApiResponse;
import expense.tracker.v1.utils.BasicService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExpenseService implements BasicService<Expense, ExpenseRequest, ExpenseResponse> {

	private final ExpenseRepository expenseRepository;
	private final UserRepository userRepository;

	private final UserService userService;

	public ApiResponse<ExpenseResponse> recordAnExpense(Long userId, ExpenseRequest request) {
		var title = request.title();

		if (expenseRepository.existsByTitleAndUser_Id(title, userId)) throw new ExpenseTitleAlreadyExists("This expense title is already existing in your records.");

		var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Invalid user ID."));
		var note = request.notes() != null && !request.notes().isBlank()
				? request.notes()
				: "You have no notes for this record.";

		var expense  = fromRequest(request, req -> new Expense(req.title(), req.amount(), req.expenseDate(), req.category(), req.paymentMethod(), note, user));
		var response = toResponse(expenseRepository.save(expense),
					r -> new ExpenseResponse(r.getId(), r.getTitle(), r.getAmount(), r.getExpenseDate(), r.getNotes(), r.getCategory(), r.getPaymentMethod(), user.getId())
				);

		return ApiResponse.successful(response, "Expense has been recorded!");
	}

	public ApiResponse<ExpenseResponse> updateTitle(Long userId, UpdateExpenseRequest<String> request) {
		var title = request.toApply();
		var expId = request.expenseId();

		if (title == null || title.isBlank()) throw new NullValueException("Title is required.");
		if (title.length() > 100) throw new IllegalArgumentException("Title length must not be greater than 100.");

		var expense = ownedOrThrow(expId, userId);
		expense.setTitle(title);

		return ApiResponse.successful(update(expense), "Title has been updated!");
	}

	public ApiResponse<ExpenseResponse> updateAmount(Long userId, UpdateExpenseRequest<BigDecimal> request) {
		var amount = request.toApply();
		var expId  = request.expenseId();

		if (amount == null) throw new NullValueException("Title is required.");

		var expense = ownedOrThrow(expId, userId);
		expense.setAmount(amount);

		return ApiResponse.successful(update(expense), "Amount has been updated!");
	}

	public ApiResponse<ExpenseResponse> updateExpenseDate(Long userId, UpdateExpenseRequest<Instant> request) {
		var dateTime = request.toApply();
		var expId = request.expenseId();

		if (dateTime == null) throw new NullValueException("Expense date is required.");
		if (dateTime.isAfter(Instant.now())) throw new ExpenseDateAtFutureException("Expense date cannot be in future.");

		var expense = ownedOrThrow(expId, userId);
		expense.setExpenseDate(dateTime);

		return ApiResponse.successful(update(expense), "Expense date has been updated!");
	}

	public ApiResponse<ExpenseResponse> updateNotes(Long userId, UpdateExpenseRequest<String> request) {
		var expense = ownedOrThrow(request.expenseId(), userId);
		expense.setNotes(request.toApply());

		return ApiResponse.successful(update(expense), "Expense notes has been updated!");
	}

	public ApiResponse<ExpenseResponse> updateExpenseCategory(Long userId, UpdateExpenseRequest<Category> request) {
		var category = request.toApply();

		if (category == null) throw new NullValueException("Invalid category choice.");

		var expense = ownedOrThrow(request.expenseId(), userId);
		expense.setCategory(request.toApply());

		return ApiResponse.successful(update(expense), "Expense category has been updated!");
	}

	public ApiResponse<ExpenseResponse> updateExpensePaymentMethod(Long userId, UpdateExpenseRequest<PaymentMethod> request) {
		var method = request.toApply();

		if (method == null) throw new NullValueException("Invalid payment method choice.");

		var expense = ownedOrThrow(request.expenseId(), userId);
		expense.setPaymentMethod(method);

		return ApiResponse.successful(update(expense), "Expense payment method has been updated!");
	}

	public void deleteExpense(Long userId, DeleteExpenseRequest request) {
		if (expenseRepository.deleteExpense(request.id(), userId) == 0) {
			throw new ExpenseNotFoundException("Invalid expense ID.");
		}
	}

	private ExpenseResponse update(Expense e) {
		Objects.requireNonNull(e);
		expenseRepository.save(e);

		return new ExpenseResponse(
			e.getId(), e.getTitle(), e.getAmount(), e.getExpenseDate(), e.getNotes(), e.getCategory(), e.getPaymentMethod(), e.getUser().getId()
		);
	}

	public Page<ExpenseResponse> getSort(Long userId, GetAndSortExpenseRequest request) {
		var p = request.property();

		var property = (p != null && !p.isBlank()) ? p : "createdAt";
		var order    = (request.ascending()) ? Sort.by(property).ascending() : Sort.by(property).descending();
		var pageable = PageRequest.of(Math.max(0, request.pageNumber()), request.pageSize(), order);

		return expenseRepository
				.findAll(userId, pageable)
				.map(e -> new ExpenseResponse(e.getId(), e.getTitle(), e.getAmount(), e.getExpenseDate(), e.getNotes(), e.getCategory(), e.getPaymentMethod(), e.getUser().getId()));
	}

	private Expense ownedOrThrow(Long expId, Long userId) {
		if (!expenseRepository.existsById(expId)) throw new ExpenseNotFoundException("Invalid expense ID.");

		return expenseRepository
				.findByIdAndUser_Id(expId, userId)
				.orElseThrow(() -> new ForbiddenAccessException("Invalid expense ID."));
	}
}
