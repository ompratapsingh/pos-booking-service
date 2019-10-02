package com.pos.booking.resource;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pos.booking.domain.User;
import com.pos.booking.domain.UserTable;
import com.pos.booking.service.UserService;

@RestController
@RequestMapping("v1/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestBody User user) {
		return ResponseEntity.ok(userService.fetchLoginDetails(user));
	}

	@GetMapping(value = "/table/{id}")
	public ResponseEntity<List<UserTable>> getUserTable(@PathVariable(required = true, name = "id") String id) {
		return ResponseEntity.ok(userService.getUserTables(id));
	}

	@GetMapping(value = "/{branchId}/salesman")
	public ResponseEntity<Map<String, String>> getSalesMan(@PathVariable("branchId") String branchId) {
		return ResponseEntity.ok(userService.getSalesMan(branchId));
	}
}