package com.pos.booking.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.booking.domain.TableStatus;
import com.pos.booking.domain.User;
import com.pos.booking.domain.UserTable;
import com.pos.booking.exception.UserNotFoundException;
import com.pos.booking.repository.UserRepository;

/**
 * 
 * @author Ompratap
 *
 */
@Service
public class UserService {

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
		return repository.fetchUserTables(id);
	}

	public Map<String, String> getSalesMan(String branchId) {
		return repository.fetchSalesManBranchWise(branchId);
	}

	public boolean updateTableStatus(String statusCode, String tableCode) {
		return repository.updateTableStatus(TableStatus.valueOf(statusCode).getStatusCode(), tableCode);
	}
	
	public void logOutUpdate(String id) {
		repository.updateLoginAndLogout(0,  id);
	}
}
