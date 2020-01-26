package com.pos.booking.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.pos.booking.domain.SaleReport;
import com.pos.booking.domain.User;
import com.pos.booking.domain.UserTable;

@Repository
public class UserRepository {

	private org.slf4j.Logger log = LoggerFactory.getLogger(MenuItemsRepository.class);

	private static final String LOGIN_VALIDATE_QUERY = "SELECT TOP(1) COUNT(ID) as login_count FROM UserDetail WHERE ID=? AND PASSWORD=? AND SessionCount=0";

	private static final String USER_LOGIN_DETAIL_QUERY = "SELECT TOP(1) ID, BRANCH, SALESMAN, HWSERIAL FROM UserDetail WHERE ID=?";

	private static final String USER_TABLE_FETCH_QUERY = "SELECT Code,Name,Users,Availability,Location,(select sum(BillAmount) from Opentables where TableCode=tblmst.Code) as total_amount FROM TABLEMASTER tblmst WHERE USERS LIKE ?";

	private static final String FETCH_SALES_MAN_DETAILS_FOR_BRANCH = "Select code,name from salesman where  branch=?";

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

	public Map<String, String> fetchSalesManBranchWise(String branchId) {
		return jdbcTemplate.query(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareCall(FETCH_SALES_MAN_DETAILS_FOR_BRANCH);
				preparedStatement.setString(1, branchId);
				return preparedStatement;
			}
		}, new ResultSetExtractor<Map<String, String>>() {

			@Override
			public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, String> salesManMap = new HashMap<>();
				while (rs.next()) {
					salesManMap.put(rs.getString("code"), rs.getString("name"));
				}
				return salesManMap;
			}

		});
	}

	public boolean updateTableStatus(String statusCode, String tableCode) {
		log.info("Updating table status with {} for tableID: {}", statusCode, tableCode);
		String UPDATE_TABLE_QUERY = new String("update TableMaster set Availability=? where code=?");
		return jdbcTemplate.update(UPDATE_TABLE_QUERY, statusCode, tableCode) > 0;
	}

	public void updateLoginAndLogout(int code, String id) {
		jdbcTemplate.update("update UserDetail set SessionCount=? where ID=?", new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, code);
				ps.setString(2, id);
			}
		});
	}

	public SaleReport getSalesReportData(String preFix) {
		String sql = "Select Sum(Case when day(DocDate)=day(Getdate()) then NetAmount else 0 end) as Today ,Sum(Case when Month(DocDate)=Month(Getdate()) then NetAmount else 0 end) as CurrentMonth,Sum(Case when Prefix=? then NetAmount else 0 end) as CurrentYear  "
				+ "From rSales Where Prefix=? And Canceled=0 ";

		return jdbcTemplate.query(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, preFix);
				ps.setString(2, preFix);
			}
		}, new ResultSetExtractor<SaleReport>() {
			@Override
			public SaleReport extractData(ResultSet rs) throws SQLException, DataAccessException {
				SaleReport saleReport = new SaleReport();
				while (rs.next()) {
					saleReport.setTodaySale(replaceDecimal(rs.getString("Today")));
					saleReport.setMonthlySale(replaceDecimal(rs.getString("CurrentMonth")));
					saleReport.setYearlySale(replaceDecimal(rs.getString("CurrentYear")));
				}
				return saleReport;
			}
		});

	}

	private String replaceDecimal(String value) {
		if(value!=null &&  value.contains(".")) {
			return value.substring(0, value.indexOf("."));
		}
		return value;
	}
}
