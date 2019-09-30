package com.pos.booking.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.MenuItems;
import com.pos.booking.service.MenuService;

@RestController
@RequestMapping("v1/menu")
public class MenuController {

	@Autowired
	private MenuService menuService;

	@GetMapping(value = "/categories")
	public ResponseEntity<List<Category>> getCategories() {
		return ResponseEntity.ok(menuService.getCategory());
	}

	@GetMapping(value = "/items")
	public ResponseEntity<List<MenuItems>> getUserTable() {
		return ResponseEntity.ok(menuService.getMenu());
	}

	@PostMapping(value = "/savecart", consumes = "application/json")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void addToCart(@RequestBody List<CartItems> cartItems, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		menuService.addToKot(cartItems);
	}

}
