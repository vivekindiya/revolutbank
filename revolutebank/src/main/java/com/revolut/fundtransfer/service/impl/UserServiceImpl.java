package com.revolut.fundtransfer.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.revolut.fundtransfer.dao.UserDAO;
import com.revolut.fundtransfer.dao.impl.UserDAOImpl;
import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.User;
import com.revolut.fundtransfer.service.UserService;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserServiceImpl implements UserService {

	private static Logger log = Logger.getLogger(UserServiceImpl.class);
	
	@POST
    @Path("/create")
	@Override
	public HashMap<String, Object> createUser(User user) {
		UserDAO userDAO = new UserDAOImpl();
		long userId = -1;
		try {
			if (userDAO.getUserByName(user.getUserName()) != null) {
				throw new WebApplicationException("User name already exist", Response.Status.BAD_REQUEST);
			}
			userId = userDAO.createUser(user);
		} catch (HSQLDBException e) {
			log.error("Error : createUser() " + e);
			e.printStackTrace();
		}

		HashMap<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("UserId", userId);
		responseMap.put("status", "SUCCESS");

		return responseMap;
	}

	@PUT
    @Path("/{userId}")
	@Override
	public Response updateUser(@PathParam("userId") long userId, User user) {
		UserDAO userDAO = new UserDAOImpl();
		int updateCount = 0;
		try {
			updateCount = userDAO.updateUser(userId, user);
		} catch (HSQLDBException e) {
			log.error("Error : updateUser() " + e);
			e.printStackTrace();
		}
        if (updateCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
	}
	
	@GET
    @Path("/all")
	@Override
	public List<User> getAllUsers() {
		UserDAO userDAO = new UserDAOImpl();
		List<User> users = null;
		try {
			users = userDAO.getAllUsers();
		} catch (HSQLDBException e) {
			log.error("Error : getAllUsers() " + e);
			e.printStackTrace();
		}
		return users;
	}

	@DELETE
    @Path("/{userId}")
	@Override
	public Response deleteUser(@PathParam("userId") long userId) {
		UserDAO userDAO = new UserDAOImpl();
		int deleteCount = 0;
		try {
			deleteCount = userDAO.deleteUser(userId);
		} catch (HSQLDBException e) {
			log.error("Error : deleteUser() " + e);
			e.printStackTrace();
		}
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
	}

	@GET
    @Path("/{userId}")
	@Override
	public User getUserById(@PathParam("userId") long userId) {
		UserDAO userDAO = new UserDAOImpl();
		if (log.isDebugEnabled())
            log.debug("getUserById() : request " + userId);
        User user = null;
		try {
			user = userDAO.getUserById(userId);
		} catch (HSQLDBException e) {
			log.error("Error : getUserByName() " + e);
			e.printStackTrace();
		}
        if (user == null) {
            throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
        }
        return user;
	}
}
