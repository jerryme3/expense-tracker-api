package expense.tracker.v1.config;

import expense.tracker.v1.account.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

@Configuration
public class ApplicationConfig {

	private final AccountRepository accountRepository;

	public ApplicationConfig(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> accountRepository.findByEmail(username)
				.map(account -> org.springframework.security.core.userdetails.User.builder()
						.username(account.getEmail())
						.password(account.getPassword())
						.authorities(Collections.emptyList()) // or pass account authorities/roles
						.build())
				.orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + username));
	}

}
