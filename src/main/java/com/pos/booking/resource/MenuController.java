package com.pos.booking.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pos.booking.domain.Category;
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

}
