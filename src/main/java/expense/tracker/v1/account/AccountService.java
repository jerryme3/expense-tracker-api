package expense.tracker.v1.account;

import expense.tracker.v1.account.dto.AccountRequest;
import expense.tracker.v1.account.dto.AccountResponse;
import expense.tracker.v1.utils.BasicService;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements BasicService<Account, AccountRequest, AccountResponse> {

	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}
}
