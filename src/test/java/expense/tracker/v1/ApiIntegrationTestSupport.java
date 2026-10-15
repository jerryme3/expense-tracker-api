package expense.tracker.v1;

import expense.tracker.v1.account.AccountRepository;
import expense.tracker.v1.expense.ExpenseRepository;
import expense.tracker.v1.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:expense-tracker-test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.open-in-view=false",
		"spring.docker.compose.enabled=false",
		"app.jwt.secret=test-jwt-secret-long-enough-for-hs256-tests",
		"app.jwt.expiration=PT1H"
})
@AutoConfigureMockMvc
@Transactional
abstract class ApiIntegrationTestSupport {

	protected static final String TEST_PASSWORD = "Goodpass1!";

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@BeforeEach
	void clearDatabase() {
		expenseRepository.deleteAll();
		userRepository.deleteAll();
		accountRepository.deleteAll();
	}

	protected String unique(String prefix) {
		return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
	}

	protected String registerAndLogin(String username, String email) throws Exception {
		var registration = """
				{
				  "username": "%s",
				  "role": "USER",
				  "account": {
				    "email": "%s",
				    "password": "%s"
				  }
				}
				""".formatted(username, email, TEST_PASSWORD);

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(registration))
				.andExpect(status().isOk());

		var login = """
				{"email": "%s", "password": "%s"}
				""".formatted(email, TEST_PASSWORD);

		MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(login))
				.andExpect(status().isOk())
				.andReturn();

		return com.jayway.jsonpath.JsonPath.read(
				result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				"$.response.token");
	}
}
