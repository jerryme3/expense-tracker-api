package expense.tracker.v1.auth.jwt;

import expense.tracker.v1.exception.ForbiddenAccessException;
import expense.tracker.v1.exception.NullValueException;
import org.springframework.security.oauth2.jwt.Jwt;

public final class JwtPrincipal {

	private JwtPrincipal() {
	}

	public static Long userId(Jwt jwt) {
		if (jwt == null) throw new NullValueException("Invalid session token.");

		Object claim = jwt.getClaim("userId");

		if (claim instanceof Number number) return number.longValue();

		throw new ForbiddenAccessException("Token does not contain a valid user ID.");
	}
}
