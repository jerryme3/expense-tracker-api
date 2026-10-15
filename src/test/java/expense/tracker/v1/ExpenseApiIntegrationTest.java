package expense.tracker.v1;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExpenseApiIntegrationTest extends ApiIntegrationTestSupport {

	@Test
	void createsExpenseAndReadsPersistedDataWithDefaultNotes() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");
		String title = unique("Lunch");
		String payload = expensePayload(title, "  ", "12.50", "FOOD_AND_DINING", "CASH");

		MvcResult created = mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.successful").value(true))
				.andExpect(jsonPath("$.response.title").value(title))
				.andExpect(jsonPath("$.response.notes").value("You have no notes for this record."))
				.andReturn();

		long expenseId = ((Number) com.jayway.jsonpath.JsonPath.read(
				created.getResponse().getContentAsString(), "$.response.id")).longValue();

		mockMvc.perform(get("/api/v1/expense")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(pageRequest(0, 10, "title", true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].id").value(expenseId))
				.andExpect(jsonPath("$.content[0].title").value(title))
				.andExpect(jsonPath("$.content[0].notes").value("You have no notes for this record."));
	}

	@Test
	void updatesEveryExpenseFieldAndVerifiesPersistence() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");
		long id = createExpense(token, unique("Taxi"), "8.00", "TRANSPORTATION", "CASH");

		update(token, "/update/title", id, "\"" + unique("Ride") + "\"");
		update(token, "/update/amount", id, "21.75");
		Instant date = Instant.now().minusSeconds(3600).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
		update(token, "/update/expense-date", id, "\"" + date + "\"");
		update(token, "/update/notes", id, "\"Updated note\"");
		update(token, "/update/category", id, "\"UTILITIES\"");
		update(token, "/update/payment-method", id, "\"DEBIT_CARD\"");

		mockMvc.perform(get("/api/v1/expense")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(pageRequest(0, 10, "title", true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].price").value(21.75))
				.andExpect(jsonPath("$.content[0].expenseDate").value(date.toString()))
				.andExpect(jsonPath("$.content[0].notes").value("Updated note"))
				.andExpect(jsonPath("$.content[0].category").value("UTILITIES"))
				.andExpect(jsonPath("$.content[0].paymentMethod").value("DEBIT_CARD"));
	}

	@Test
	void validatesExpenseInputAndRejectsDuplicateTitle() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");
		String title = unique("Expense");
		createExpense(token, title, "4.00", "FOOD_AND_DINING", "CASH");

		mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(expensePayload(title, "note", "4.00", "FOOD_AND_DINING", "CASH")))
				.andExpect(status().isConflict());

		mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(expensePayload(unique("Negative"), "note", "-1", "FOOD_AND_DINING", "CASH")))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(expensePayload(unique("Future"), "note", "1", "FOOD_AND_DINING", "CASH",
								Instant.now().plusSeconds(3600))))
				.andExpect(status().isBadRequest());

		mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"", "amount":1, "expenseDate":"%s", "category":"UNKNOWN", "paymentMethod":"CASH"}
								""".formatted(Instant.now().minusSeconds(60))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void rejectsInvalidUpdateValuesAndMissingRequiredFields() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");
		long id = createExpense(token, unique("Update"), "8.00", "FOOD_AND_DINING", "CASH");
		String updateUrl = "/api/v1/expense/update/";

		mockMvc.perform(put(updateUrl + "title")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"\",\"expenseId\":" + id + "}"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put(updateUrl + "title")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"" + "t".repeat(101) + "\",\"expenseId\":" + id + "}"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put(updateUrl + "expense-date")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"" + Instant.now().plusSeconds(3600) + "\",\"expenseId\":" + id + "}"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put(updateUrl + "amount")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":null,\"expenseId\":" + id + "}"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put(updateUrl + "category")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":null,\"expenseId\":" + id + "}"))
				.andExpect(status().isBadRequest());

		mockMvc.perform(put(updateUrl + "notes")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":\"updated\",\"expenseId\":null}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listsOnlyOwnExpensesAndSupportsPaginationAndSorting() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");
		String otherToken = registerAndLogin(unique("user"), unique("member") + "@example.test");
		createExpense(token, "Zebra " + unique(""), "2.00", "SHOPPING", "CASH");
		createExpense(token, "Apple " + unique(""), "1.00", "SHOPPING", "CASH");
		createExpense(otherToken, "Private " + unique(""), "9.00", "SHOPPING", "CASH");

		mockMvc.perform(get("/api/v1/expense")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(pageRequest(0, 1, "title", true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.totalElements").value(2))
				.andExpect(jsonPath("$.content[0].title").value(org.hamcrest.Matchers.startsWith("Apple ")));
	}

	@Test
	void listsEmptyPageAndNormalizesNegativePageNumber() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");

		mockMvc.perform(get("/api/v1/expense")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(pageRequest(-1, 10, "", true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.number").value(0))
				.andExpect(jsonPath("$.totalElements").value(0))
				.andExpect(jsonPath("$.content", hasSize(0)));
	}

	@Test
	void deletesExpenseAndRejectsRepeatedDeletion() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");
		long id = createExpense(token, unique("Delete"), "4.00", "MISCELLANEOUS", "CASH");
		String deleteBody = "{\"id\":" + id + "}";

		mockMvc.perform(delete("/api/v1/expense/delete")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(deleteBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Expense has been successfully deleted!"));

		mockMvc.perform(get("/api/v1/expense")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(pageRequest(0, 10, "title", true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)));

		mockMvc.perform(delete("/api/v1/expense/delete")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(deleteBody))
				.andExpect(status().isConflict());

		mockMvc.perform(delete("/api/v1/expense/delete")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void exposesUpdateChoices() throws Exception {
		String token = registerAndLogin(unique("user"), unique("member") + "@example.test");

		mockMvc.perform(get("/api/v1/expense/update")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.response", hasSize(6)));

		mockMvc.perform(get("/api/v1/expense/update"))
				.andExpect(status().isUnauthorized());
	}

	private long createExpense(String token, String title, String amount, String category, String paymentMethod)
			throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/expense/create")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(expensePayload(title, "Integration test", amount, category, paymentMethod)))
				.andExpect(status().isOk())
				.andReturn();
		return ((Number) com.jayway.jsonpath.JsonPath.read(
				result.getResponse().getContentAsString(), "$.response.id")).longValue();
	}

	private void update(String token, String path, long id, String value) throws Exception {
		mockMvc.perform(put("/api/v1/expense" + path)
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"toApply\":" + value + ",\"expenseId\":" + id + "}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.successful").value(true));
	}

	private String expensePayload(String title, String notes, String amount, String category, String paymentMethod) {
		return expensePayload(title, notes, amount, category, paymentMethod, Instant.now().minusSeconds(60));
	}

	private String expensePayload(String title, String notes, String amount, String category, String paymentMethod,
								  Instant expenseDate) {
		return """
				{"title":"%s","amount":%s,"expenseDate":"%s","notes":"%s","category":"%s","paymentMethod":"%s"}
				""".formatted(title, amount, expenseDate, notes, category, paymentMethod);
	}

	private String pageRequest(int page, int size, String property, boolean ascending) {
		return """
				{"pageNumber":%d,"pageSize":%d,"property":"%s","ascending":%s}
				""".formatted(page, size, property, ascending);
	}
}
