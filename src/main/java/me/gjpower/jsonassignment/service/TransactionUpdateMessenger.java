package me.gjpower.jsonassignment.service;

import me.gjpower.jsonassignment.dto.Transaction;

public interface TransactionUpdateMessenger {
    void notifyEvent(Transaction t);
}
