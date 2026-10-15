package expense.tracker.v1.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasicServiceTest {

	private final BasicService<String, String, String> service = new BasicService<>() {};

	@Test
	void mapsRequestAndResponse() {
		assertEquals("mapped:request", service.fromRequest("request", value -> "mapped:" + value));
		assertEquals("response", service.toResponse("entity", value -> "response"));
		assertEquals("entity", service.anyToEntity("input", value -> "entity"));
	}

	@Test
	void rejectsNullInputsAndMappers() {
		assertThrows(NullPointerException.class, () -> service.fromRequest(null, value -> value));
		assertThrows(NullPointerException.class, () -> service.fromRequest("request", null));
		assertThrows(NullPointerException.class, () -> service.toResponse(null, value -> value));
		assertThrows(NullPointerException.class, () -> service.<String>anyToEntity(null, value -> value));
	}

	@Test
	void returnsExistingEntity() {
		@SuppressWarnings("unchecked")
		CrudRepository<String, Long> repository = mock(CrudRepository.class);
		when(repository.findById(7L)).thenReturn(Optional.of("found"));

		assertEquals("found", service.getOrThrow(repository, 7L, new IllegalStateException()));
	}

	@Test
	void throwsProvidedExceptionWhenEntityDoesNotExist() {
		@SuppressWarnings("unchecked")
		CrudRepository<String, Long> repository = mock(CrudRepository.class);
		var expected = new IllegalStateException("missing");
		when(repository.findById(9L)).thenReturn(Optional.empty());

		assertEquals(expected, assertThrows(IllegalStateException.class,
				() -> service.getOrThrow(repository, 9L, expected)));
	}

	@Test
	void rejectsUnsupportedIdentifierTypes() {
		@SuppressWarnings("unchecked")
		CrudRepository<String, Number> repository = mock(CrudRepository.class);

		assertThrows(IllegalArgumentException.class,
				() -> service.getOrThrow(repository, 1.5, new IllegalStateException()));
	}
}