package expense.tracker.v1.utils;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Objects;
import java.util.function.Function;

/**
 * As the class name says, this interface maps the passed type parameter by available methods
 * @param <T> the entity to map
 * @param <R> the request DTO
 * @param <S> the response DTO
 */
public interface BasicService<T, R, S> {

	default	T fromRequest(R request, Function<R, T> func) {
		Objects.requireNonNull(request);

		return Objects.requireNonNull(func).apply(request);
	}

	default S toResponse(T entity, Function<T, S> func) {
		Objects.requireNonNull(entity);

		return Objects.requireNonNull(func).apply(entity);
	}

	default <A> T anyToEntity(A any, Function<A, T> func) {
		Objects.requireNonNull(any);

		return Objects.requireNonNull(func).apply(any);
	}

	default <ID extends Number, E extends CrudRepository<T, ID>> T getOrThrow(E repository, ID id, RuntimeException e) {
		if (!(id instanceof Long) && !(id instanceof Integer)) throw new IllegalArgumentException("ID must be a Long or Integer");

		return repository.findById(id).orElseThrow(() -> e);
	}
}
