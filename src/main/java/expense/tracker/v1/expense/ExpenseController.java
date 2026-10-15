package expense.tracker.v1.expense;

import expense.tracker.v1.auth.jwt.JwtPrincipal;
import expense.tracker.v1.expense.dto.*;
import expense.tracker.v1.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/expense")
@RequiredArgsConstructor
public class ExpenseController {

	private final ExpenseService expenseService;

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<ExpenseResponse>> postExpense(
			@Valid @RequestBody ExpenseRequest request,
			@AuthenticationPrincipal Jwt jwt
			) {
		var resp = expenseService.recordAnExpense(JwtPrincipal.userId(jwt), request);

		if (!resp.successful()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failed("BAD REQUEST"));

		return ResponseEntity.ok(resp);
	}

	@GetMapping("/update")
	public ApiResponse<List<String>> updateChoices() {
		var list = List.of("Title", "Amount", "Expense Date", "Notes", "Category", "Payment Method");

		return ApiResponse.successful(list, "");
	}

	@PutMapping("/update/title")
	public ApiResponse<ExpenseResponse> updateExpenseTitle(
			@Valid @RequestBody UpdateExpenseRequest<String> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return expenseService.updateTitle(JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/amount")
	public ApiResponse<ExpenseResponse> updateExpenseAmount(
			@Valid @RequestBody UpdateExpenseRequest<BigDecimal> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return expenseService.updateAmount(JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/expense-date")
	public ApiResponse<ExpenseResponse> updateExpenseDate(
			@Valid @RequestBody UpdateExpenseRequest<Instant> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return expenseService.updateExpenseDate(JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/notes")
	public ApiResponse<ExpenseResponse> updateExpenseNotes(
			@Valid @RequestBody UpdateExpenseRequest<String> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return expenseService.updateNotes(JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/category")
	public ApiResponse<ExpenseResponse> updateExpenseCategory(
			@Valid @RequestBody UpdateExpenseRequest<Category> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return expenseService.updateExpenseCategory(JwtPrincipal.userId(jwt), request);
	}

	@PutMapping("/update/payment-method")
	public ApiResponse<ExpenseResponse> updateExpensePaymentMethod(
			@Valid @RequestBody UpdateExpenseRequest<PaymentMethod> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return expenseService.updateExpensePaymentMethod(JwtPrincipal.userId(jwt), request);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, String>> deleteExpense(
			@Valid @RequestBody DeleteExpenseRequest request,
			@AuthenticationPrincipal Jwt jwt
	) {
		expenseService.deleteExpense(JwtPrincipal.userId(jwt), request);

		return ResponseEntity.ok(Map.of("message", "Expense has been successfully deleted!"));
	}

	@GetMapping
	public Page<ExpenseResponse> getExpenses(
			@Valid @RequestBody GetAndSortExpenseRequest request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return expenseService.getSort(JwtPrincipal.userId(jwt), request);
	}

}
