package me.gjpower.jsonassignment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.gjpower.jsonassignment.dto.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TransactionFileBackend implements TransactionBackend {

    final ObjectMapper om;
    final File storageFile;

    public TransactionFileBackend(ObjectMapper om, String fileLocation) {
        this.om = om;
        this.storageFile = new File(fileLocation);
    }

    public synchronized void save(List<Transaction> t) throws IOException {
        om.writeValue(storageFile, t);
    }

    public List<Transaction> restore() throws IOException {
        return om.readValue(storageFile, new TypeReference<>() {});
    }
}
