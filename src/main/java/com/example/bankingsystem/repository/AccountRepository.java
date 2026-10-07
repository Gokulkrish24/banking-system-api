package com.example.bankingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bankingsystem.model.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long>{
	
	List<Account> findByBranch(String branch);

	// Row-level lock so two simultaneous requests cannot corrupt a balance
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Account a where a.accountNumber = :accNo")
	Optional<Account> findByIdForUpdate(@Param("accNo") long accNo);

}
