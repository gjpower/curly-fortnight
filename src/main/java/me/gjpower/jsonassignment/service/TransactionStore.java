package me.gjpower.jsonassignment.service;

import me.gjpower.jsonassignment.dto.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface TransactionStore {

    Optional<Transaction> fetch(LocalDate d, String type);
    List<Transaction> save(List<Transaction> t);
    List<Transaction> getAll();

}
