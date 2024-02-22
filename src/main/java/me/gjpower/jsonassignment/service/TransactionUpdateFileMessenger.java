package me.gjpower.jsonassignment.service;

import me.gjpower.jsonassignment.dto.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TransactionUpdateFileMessenger implements TransactionUpdateMessenger {

    Logger log = LoggerFactory.getLogger(TransactionUpdateFileMessenger.class);
    final String fileLocation;

    public TransactionUpdateFileMessenger(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * Simulates writing a notification message for when a transaction has changed
     * method is synchronized to ensure thread safety during file access
     * @param t new values for changed transaction
     */
    @Override
    public synchronized void notifyEvent(Transaction t) {
        log.info("Writing transaction update to event log {} {} {}", t.date().toString(), t.type(), t.amount());
        try (PrintWriter eventLog = new PrintWriter(new FileWriter(fileLocation, true))) {
            eventLog.printf("UPDATE %s %s %s%n", t.date().toString(), t.type(), t.amount());
        } catch (IOException e) { // for the purpose of assignment just catch and log
            log.error("Error writing update event to file", e);
        }
    }
}
