package me.gjpower.jsonassignment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

// Specify date format
//
// amount looks like a currency so use BigDecimal to avoid
// accumulating errors during arithmetic
public record Transaction(
        @JsonProperty(required = true)
        @JsonFormat(pattern="dd-MM-yyyy")
        LocalDate date,
        @JsonProperty(required = true)
        String type,
        @JsonProperty(required = true)
        BigDecimal amount)
{}
