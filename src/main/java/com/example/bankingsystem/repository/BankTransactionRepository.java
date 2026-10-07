package com.example.bankingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankingsystem.model.BankTransaction;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

	List<BankTransaction> findByAccountNumberOrderByCreatedAtDesc(long accountNumber);
}
