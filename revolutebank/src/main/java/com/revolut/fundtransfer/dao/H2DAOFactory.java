package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.dao.impl.AccountDAOImpl;
import com.revolut.fundtransfer.dao.impl.UserDAOImpl;
import com.revolut.fundtransfer.utils.PropertiesLoader;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Vivek Shah
 * Properties are fetched from application.properties
 *
 */
public class H2DAOFactory extends DBDAOFactory {
	private static final String h2_driver = PropertiesLoader.getStringProperty("h2_driver");
	private static final String h2_connection_url = PropertiesLoader.getStringProperty("h2_connection_url");
	private static final String h2_user = PropertiesLoader.getStringProperty("h2_user");
	private static final String h2_password = PropertiesLoader.getStringProperty("h2_password");
	private static Logger log = Logger.getLogger(H2DAOFactory.class);

	private final UserDAOImpl userDAO = new UserDAOImpl();
	private final AccountDAOImpl accountDAO = new AccountDAOImpl();

	static {
		// Load the driver class 
		try {
			Class.forName(h2_driver);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			log.error("Unable to load driver class : ", e);
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(h2_connection_url, h2_user, h2_password);

	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public AccountDAO getAccountDAO() {
		return accountDAO;
	}

	@Override
	public void createDBContent() {
		log.info("Populating Test User Table and data ..... ");
		Connection conn = null;
		try {
			conn = H2DAOFactory.getConnection();
			RunScript.execute(conn, new FileReader("src/test/resources/content.sql"));
		} catch (SQLException e) {
			log.error("populateTestData(): Error populating user data: ", e);
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			log.error("populateTestData(): Error finding test script file ", e);
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

}
