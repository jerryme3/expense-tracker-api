package expense.tracker.v1.auth.jwt;


import expense.tracker.v1.config.JwtConfig;
import expense.tracker.v1.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;
	private final Duration expiration;

	public JwtService(
			JwtEncoder jwtEncoder,
			@Value("${app.jwt.expiration:PT15M}") Duration expiration
	) {
		if (expiration.compareTo(Duration.ofSeconds(1)) < 0) {
			throw new IllegalArgumentException("JWT expiration must be at least one second.");
		}
		this.jwtEncoder = jwtEncoder;
		this.expiration = expiration;
	}

	public IssuedToken issueToken(String email, Long userId, UserRole role) {
		Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		Instant expiresAt = issuedAt.plus(expiration).truncatedTo(ChronoUnit.SECONDS);

		var claims = JwtClaimsSet.builder()
				.issuer(JwtConfig.ISSUER)
				.subject(email)
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.claim("userId", userId)
				.claim("roles", List.of(role.name()))
				.build();

		var headers = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();

		return new IssuedToken(token, expiresAt);
	}

	public record IssuedToken(String value, Instant expiresAt) {
	}
}
