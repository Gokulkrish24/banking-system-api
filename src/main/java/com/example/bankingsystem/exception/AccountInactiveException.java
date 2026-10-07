package com.example.bankingsystem.exception;

public class AccountInactiveException extends RuntimeException {

	public AccountInactiveException(long accountNumber) {
		super("Account " + accountNumber + " is not active");
	}
}
