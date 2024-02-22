package me.gjpower.jsonassignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.gjpower.jsonassignment.dto.Transaction;
import me.gjpower.jsonassignment.service.TransactionBackend;
import me.gjpower.jsonassignment.service.TransactionFileStore;
import me.gjpower.jsonassignment.service.TransactionStore;
import me.gjpower.jsonassignment.service.TransactionUpdateMessenger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFileStoreTests {

    static final LocalDate TEST_DATE = LocalDate.now();
    static final String TEST_TYPE = "credit";
    static final BigDecimal TEST_AMOUNT = new BigDecimal("12.34");

    static TransactionBackend backend;
    static TransactionUpdateMessenger messenger;
    static ObjectMapper om;

    TransactionStore store;

    @BeforeAll
    static void init() {
        backend = Mockito.mock(TransactionBackend.class);
        messenger = Mockito.mock(TransactionUpdateMessenger.class);
        om = new ObjectMapper();
    }

    @BeforeEach
    void preTest() {
        store = new TransactionFileStore(backend, messenger);
    }

    @Test
    void canRetrieveStoredTransaction() {
        final Transaction testTransaction = new Transaction(TEST_DATE, TEST_TYPE, TEST_AMOUNT);
        store.save(List.of(testTransaction));
        assertEquals(Optional.of(testTransaction), store.fetch(TEST_DATE, TEST_TYPE));
    }

    @Test
    void storedAmountsAreSummed() {
        final Transaction testTransaction = new Transaction(TEST_DATE, TEST_TYPE, TEST_AMOUNT);
        final List<Transaction> testTList = List.of(testTransaction);

        for (int i = 1; i <= 20; i++) {
            store.save(testTList);
            var result = store.fetch(TEST_DATE, TEST_TYPE);
            assertTrue(result.isPresent());
            assertEquals(TEST_AMOUNT.multiply(BigDecimal.valueOf(i)), result.get().amount());
        }

    }

    @Test
    void cannotRetrieveNonExistingTransaction() {
        assertEquals(Optional.empty(), store.fetch(TEST_DATE, TEST_TYPE));
    }
}
