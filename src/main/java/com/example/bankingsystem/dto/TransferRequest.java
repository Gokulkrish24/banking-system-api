package com.example.bankingsystem.dto;

import java.math.BigDecimal;

public record TransferRequest(long fromAccount, long toAccount, BigDecimal amount) {
}
