package com.example.bankingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankingsystem.exception.AccountInactiveException;
import com.example.bankingsystem.exception.AccountNotFoundException;
import com.example.bankingsystem.exception.InsufficientBalanceException;
import com.example.bankingsystem.exception.InvalidAmountException;
import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.model.BankTransaction;
import com.example.bankingsystem.model.TransactionType;
import com.example.bankingsystem.repository.AccountRepository;
import com.example.bankingsystem.repository.BankTransactionRepository;

@Service
public class AccountServiceImpl implements AccountService{

	private static final String ACTIVE = "Active";

	private final AccountRepository accountRepo;
	private final BankTransactionRepository txnRepo;

	public AccountServiceImpl(AccountRepository accountRepo, BankTransactionRepository txnRepo) {
		this.accountRepo = accountRepo;
		this.txnRepo = txnRepo;
	}

	@Override
	public List<Account> getAllAccounts() {
		return accountRepo.findAll();
	}

	@Override
	public Optional<Account> searchByAccId(long accno) {
		return accountRepo.findById(accno);
	}

	@Override
	public Account addNewAccount(Account account) {
		if (account.getBalance() == null) {
			account.setBalance(BigDecimal.ZERO.setScale(2));
		} else if (account.getBalance().signum() < 0) {
			throw new InvalidAmountException("Opening balance cannot be negative");
		}
		return accountRepo.save(account);
	}

	@Override
	public void deleteAccount(long accno) {
		accountRepo.deleteById(accno);
	}

	@Override
	public List<Account> getAllAccByBranch(String branch) {
		return accountRepo.findByBranch(branch);
	}

	@Override
	@Transactional
	public BankTransaction deposit(long accno, BigDecimal amount) {
		BigDecimal amt = validateAmount(amount);
		Account acc = lockActiveAccount(accno);

		acc.setBalance(balanceOf(acc).add(amt));
		accountRepo.save(acc);

		return txnRepo.save(new BankTransaction(accno, TransactionType.DEPOSIT, amt,
				acc.getBalance(), null, "Deposit"));
	}

	@Override
	@Transactional
	public BankTransaction withdraw(long accno, BigDecimal amount) {
		BigDecimal amt = validateAmount(amount);
		Account acc = lockActiveAccount(accno);

		if (balanceOf(acc).compareTo(amt) < 0) {
			throw new InsufficientBalanceException("Insufficient balance in account " + accno);
		}
		acc.setBalance(balanceOf(acc).subtract(amt));
		accountRepo.save(acc);

		return txnRepo.save(new BankTransaction(accno, TransactionType.WITHDRAWAL, amt,
				acc.getBalance(), null, "Withdrawal"));
	}

	@Override
	@Transactional
	public BankTransaction transfer(long fromAcc, long toAcc, BigDecimal amount) {
		BigDecimal amt = validateAmount(amount);
		if (fromAcc == toAcc) {
			throw new InvalidAmountException("Cannot transfer to the same account");
		}

		// Lock rows in a fixed order (lowest account number first) so that two opposite
		// transfers running at the same time cannot deadlock each other.
		long low = Math.min(fromAcc, toAcc);
		long high = Math.max(fromAcc, toAcc);
		Account lowAcc = lockActiveAccount(low);
		Account highAcc = lockActiveAccount(high);
		Account source = (fromAcc == low) ? lowAcc : highAcc;
		Account target = (fromAcc == low) ? highAcc : lowAcc;

		if (balanceOf(source).compareTo(amt) < 0) {
			throw new InsufficientBalanceException("Insufficient balance in account " + fromAcc);
		}

		source.setBalance(balanceOf(source).subtract(amt));
		target.setBalance(balanceOf(target).add(amt));
		accountRepo.save(source);
		accountRepo.save(target);

		BankTransaction debit = txnRepo.save(new BankTransaction(fromAcc, TransactionType.TRANSFER_OUT, amt,
				source.getBalance(), toAcc, "Transfer to " + toAcc));
		txnRepo.save(new BankTransaction(toAcc, TransactionType.TRANSFER_IN, amt,
				target.getBalance(), fromAcc, "Transfer from " + fromAcc));
		return debit;
	}

	@Override
	public List<BankTransaction> getTransactions(long accno) {
		if (!accountRepo.existsById(accno)) {
			throw new AccountNotFoundException(accno);
		}
		return txnRepo.findByAccountNumberOrderByCreatedAtDesc(accno);
	}

	// ---------- helpers ----------

	private BigDecimal validateAmount(BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new InvalidAmountException("Amount must be greater than zero");
		}
		if (amount.scale() > 2) {
			throw new InvalidAmountException("Amount can have at most 2 decimal places");
		}
		return amount.setScale(2, RoundingMode.UNNECESSARY);
	}

	private Account lockActiveAccount(long accno) {
		Account acc = accountRepo.findByIdForUpdate(accno)
				.orElseThrow(() -> new AccountNotFoundException(accno));
		if (!ACTIVE.equalsIgnoreCase(acc.getStatus())) {
			throw new AccountInactiveException(accno);
		}
		return acc;
	}

	private BigDecimal balanceOf(Account acc) {
		return acc.getBalance() == null ? BigDecimal.ZERO : acc.getBalance();
	}
}
