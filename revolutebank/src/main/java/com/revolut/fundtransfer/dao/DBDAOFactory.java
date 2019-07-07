package com.revolut.fundtransfer.dao;

/**
 * 
 * @author Vivek Shah
 * To change the database we will have to implement the api and provide it in the factory
 *
 */
public abstract class DBDAOFactory {

	public static final int H2DB = 1;

	public abstract UserDAO getUserDAO();

	public abstract AccountDAO getAccountDAO();

	public abstract void createDBContent();

	public static DBDAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2DB:
			return new H2DAOFactory();
		default:
			return new H2DAOFactory(); // default database
		}
	}
}
