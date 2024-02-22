package me.gjpower.jsonassignment.controller;

import me.gjpower.jsonassignment.dto.Transaction;
import me.gjpower.jsonassignment.service.TransactionStore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    final TransactionStore store;

    public TransactionController(TransactionStore store) {
        this.store = store;
    }

    @PostMapping(
            value = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Transaction> addTransaction(@RequestBody List<Transaction> t) {
        return store.save(t);
    }

    @GetMapping(
        value = "/{date}/{type}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Transaction getTransaction(
            @PathVariable
            @DateTimeFormat(pattern="dd-MM-yyyy")
            LocalDate date,
            @PathVariable String type) throws NotFoundException {
        return store.fetch(date, type).orElseThrow(NotFoundException::new);
    }


    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NotFoundException extends Exception {}
}
