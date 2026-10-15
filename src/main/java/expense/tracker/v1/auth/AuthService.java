package expense.tracker.v1.auth;

import expense.tracker.v1.account.Account;
import expense.tracker.v1.account.AccountRepository;
import expense.tracker.v1.account.AccountService;
import expense.tracker.v1.account.dto.AccountRequest;
import expense.tracker.v1.auth.dto.AuthResponse;
import expense.tracker.v1.auth.jwt.JwtService;
import expense.tracker.v1.exception.duplicate.EmailAlreadyExistsException;
import expense.tracker.v1.exception.duplicate.UsernameAlreadyExistsException;
import expense.tracker.v1.exception.invalid.InvalidPasswordException;
import expense.tracker.v1.exception.notfound.EmailNotFoundException;
import expense.tracker.v1.exception.notfound.UserNotFoundException;
import expense.tracker.v1.user.User;
import expense.tracker.v1.user.UserRepository;
import expense.tracker.v1.user.UserService;
import expense.tracker.v1.user.dto.UserRequest;
import expense.tracker.v1.user.dto.UserResponse;
import expense.tracker.v1.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	private final AccountService accountService;
	private final UserService userService;
	private final JwtService jwtService;

	private final PasswordEncoder passwordEncoder;

	public ApiResponse<UserResponse> register(UserRequest request) {
		var accReq  = request.account();
		var account = accountService.fromRequest(accReq, ar -> new Account(ar.email(), passwordEncoder.encode(ar.password())));
		var email   = account.getEmail();
		var name 	= request.username();

		if (accountRepository.existsByEmail(email)) throw new EmailAlreadyExistsException("This email has been registered.");
		if (userRepository.existsByUsername(name))  throw new UsernameAlreadyExistsException("This username is already in use.");

		var user = userService.fromRequest(request, req -> new User(req.username(), req.role(), accountRepository.save(account)));
		var response = userService.toResponse(userRepository.save(user),
				u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.getCreatedAt(), u.getUpdatedAt()));

		return ApiResponse.successful(response, "You're now registered!");
	}

	public ApiResponse<AuthResponse> login(AccountRequest request) {
		var email = request.email();
		var password = request.password();

		if (!accountRepository.existsByEmail(email)) throw new EmailNotFoundException("Invalid email.");

		var account = accountRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException("Invalid email."));

		if (!passwordEncoder.matches(password, account.getPassword())) throw new InvalidPasswordException("Password did not match.");

		var user = userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException("Invalid account information. Cannot find user."));
		var resp = userService.toResponse(user,
				u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.getCreatedAt(), u.getUpdatedAt()));

		var token = jwtService.issueToken(user.getEmail(), user.getId(), user.getRole());
		var authResp = new AuthResponse(token.value(), "Bearer", token.expiresAt(), resp);

		return ApiResponse.successful(authResp, "Logged in successfully!");
	}
}
