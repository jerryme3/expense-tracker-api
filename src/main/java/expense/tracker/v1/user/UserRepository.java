package expense.tracker.v1.user;

import expense.tracker.v1.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);

	@Query("SELECT e FROM User e WHERE e.account.email = :email")
	Optional<User> findByEmail(@Param("email") String email);

	Optional<User> findByAccount(Account account);

}
