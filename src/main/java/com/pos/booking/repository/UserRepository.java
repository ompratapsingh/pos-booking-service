package com.pos.booking.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pos.booking.domain.User;
import com.pos.booking.domain.UserTable;

@Repository
public class UserRepository {

	private static final String LOGIN_VALIDATE_QUERY = "SELECT TOP(1) COUNT(ID) as login_count FROM UserDetail WHERE ID=? AND PASSWORD=?";

	private static final String USER_LOGIN_DETAIL_QUERY = "SELECT TOP(1) ID, BRANCH, SALESMAN, HWSERIAL FROM UserDetail WHERE ID=?";

	private static final String USER_TABLE_FETCH_QUERY = "SELECT Code,Name,Users,Availability,Location FROM TABLEMASTER WHERE USERS LIKE ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean isValidUser(User user) {
		return jdbcTemplate.query(LOGIN_VALIDATE_QUERY, preparedStatement -> {
			preparedStatement.setString(1, user.getId());
			preparedStatement.setString(2, user.getPassword());
		}, new ResultSetExtractor<Boolean>() {

			@Override
			public Boolean extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				if (resultSet.next()) {
					return resultSet.getInt("login_count") > 0;
				}
				return false;
			}

		});
	}

	public User fetchUserDetails(String id) {
		return jdbcTemplate.query(USER_LOGIN_DETAIL_QUERY, new String[] { id }, new ResultSetExtractor<User>() {
			User user = null;

			@Override
			public User extractData(ResultSet resultSet) throws SQLException {
				if (resultSet.next()) {
					user = new User();
					user.setId(resultSet.getString("ID"));
					user.setBranch(resultSet.getString("BRANCH"));
					user.setHWSerial(resultSet.getString("HWSERIAL"));
					user.setSalesman(resultSet.getString("SALESMAN"));
				}
				return user;
			}
		});
	}

	public List<UserTable> fetchUserTables(String id) {

		return jdbcTemplate.query(USER_TABLE_FETCH_QUERY, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, "%" + id + "%");
			}
		}, new UserTableRowMapper());

	}

}
