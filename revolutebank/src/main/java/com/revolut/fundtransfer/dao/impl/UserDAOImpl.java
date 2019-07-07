package com.revolut.fundtransfer.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.revolut.fundtransfer.dao.H2DAOFactory;
import com.revolut.fundtransfer.dao.UserDAO;
import com.revolut.fundtransfer.exception.HSQLDBException;
import com.revolut.fundtransfer.model.User;
import com.revolut.fundtransfer.utils.PropertiesLoader;

/**
 * @author Vivek Shah
 */
public class UserDAOImpl implements UserDAO {

	private static Logger log = Logger.getLogger(UserDAOImpl.class);
	
	@Override
	public long createUser(User user) throws HSQLDBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("INSERT_USER"), Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmailAddress());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.error("createUser(): Unable to create user" + user);
                throw new HSQLDBException("Unable to create user");
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                log.error("createUser(): Unable to create user, no ID generated" + user);
                throw new HSQLDBException("Unable to create user, no ID generated");
            }
        } catch (SQLException e) {
            log.error("Error creating user :" + user);
            throw new HSQLDBException("Error creating user :", e);
        } finally {
            DbUtils.closeQuietly(conn,stmt,generatedKeys);
        }
    }

	@Override
	public int updateUser(Long userId, User user) throws HSQLDBException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("UPDATE_USER"));
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmailAddress());
            stmt.setLong(3, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating user :" + user);
            throw new HSQLDBException("Error updating user :", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

	@Override
	public int deleteUser(long userId) throws HSQLDBException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("DELETE_USER"));
            stmt.setLong(1, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting user :" + userId);
            throw new HSQLDBException("Error deleting user :"+ userId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

	@Override
	public List<User> getAllUsers() throws HSQLDBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<User>();
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("GET_ALL_USERS"));
            rs = stmt.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                users.add(u);
                if (log.isDebugEnabled())
                    log.debug("getAllUsers() : Unable to fetch data : " + u);
            }
            return users;
        } catch (SQLException e) {
            throw new HSQLDBException("Unable to fetch data : ", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

	@Override
	public User getUserById(long userId) throws HSQLDBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User u = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("GET_USER_BY_ID"));
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                if (log.isDebugEnabled())
                    log.debug("getUserById(): Retrieved user: " + u);
            }
            return u;
        } catch (SQLException e) {
            throw new HSQLDBException("Error reading data : ", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

	@Override
	public User getUserByName(String userName) throws HSQLDBException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User u = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(PropertiesLoader.getStringProperty("GET_USER_BY_NAME"));
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                if (log.isDebugEnabled())
                    log.debug("Retrieved user: " + u);
            }
            return u;
        } catch (SQLException e) {
            throw new HSQLDBException("Error reading data : ", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }
}
