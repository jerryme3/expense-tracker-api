package expense.tracker.v1.expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	Optional<Expense> findByIdAndUser_Id(Long expenseId, Long userId);

	@Transactional
	@Modifying
	@Query("DELETE FROM Expense e WHERE e.id = :expenseId AND e.user.id = :userId")
	int deleteExpense(@Param("expenseId") Long expenseId, @Param("userId") Long userId);

	@Query(value = "SELECT e FROM Expense e JOIN FETCH e.user u WHERE u.id = :userId",
			countQuery = "SELECT COUNT(e) FROM Expense e WHERE e.user.id = :userId")
	Page<Expense> findAll(@Param("userId") Long userId, Pageable pageable);

	boolean existsByTitleAndUser_Id(String title, Long userId);

}
