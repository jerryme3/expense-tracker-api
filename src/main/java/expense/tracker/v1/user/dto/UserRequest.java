package expense.tracker.v1.user.dto;

import expense.tracker.v1.account.dto.AccountRequest;
import expense.tracker.v1.user.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(

		@NotBlank(message = "Username must not be blank")
		@Size(min = 5, max = 50, message = "Username length must be between 5 and 50")
		String username,

		@NotNull(message = "Role is required")
		UserRole role,

		@Valid
		@NotNull(message = "Account information is required")
		AccountRequest account
) {
}
