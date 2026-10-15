package expense.tracker.v1.auth;

import expense.tracker.v1.account.dto.AccountRequest;
import expense.tracker.v1.auth.dto.AuthResponse;
import expense.tracker.v1.user.dto.UserRequest;
import expense.tracker.v1.user.dto.UserResponse;
import expense.tracker.v1.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ApiResponse<UserResponse> register(@Valid @RequestBody UserRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public ApiResponse<AuthResponse> login(@Valid @RequestBody AccountRequest request) {
		return authService.login(request);
	}

}
