package com.example.bankingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_transaction")
public class BankTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long accountNumber;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	private BigDecimal amount;
	private BigDecimal balanceAfter;

	// the other account involved in a transfer (null for deposits/withdrawals)
	private Long relatedAccount;

	private LocalDateTime createdAt;
	private String description;

	public BankTransaction() {
	}

	public BankTransaction(long accountNumber, TransactionType transactionType, BigDecimal amount,
			BigDecimal balanceAfter, Long relatedAccount, String description) {
		this.accountNumber = accountNumber;
		this.transactionType = transactionType;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.relatedAccount = relatedAccount;
		this.description = description;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public Long getRelatedAccount() {
		return relatedAccount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getDescription() {
		return description;
	}
}
