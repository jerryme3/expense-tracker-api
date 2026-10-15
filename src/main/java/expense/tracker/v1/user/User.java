package expense.tracker.v1.user;

import expense.tracker.v1.utils.BaseEntity;
import expense.tracker.v1.account.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false, unique = true)
	private Account account;

	public User(String username, UserRole role, Account account) {
		this.username = username;
		this.role = role;
		this.account = account;
	}

	public String getEmail() {
		return this.account.getEmail();
	}

}
