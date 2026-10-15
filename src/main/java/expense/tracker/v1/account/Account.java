package expense.tracker.v1.account;


import expense.tracker.v1.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "accounts")
public class Account extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, unique = true, length = 80)
	private String email;

	@Column(nullable = false, length = 100)
	private String password;

	public Account(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
