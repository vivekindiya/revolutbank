package com.revolut.fundtransfer.dao;

import java.math.BigDecimal;
import java.util.List;

import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.Account;
import com.revolut.fundtransfer.model.UserTransaction;

/**
 * 
 * @author Vivek Shah
 *
 */
public interface AccountDAO {

	public long createAccount(Account account) throws HSQLDBException;

	public Account getAccountById(long accountId) throws HSQLDBException;

	public List<Account> getAllAccounts() throws HSQLDBException;

	public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws HSQLDBException;

	public int transferAccountBalance(UserTransaction userTransaction) throws HSQLDBException;

	public int deleteAccountById(long accountId) throws HSQLDBException;
}
