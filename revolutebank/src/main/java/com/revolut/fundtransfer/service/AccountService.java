package com.revolut.fundtransfer.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.core.Response;

import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.Account;

public interface AccountService {
	public List<Account> getAllAccounts() throws HSQLDBException;

	public Account getAccount(long accountId) throws HSQLDBException;

	public BigDecimal getBalance(long accountId) throws HSQLDBException;

	public Account createAccount(Account account) throws HSQLDBException;

	public Account deposit(long accountId, BigDecimal amount) throws HSQLDBException;

	public Account withdraw(long accountId, BigDecimal amount) throws HSQLDBException;

	public Response deleteAccount(long accountId) throws HSQLDBException;

}
