package com.example.bankingsystem.exception;

public class AccountNotFoundException extends RuntimeException {

	public AccountNotFoundException(long accountNumber) {
		super("Account not found: " + accountNumber);
	}
}
