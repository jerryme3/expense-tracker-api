package expense.tracker.v1;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExpenseSecurityIntegrationTest extends ApiIntegrationTestSupport {

	@Test
	void rejectsMissingOrInvalidBearerToken() throws Exception {
		String payload = """
				{"title":"Coffee","amount":3.50,"expenseDate":"2026-01-01T00:00:00Z","category":"FOOD_AND_DINING","paymentMethod":"CASH"}
				""";

		mockMvc.perform(post("/api/v1/expense/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer not-a-valid-jwt")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void preventsUsersFromChangingOrDeletingAnotherUsersExpense() throws Exception {
		String ownerToken = registerAndLogin(unique("owner"), unique("owner") + "@example.test");
		String otherToken = registerAndLogin(unique("other"), unique("other") + "@example.test");
		String payload = """
				{"title":"Shared-boundary-check","amount":5.00,"expenseDate":"2026-01-01T00:00:00Z","category":"FOOD_AND_DINING","paymentMethod":"CASH"}
				""";

		var created = mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer " + ownerToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isOk())
				.andReturn();
		long id = ((Number) com.jayway.jsonpath.JsonPath.read(
				created.getResponse().getContentAsString(), "$.response.id")).longValue();

		mockMvc.perform(put("/api/v1/expense/update/title")
						.header("Authorization", "Bearer " + otherToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"stolen\",\"expenseId\":" + id + "}"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/api/v1/expense/delete")
						.header("Authorization", "Bearer " + otherToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id\":" + id + "}"))
				.andExpect(status().isConflict());
	}
}
