package com.pos.booking.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.pos.booking.domain.UserTable;

public final class UserTableRowMapper implements RowMapper<UserTable> {

	@Override
	public UserTable mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		UserTable userTable = new UserTable();
		userTable.setCode(resultSet.getString("Code"));
		userTable.setName(resultSet.getString("Name"));
		userTable.setUsers(resultSet.getString("Users"));
		userTable.setAvailablity(resultSet.getString("Availability"));
		userTable.setLocation(resultSet.getString("Location"));
		return userTable;
	}

}
