package me.gjpower.jsonassignment.service;

import me.gjpower.jsonassignment.dto.Transaction;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface TransactionBackend {
    void save(List<Transaction> t) throws IOException;
    List<Transaction> restore() throws IOException;
}
