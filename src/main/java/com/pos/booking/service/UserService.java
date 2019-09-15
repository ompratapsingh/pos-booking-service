package com.pos.booking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.booking.domain.User;
import com.pos.booking.domain.UserTable;
import com.pos.booking.exception.UserNotFoundException;
import com.pos.booking.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public User fetchLoginDetails(User user) {
		if (repository.isValidUser(user)) {
			return repository.fetchUserDetails(user.getId());
		} else {
			throw new UserNotFoundException("Invalid User");
		}
	}

	public List<UserTable> getUserTables(String id) {
		return repository.fetchUserTables(id);
	}
}
