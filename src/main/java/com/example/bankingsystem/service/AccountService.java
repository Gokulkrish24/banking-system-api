package com.example.bankingsystem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.model.BankTransaction;

public interface AccountService {

	List<Account> getAllAccounts();
	
	Optional<Account> searchByAccId(long accno);
	
	List<Account> getAllAccByBranch(String branch);
	 
	Account addNewAccount(Account account);
	
	void deleteAccount(long accno);

	BankTransaction deposit(long accno, BigDecimal amount);

	BankTransaction withdraw(long accno, BigDecimal amount);

	/** Moves money atomically; returns the debit-side transaction. */
	BankTransaction transfer(long fromAcc, long toAcc, BigDecimal amount);

	List<BankTransaction> getTransactions(long accno);
}
