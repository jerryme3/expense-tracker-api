package expense.tracker.v1;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiIntegrationTest extends ApiIntegrationTestSupport {

	@Test
	void registersAndLogsInUserWithBearerToken() throws Exception {
		String username = unique("user");
		String email = unique("member") + "@example.test";
		String registration = """
				{"username":"%s","role":"USER","account":{"email":"%s","password":"%s"}}
				""".formatted(username, email, TEST_PASSWORD);

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(registration))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.successful").value(true))
				.andExpect(jsonPath("$.response.username").value(username))
				.andExpect(jsonPath("$.response.email").value(email))
				.andExpect(jsonPath("$.response.password").doesNotExist());

		String login = """
				{"email":"%s","password":"%s"}
				""".formatted(email, TEST_PASSWORD);
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(login))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.successful").value(true))
				.andExpect(jsonPath("$.response.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.response.token", not("")))
				.andExpect(jsonPath("$.response.user.username").value(username))
				.andExpect(jsonPath("$.response.expiration").exists());
	}

	@Test
	void rejectsDuplicateEmailAndUsername() throws Exception {
		String username = unique("user");
		String email = unique("member") + "@example.test";
		String original = """
				{"username":"%s","role":"USER","account":{"email":"%s","password":"%s"}}
				""".formatted(username, email, TEST_PASSWORD);
		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(original))
				.andExpect(status().isOk());

		String duplicateEmail = """
				{"username":"%s","role":"USER","account":{"email":"%s","password":"%s"}}
				""".formatted(unique("user"), email, TEST_PASSWORD);
		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(duplicateEmail))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.successful").value(false));

		String duplicateUsername = """
				{"username":"%s","role":"USER","account":{"email":"%s","password":"%s"}}
				""".formatted(username, unique("member") + "@example.test", TEST_PASSWORD);
		mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(duplicateUsername))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.successful").value(false));
	}

	@Test
	void rejectsMalformedRegistrationRequests() throws Exception {
		var invalidRequests = new String[] {
				"""
				{"username":"validuser","role":"USER","account":{"email":"not-an-email","password":"Goodpass1!"}}
				""",
				"""
				{"username":"ab","role":"USER","account":{"email":"valid@example.test","password":"Goodpass1!"}}
				""",
				"""
				{"username":"","role":"USER","account":{"email":"valid@example.test","password":"Goodpass1!"}}
				""",
				"""
				{"username":"%s","role":"USER","account":{"email":"valid@example.test","password":"Goodpass1!"}}
				""".formatted("u".repeat(51)),
				"""
				{"username":"validuser","role":"USER","account":{"email":"valid@example.test","password":"weak"}}
				""",
				"""
				{"username":"validuser","role":"USER","account":{"email":"valid@example.test","password":"%s"}}
				""".formatted("A1!" + "x".repeat(40)),
				"""
				{"username":"validuser","account":{"email":"valid@example.test","password":"Goodpass1!"}}
				""",
				"""
				{"username":"validuser","role":"USER"}
				"""
		};

		for (String request : invalidRequests) {
			mockMvc.perform(post("/api/v1/auth/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(request))
					.andExpect(status().isBadRequest());
		}
	}

	@Test
	void rejectsUnknownEmailAndIncorrectPassword() throws Exception {
		String username = unique("user");
		String email = unique("member") + "@example.test";
		registerAndLogin(username, email);

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"missing@example.test","password":"Goodpass1!"}
								"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.successful").value(false));

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"%s","password":"Wrongpass1!"}
								""".formatted(email)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.successful").value(false));

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"not-an-email","password":""}
								"""))
				.andExpect(status().isBadRequest());
	}
}
