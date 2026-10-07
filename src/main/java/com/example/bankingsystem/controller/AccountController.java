package com.example.bankingsystem.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankingsystem.dto.AmountRequest;
import com.example.bankingsystem.dto.TransferRequest;
import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.model.BankTransaction;
import com.example.bankingsystem.service.AccountService;

@RestController
public class AccountController {
	
	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@GetMapping("/api/accounts")
	public List<Account> getForAllAccounts(){
		return accountService.getAllAccounts();
	}
	
	@GetMapping("/api/accounts/{id}")
	public ResponseEntity<Account> searchByAccId(@PathVariable("id") long accNo){
		Optional<Account> opt =accountService.searchByAccId(accNo);
		if(opt.isPresent()) {
			return ResponseEntity.ok().body(opt.get());
		}
		return ResponseEntity.notFound().build();	
	}
	
	
	@GetMapping("/api/accounts/branch/{branch}") //search by branch
	public List<Account> searchByAccBranch(@PathVariable String branch){				
		return accountService.getAllAccByBranch(branch);
	}
	
	@PostMapping("/api/accounts")
	public ResponseEntity<Account> apiForNewAccount(@RequestBody Account account){
		Optional<Account> opt =accountService.searchByAccId(account.getAccountNumber());
		if(opt.isEmpty()) {
			Account savedAcount = accountService.addNewAccount(account);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedAcount);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	
	@PutMapping("/api/accounts/{id}")
	public ResponseEntity<Account> apiForUpdateData(@PathVariable("id") long accno, @RequestBody Account account)
	{
		Optional<Account> opt=accountService.searchByAccId(accno);
		
		if(opt.isEmpty())		
			return ResponseEntity.notFound().build(); 		
		
		// Account number comes from the URL, and the balance can only change through
		// deposit / withdraw / transfer so that every movement is recorded.
		account.setAccountNumber(accno);
		account.setBalance(opt.get().getBalance());
		Account updatedAcc = accountService.addNewAccount(account);
		return ResponseEntity.ok(updatedAcc); 
	}

	@DeleteMapping("/api/accounts/{id}")
	public ResponseEntity<Account> apiForDeleteAcc(@PathVariable("id") long accNO){
		Optional<Account> opt =accountService.searchByAccId(accNO);
		if(opt.isPresent()) {
			accountService.deleteAccount(accNO);
			return ResponseEntity.ok().body(opt.get());
		}
		return ResponseEntity.notFound().build();
	}

	// ---------- money movement ----------

	@PostMapping("/api/accounts/{id}/deposit")
	public ResponseEntity<BankTransaction> deposit(@PathVariable("id") long accNo, @RequestBody AmountRequest request) {
		return ResponseEntity.ok(accountService.deposit(accNo, request.amount()));
	}

	@PostMapping("/api/accounts/{id}/withdraw")
	public ResponseEntity<BankTransaction> withdraw(@PathVariable("id") long accNo, @RequestBody AmountRequest request) {
		return ResponseEntity.ok(accountService.withdraw(accNo, request.amount()));
	}

	@PostMapping("/api/transfer")
	public ResponseEntity<BankTransaction> transfer(@RequestBody TransferRequest request) {
		return ResponseEntity.ok(accountService.transfer(request.fromAccount(), request.toAccount(), request.amount()));
	}

	@GetMapping("/api/accounts/{id}/transactions")
	public List<BankTransaction> transactions(@PathVariable("id") long accNo) {
		return accountService.getTransactions(accNo);
	}

}
