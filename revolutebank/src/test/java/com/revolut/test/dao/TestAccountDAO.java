package com.revolut.test.dao;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revolut.fundtransfer.dao.DBDAOFactory;
import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class TestAccountDAO {

	private static final DBDAOFactory h2DaoFactory = DBDAOFactory.getDAOFactory(DBDAOFactory.H2DB);

	@BeforeClass
	public static void setup() {
		h2DaoFactory.createDBContent();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetAllAccounts() throws HSQLDBException {
		List<Account> allAccounts = h2DaoFactory.getAccountDAO().getAllAccounts();
		assertTrue(allAccounts.size() > 1);
	}

	@Test
	public void testGetAccountById() throws HSQLDBException {
		Account account = h2DaoFactory.getAccountDAO().getAccountById(1L);
		assertTrue(account.getUserName().equals("vivek"));
	}

	@Test
	public void testGetNonExistingAccById() throws HSQLDBException {
		Account account = h2DaoFactory.getAccountDAO().getAccountById(100L);
		assertTrue(account == null);
	}

	@Test
	public void testCreateAccount() throws HSQLDBException {
		BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
		Account a = new Account("test2", balance, "CNY");
		long aid = h2DaoFactory.getAccountDAO().createAccount(a);
		Account afterCreation = h2DaoFactory.getAccountDAO().getAccountById(aid);
		assertTrue(afterCreation.getUserName().equals("test2"));
		assertTrue(afterCreation.getCurrencyCode().equals("CNY"));
		assertTrue(afterCreation.getBalance().equals(balance));
	}

	@Test
	public void testDeleteAccount() throws HSQLDBException {
		int rowCount = h2DaoFactory.getAccountDAO().deleteAccountById(2l);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(2l) == null);
	}

	@Test
	public void testDeleteNonExistingAccount() throws HSQLDBException {
		int rowCount = h2DaoFactory.getAccountDAO().deleteAccountById(200L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

	@Test
	public void testUpdateAccountBalanceSufficientFund() throws HSQLDBException {

		BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdated = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaDeposit);
		assertTrue(rowsUpdated == 1);
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(1L).getBalance().equals(afterDeposit));
		BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 1);
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(1L).getBalance().equals(afterWithDraw));

	}

	@Test(expected = HSQLDBException.class)
	public void testUpdateAccountBalanceNotEnoughFund() throws HSQLDBException {
		BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = h2DaoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 0);

	}

}