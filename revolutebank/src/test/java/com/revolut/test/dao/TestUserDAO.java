package com.revolut.test.dao;

import static junit.framework.TestCase.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revolut.fundtransfer.dao.DBDAOFactory;
import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.User;

public class TestUserDAO {
	
	private static Logger log = Logger.getLogger(TestUserDAO.class);
	
	private static final DBDAOFactory h2dbDAO = DBDAOFactory.getDAOFactory(DBDAOFactory.H2DB);

	@BeforeClass
	public static void setup() {
		log.debug("Creating content for the database...");
		h2dbDAO.createDBContent();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetAllUsers() throws HSQLDBException {
		List<User> allUsers = h2dbDAO.getUserDAO().getAllUsers();
		assertTrue(allUsers.size() > 1);
	}

	@Test
	public void testGetUserById() throws HSQLDBException {
		User u = h2dbDAO.getUserDAO().getUserById(2L);
		assertTrue(u.getUserName().equals("jhon"));
	}

	@Test
	public void testGetNonExistingUserById() throws HSQLDBException {
		User u = h2dbDAO.getUserDAO().getUserById(500L);
		assertTrue(u == null);
	}

	@Test
	public void testGetNonExistingUserByName() throws HSQLDBException {
		User u = h2dbDAO.getUserDAO().getUserByName("abcdeftg");
		assertTrue(u == null);
	}

	@Test
	public void testCreateUser() throws HSQLDBException {
		User user = new User("vivek", "vivek@gmail.com");
		long id = h2dbDAO.getUserDAO().createUser(user);
		User uAfterInsert = h2dbDAO.getUserDAO().getUserById(id);
		assertTrue(uAfterInsert.getUserName().equals("vivek"));
		assertTrue(user.getEmailAddress().equals("vivek@gmail.com"));
	}

	@Test
	public void testUpdateUser() throws HSQLDBException {
		User user = new User(1L, "vivek_new", "vivek_new@gmail.com");
		int rowCount = h2dbDAO.getUserDAO().updateUser(1L, user);
		// assert one row(user) updated
		assertTrue(rowCount == 1);
		assertTrue(h2dbDAO.getUserDAO().getUserById(1L).getEmailAddress().equals("vivek_new@gmail.com"));
	}

	@Test
	public void testUpdateNonExistingUser() throws HSQLDBException {
		User u = new User(500L, "test2", "test2@gmail.com");
		int rowCount = h2dbDAO.getUserDAO().updateUser(500L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 0);
	}

	@Test
	public void testDeleteUser() throws HSQLDBException {
		int rowCount = h2dbDAO.getUserDAO().deleteUser(1L);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(h2dbDAO.getUserDAO().getUserById(1L) == null);
	}

	@Test
	public void testDeleteNonExistingUser() throws HSQLDBException {
		int rowCount = h2dbDAO.getUserDAO().deleteUser(500L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

}
