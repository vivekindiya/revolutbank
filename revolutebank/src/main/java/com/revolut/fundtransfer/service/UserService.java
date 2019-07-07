package com.revolut.fundtransfer.service;

import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import com.revolut.fundtransfer.model.User;

public interface UserService {
	
	public HashMap<String, Object> createUser(User user);

	public Response updateUser(long userId, User user);

	public List<User> getAllUsers();

	public User getUserById(long userId);
		
	public Response deleteUser(long userId);
}
