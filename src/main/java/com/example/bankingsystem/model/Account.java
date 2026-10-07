package com.example.bankingsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Account {
	
	@Id
	private long accountNumber;
	
	private String accountHolderName;
	private String IFSCCode;
	private long mobileNumber;
	private String emailID;
	private String accType;
	private long aadhaarNo;
	private String branch;
	private String status;
	private BigDecimal balance;
	
	public Account() {
		
	}
	
	
	public Account(long accountNumber, String accountHolderName, String iFSCCode, long mobileNumber, String emailID,
			String accType, long aadhaarNo, String branch, String status, BigDecimal balance) {
		super();
		this.accountNumber = accountNumber;
		this.accountHolderName = accountHolderName;
		IFSCCode = iFSCCode;
		this.mobileNumber = mobileNumber;
		this.emailID = emailID;
		this.accType = accType;
		this.aadhaarNo = aadhaarNo;
		this.branch = branch;
		this.status = status;
		this.balance = balance;
	}


	public long getAccountNumber() {
		return accountNumber;
	}


	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}


	public String getAccountHolderName() {
		return accountHolderName;
	}


	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}


	public String getIFSCCode() {
		return IFSCCode;
	}


	public void setIFSCCode(String iFSCCode) {
		IFSCCode = iFSCCode;
	}


	public long getMobileNumber() {
		return mobileNumber;
	}


	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}


	public String getEmailID() {
		return emailID;
	}


	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}


	public String getAccType() {
		return accType;
	}


	public void setAccType(String accType) {
		this.accType = accType;
	}


	public long getAadhaarNo() {
		return aadhaarNo;
	}


	public void setAadhaarNo(long aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}


	public String getBranch() {
		return branch;
	}


	public void setBranch(String branch) {
		this.branch = branch;
	}


	


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public BigDecimal getBalance() {
		return balance;
	}


	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", accountHolderName=" + accountHolderName + ", IFSCCode="
				+ IFSCCode + ", mobileNumber=" + mobileNumber + ", emailID=" + emailID + ", accType=" + accType
				+ ", aadhaarNo=" + aadhaarNo + ", branch=" + branch + ", status=" + status + ", balance=" + balance
				+ "]";
	}
	
}
