package com.revolut.fundtransfer.dao.impl;

import com.revolut.fundtransfer.dao.AccountDAO;
import com.revolut.fundtransfer.dao.H2DAOFactory;
import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.Account;
import com.revolut.fundtransfer.model.UserTransaction;
import com.revolut.fundtransfer.utils.MoneyUtil;
import com.revolut.fundtransfer.utils.PropertiesLoader;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Vivek Shah
 *
 */
public class AccountDAOImpl implements AccountDAO {

	private static Logger log = Logger.getLogger(AccountDAOImpl.class);
	
	/**
	 * Create account
	 */
	public long createAccount(Account account) throws HSQLDBException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("CREATE_ACCOUNT"));
			stmt.setString(1, account.getUserName());
			stmt.setBigDecimal(2, account.getBalance());
			stmt.setString(3, account.getCurrencyCode());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("createAccount(): Creating account failed, no rows affected.");
				throw new HSQLDBException("Account Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("Creating account failed, no ID obtained.");
				throw new HSQLDBException("Account Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting Account  " + account);
			throw new HSQLDBException("createAccount(): Error creating user account " + account, e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, generatedKeys);
		}
	}
	
	/**
	 * Get account by id
	 */
	public Account getAccountById(long accountId) throws HSQLDBException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("GET_ACCOUNT_BY_ID"));
			stmt.setLong(1, accountId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
						rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("Retrieve Account By Id: " + acc);
			}
			return acc;
		} catch (SQLException e) {
			throw new HSQLDBException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}

	}
	
	/**
	 * Update account balance
	 */
	public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws HSQLDBException {
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account targetAccount = null;
		int updateCount = -1;
		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			// lock account for writing:
			lockStmt = conn.prepareStatement(PropertiesLoader.getStringProperty("LOCK_ACCOUNT"));
			lockStmt.setLong(1, accountId);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				targetAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
						rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("updateAccountBalance from Account: " + targetAccount);
			}

			if (targetAccount == null) {
				throw new HSQLDBException("updateAccountBalance(): fail to lock account : " + accountId);
			}
			// update account upon success locking
			BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
			if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
				throw new HSQLDBException("Not sufficient Fund for account: " + accountId);
			}

			updateStmt = conn.prepareStatement(PropertiesLoader.getStringProperty("UPDATE_ACCOUNT"));
			updateStmt.setBigDecimal(1, balance);
			updateStmt.setLong(2, accountId);
			updateCount = updateStmt.executeUpdate();
			conn.commit();
			if (log.isDebugEnabled())
				log.debug("New Balance after Update: " + targetAccount);
			return updateCount;
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountId, se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new HSQLDBException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return updateCount;
	}
	
	/**
	 * Get all accounts.
	 */
	public List<Account> getAllAccounts() throws HSQLDBException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Account> allAccounts = new ArrayList<Account>();
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("GET_ALL_ACCOUNTS"));
			rs = stmt.executeQuery();
			while (rs.next()) {
				Account acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
						rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("getAllAccounts(): Get  Account " + acc);
				allAccounts.add(acc);
			}
			return allAccounts;
		} catch (SQLException e) {
			throw new HSQLDBException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}
	
	/**
	 * Delete account by id
	 */
	public int deleteAccountById(long accountId) throws HSQLDBException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("DELETE_ACCOUNT_BY_ID"));
			stmt.setLong(1, accountId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new HSQLDBException("deleteAccountById(): Error deleting user account Id " + accountId, e);
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stmt);
		}
	}

	/**
	 * Transfer balance between two accounts.
	 */
	public int transferAccountBalance(UserTransaction userTransaction) throws HSQLDBException {
		int result = -1;
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account fromAccount = null;
		Account toAccount = null;

		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			// lock the credit and debit account for writing:
			lockStmt = conn.prepareStatement(PropertiesLoader.getStringProperty("LOCK_ACCOUNT"));
			lockStmt.setLong(1, userTransaction.getFromAccountId());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
						rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance from Account: " + fromAccount);
			}
			lockStmt = conn.prepareStatement(PropertiesLoader.getStringProperty("LOCK_ACCOUNT"));
			lockStmt.setLong(1, userTransaction.getToAccountId());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				toAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
						rs.getString("CurrencyCode"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance to Account: " + toAccount);
			}

			// check locking status
			if (fromAccount == null || toAccount == null) {
				throw new HSQLDBException("Fail to lock both accounts for write");
			}

			// check transaction currency
			if (!fromAccount.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
				throw new HSQLDBException(
						"Fail to transfer Fund, transaction ccy are different from source/destination");
			}

			// check ccy is the same for both accounts
			if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
				throw new HSQLDBException(
						"Fail to transfer Fund, the source and destination account are in different currency");
			}

			// check enough fund in source account
			BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
			if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
				throw new HSQLDBException("Not enough Fund from source Account ");
			}
			// proceed with update
			updateStmt = conn.prepareStatement(PropertiesLoader.getStringProperty("UPDATE_ACCOUNT"));
			updateStmt.setBigDecimal(1, fromAccountLeftOver);
			updateStmt.setLong(2, userTransaction.getFromAccountId());
			updateStmt.addBatch();
			updateStmt.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
			updateStmt.setLong(2, userTransaction.getToAccountId());
			updateStmt.addBatch();
			int[] rowsUpdated = updateStmt.executeBatch();
			result = rowsUpdated[0] + rowsUpdated[1];
			if (log.isDebugEnabled()) {
				log.debug("Number of rows updated for the transfer : " + result);
			}
			// If there is no error, commit the transaction
			conn.commit();
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction,
					se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new HSQLDBException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return result;
	}

}
