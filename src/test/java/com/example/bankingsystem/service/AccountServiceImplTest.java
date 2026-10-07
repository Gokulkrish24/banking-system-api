package com.example.bankingsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bankingsystem.exception.AccountInactiveException;
import com.example.bankingsystem.exception.InsufficientBalanceException;
import com.example.bankingsystem.exception.InvalidAmountException;
import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.model.BankTransaction;
import com.example.bankingsystem.model.TransactionType;
import com.example.bankingsystem.repository.AccountRepository;
import com.example.bankingsystem.repository.BankTransactionRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

	@Mock
	private AccountRepository accountRepo;

	@Mock
	private BankTransactionRepository txnRepo;

	@InjectMocks
	private AccountServiceImpl service;

	private Account account(long number, String status, String balance) {
		Account a = new Account();
		a.setAccountNumber(number);
		a.setStatus(status);
		a.setBalance(new BigDecimal(balance));
		return a;
	}

	private void assertBalance(String expected, Account acc) {
		assertEquals(0, new BigDecimal(expected).compareTo(acc.getBalance()));
	}

	@Test
	void depositIncreasesBalanceAndRecordsTransaction() {
		Account acc = account(1L, "Active", "1000.00");
		when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));
		when(txnRepo.save(any(BankTransaction.class))).thenAnswer(i -> i.getArgument(0));

		BankTransaction txn = service.deposit(1L, new BigDecimal("500"));

		assertBalance("1500.00", acc);
		assertEquals(TransactionType.DEPOSIT, txn.getTransactionType());
		assertEquals(0, new BigDecimal("1500.00").compareTo(txn.getBalanceAfter()));
	}

	@Test
	void withdrawReducesBalance() {
		Account acc = account(1L, "Active", "1000.00");
		when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));
		when(txnRepo.save(any(BankTransaction.class))).thenAnswer(i -> i.getArgument(0));

		service.withdraw(1L, new BigDecimal("250.50"));

		assertBalance("749.50", acc);
	}

	@Test
	void withdrawMoreThanBalanceIsRejected() {
		Account acc = account(1L, "Active", "100.00");
		when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));

		assertThrows(InsufficientBalanceException.class, () -> service.withdraw(1L, new BigDecimal("100.01")));

		assertBalance("100.00", acc);
		verify(txnRepo, never()).save(any());
	}

	@Test
	void negativeOrZeroAmountIsRejected() {
		assertThrows(InvalidAmountException.class, () -> service.deposit(1L, new BigDecimal("-5")));
		assertThrows(InvalidAmountException.class, () -> service.withdraw(1L, BigDecimal.ZERO));
		assertThrows(InvalidAmountException.class, () -> service.deposit(1L, null));
	}

	@Test
	void inactiveAccountCannotTransact() {
		Account acc = account(1L, "Closed", "1000.00");
		when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));

		assertThrows(AccountInactiveException.class, () -> service.deposit(1L, new BigDecimal("10")));
	}

	@Test
	void transferMovesMoneyBetweenAccounts() {
		Account from = account(1L, "Active", "1000.00");
		Account to = account(2L, "Active", "200.00");
		when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
		when(accountRepo.findByIdForUpdate(2L)).thenReturn(Optional.of(to));
		when(txnRepo.save(any(BankTransaction.class))).thenAnswer(i -> i.getArgument(0));

		BankTransaction debit = service.transfer(1L, 2L, new BigDecimal("300"));

		assertBalance("700.00", from);
		assertBalance("500.00", to);
		assertEquals(TransactionType.TRANSFER_OUT, debit.getTransactionType());
	}

	@Test
	void transferWithInsufficientFundsChangesNothing() {
		Account from = account(1L, "Active", "50.00");
		Account to = account(2L, "Active", "200.00");
		when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(from));
		when(accountRepo.findByIdForUpdate(2L)).thenReturn(Optional.of(to));

		assertThrows(InsufficientBalanceException.class, () -> service.transfer(1L, 2L, new BigDecimal("300")));

		assertBalance("50.00", from);
		assertBalance("200.00", to);
		verify(txnRepo, never()).save(any());
	}

	@Test
	void transferToSameAccountIsRejected() {
		assertThrows(InvalidAmountException.class, () -> service.transfer(1L, 1L, new BigDecimal("10")));
	}
}
