package me.gjpower.jsonassignment.service;

import me.gjpower.jsonassignment.dto.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TransactionFileStore implements TransactionStore {

    // as Spring is threaded and this may be called concurrently we need
    // either a concurrent data structure or synchronised blocks & methods to ensure
    // thread safety
    final ConcurrentMap<LocalDate, ConcurrentMap<String, BigDecimal>> memoryStore;
    final TransactionBackend backend;
    final TransactionUpdateMessenger updateMessenger;
    Logger log = LoggerFactory.getLogger(TransactionFileStore.class);

    public TransactionFileStore(TransactionBackend backend, TransactionUpdateMessenger updateMessenger) {
        // using skip list for ordered structure on date
        // if thread safety was not required or synchronized blocks
        // a treemap could be used instead
        // nested map is used to maintain ordering by date
        memoryStore = new ConcurrentSkipListMap<>();
        this.backend = backend;
        this.updateMessenger = updateMessenger;
        initMemoryStore();
    }

    @Override
    public Optional<Transaction> fetch(LocalDate d, String type) {
        final Map<String, BigDecimal> request = memoryStore.get(d);
        if (request == null) {
            return Optional.empty();
        }

        final BigDecimal amount = request.get(type);
        if (amount == null) {
            return Optional.empty();
        }

        return Optional.of(new Transaction(d, type, amount));
    }

    @Override
    public List<Transaction> save(List<Transaction> t) {
        final var result = storeInMemory(t);
        flush(); // write changes to file
        return result;
    }

    List<Transaction> storeInMemory(List<Transaction> t) {
        return t.stream().map(this::storeInMemory).toList();
    }

    Transaction storeInMemory(Transaction t) {
        ConcurrentMap<String, BigDecimal> ofDate =
                memoryStore.computeIfAbsent(
                        t.date(), k -> new ConcurrentHashMap<>());

        final BigDecimal resultAmount = ofDate.compute(t.type(), (k, v) -> {
            if (v == null) {
                return t.amount();
            }
            BigDecimal newAmount = v.add(t.amount());
            // updating the amount so send event message
            // ideally this would be non-blocking
            updateMessenger.notifyEvent(new Transaction(t.date(), t.type(), newAmount));
            return v.add(t.amount());
        });

        return new Transaction(t.date(), t.type(), resultAmount);
    }

    @Override
    public List<Transaction> getAll() {
        final List<Transaction> result = new ArrayList<>();

        for (Map.Entry<LocalDate, ConcurrentMap<String, BigDecimal>> byDate : memoryStore.entrySet()) {
            for (Map.Entry<String, BigDecimal> byType : byDate.getValue().entrySet()) {
                result.add(new Transaction(byDate.getKey(), byType.getKey(), byType.getValue()));
            }
        }
        return result;
    }

    // flush changes to file when necessary
    public void flush() {
        try {
            backend.save(getAll());
        } catch (IOException e) {
            log.error("Unable to write changes to storage file", e);
        }
    }

    void initMemoryStore() {
        try {
            final List<Transaction> savedTransactions = backend.restore();
            if (!savedTransactions.isEmpty()) {
                storeInMemory(savedTransactions);
            }
        } catch (IOException e) {
            log.error("Unable to read stored transactions from file");
        }
    }

}
