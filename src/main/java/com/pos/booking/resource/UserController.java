package com.pos.booking.resource;

import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pos.booking.domain.SaleReport;
import com.pos.booking.domain.User;
import com.pos.booking.domain.UserTable;
import com.pos.booking.service.UserService;

@RestController
@RequestMapping("v1/user")
public class UserController {

	private org.slf4j.Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestBody User user) {
		log.info("Calling login service for user <{}>", user.getId());
		return ResponseEntity.ok(userService.fetchLoginDetails(user));
	}

	@GetMapping(value = "/table/{id}")
	public ResponseEntity<List<UserTable>> getUserTable(@PathVariable(required = true, name = "id") String id) {
		log.info("Calling getUserTable service..");
		return ResponseEntity.ok(userService.getUserTables(id));
	}

	@GetMapping(value = "/{branchId}/salesman")
	public ResponseEntity<Map<String, String>> getSalesMan(@PathVariable("branchId") String branchId) {
		log.info("Calling getSalesMan service..");
		return ResponseEntity.ok(userService.getSalesMan(branchId));
	}

	@PatchMapping(value = "/table/{tableCode}/status/{status}")
	public void updateTableStatus(@PathVariable("tableCode") String tableCode, @PathVariable("status") String status) {
		log.info("Calling updateTableStatus service..");
		userService.updateTableStatus(status, tableCode);
	}

	@GetMapping(value = "/logout/{id}")
	public void logOut(@PathVariable("id") String id) {
		log.info("Calling logOut service for user: {} ", id);
		userService.logOutUpdate(id);
	}

	@GetMapping(value = "/getReport")
	public ResponseEntity<SaleReport> getReport() {
		return ResponseEntity.ok(userService.getSalesReport());
	}
}