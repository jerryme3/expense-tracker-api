package expense.tracker.v1.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountRequest(

		@NotBlank(message = "Email must not be blank")
		@Email(message = "Invalid email")
		@Size(max = 80, message = "Email must not be longer than 80 characters")
		String email,

		@NotBlank(message = "Password cannot be blanked.")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.])\\S{8,40}$", message = "Password must have at least one of these characters, with no space: letter, number, and special character.")
		@Size(min = 8, max = 40, message = "Password length must be between or equal to 8 and 40 characters.")
		String password
) {
}
