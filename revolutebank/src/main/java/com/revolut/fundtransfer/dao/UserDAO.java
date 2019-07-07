package com.revolut.fundtransfer.dao;

import java.util.List;

import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.User;

/**
 * 
 * @author Vivek Shah
 *
 */
public interface UserDAO {

	/**
	 * @return userId generated from insertion, return -1 on error
	 */
	public long createUser(User user) throws HSQLDBException;

	public int updateUser(Long userId, User user) throws HSQLDBException;

	public int deleteUser(long userId) throws HSQLDBException;

	public List<User> getAllUsers() throws HSQLDBException;

	public User getUserById(long userId) throws HSQLDBException;

	public User getUserByName(String userName) throws HSQLDBException;
}
