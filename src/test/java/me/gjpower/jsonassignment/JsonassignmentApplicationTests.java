package me.gjpower.jsonassignment;

import me.gjpower.jsonassignment.dto.Transaction;
import me.gjpower.jsonassignment.service.TransactionBackend;
import me.gjpower.jsonassignment.service.TransactionStore;
import me.gjpower.jsonassignment.service.TransactionUpdateMessenger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class JsonassignmentApplicationTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	TransactionBackend backend;
	@MockBean
	TransactionUpdateMessenger messenger;

	@Autowired
	TransactionStore store;

	@Test
	void contextLoads() {
		assertNotNull(store);
	}

	@Test
	void tryFetchFromStore() {
		Assertions.assertEquals(Optional.empty(), store.fetch(LocalDate.now(), "something"));
	}

	@Test
	void saveAndFetchFromStore() {
		final var now = LocalDate.now();
		final var type = "credit";
		final var amount = new BigDecimal("12.34");
		final Transaction t = new Transaction(now, type, amount);

		store.save(List.of(t));
		Assertions.assertEquals(1, store.getAll().size());
		var fetched = store.fetch(now, type);
		Assertions.assertTrue(fetched.isPresent());
		Assertions.assertEquals(t, fetched.get());
		store.save(List.of(t));
	}

	@Test
	void receive404ForNonExistingTransaction() throws Exception {
		mvc.perform(get("/transaction/01-01-2020/not-existing"))
				.andExpect(status().isNotFound());
	}

	@Test
	void canUploadAndRetrieveTransaction() throws Exception {
		final String testJson =
				"{ \"date\": \"05-05-2020\", \"type\": \"my-test-type\", \"amount\": 22.04 }";
		mvc.perform(
				post("/transaction/")
					.contentType(MediaType.APPLICATION_JSON)
						.content("[" + testJson + "]")
		).andExpect(status().isOk());

		mvc.perform(get("/transaction/05-05-2020/my-test-type"))
				.andExpect(status().isOk())
				.andExpect(content().json(testJson));
	}

}
