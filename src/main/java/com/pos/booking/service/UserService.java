package com.pos.booking.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.booking.cache.ApplicationCahce;
import com.pos.booking.domain.SaleReport;
import com.pos.booking.domain.TableStatus;
import com.pos.booking.domain.User;
import com.pos.booking.domain.UserTable;
import com.pos.booking.exception.UserNotFoundException;
import com.pos.booking.repository.UserRepository;
import com.pos.booking.util.BookingUtil;

/**
 * 
 * @author Ompratap
 *
 */
@Service
public class UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository repository;

	public User fetchLoginDetails(User user) {
		if (repository.isValidUser(user)) {
			repository.updateLoginAndLogout(1, user.getId());
			return repository.fetchUserDetails(user.getId());
		} else {
			throw new UserNotFoundException("Invalid User");
		}
	}

	public List<UserTable> getUserTables(String id) {
		log.info("Calling userTable service method...");
		List<UserTable> userTables = repository.fetchUserTables(id);
		List<UserTable> userTables2 = new ArrayList<>();
		ApplicationCahce<String, String> applicationCahce = new ApplicationCahce<>();
		for (UserTable userTable : userTables) {
			if (applicationCahce.getValue(userTable.getCode()) != null) {
				double totalAmnt = Double.valueOf(userTable.getAmount());
				double discAmnt = Double.valueOf(applicationCahce.getValue(userTable.getCode()));
				log.info("Computing table display value for tableID: {}, totalAmnt: {}, discAmnt: {} ",userTable.getCode(),totalAmnt,discAmnt);
				double afterDiscAmnt = totalAmnt - Math.round(discAmnt);
				log.info("After discAmnt: {} ",afterDiscAmnt);
				userTable.setAmount(String.valueOf(afterDiscAmnt));
				log.info("Verify: {}",userTable.getAmount());
			}
			userTables2.add(userTable);
		}
		return userTables2;
	}

	public Map<String, String> getSalesMan(String branchId) {
		return repository.fetchSalesManBranchWise(branchId);
	}

	public boolean updateTableStatus(String statusCode, String tableCode) {
		return repository.updateTableStatus(TableStatus.valueOf(statusCode).getStatusCode(), tableCode);
	}

	public void logOutUpdate(String id) {
		repository.updateLoginAndLogout(0, id);
	}

	public SaleReport getSalesReport() {
		return repository.getSalesReportData(BookingUtil.createPrefix(LocalDate.now()));
	}
}
