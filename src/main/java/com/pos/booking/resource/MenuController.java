package com.pos.booking.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pos.booking.domain.BillDetails;
import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.MenuItems;
import com.pos.booking.domain.PaymentDetails;
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

	@GetMapping(value = "/{id}/items")
	public ResponseEntity<List<MenuItems>> getUserTable(@PathVariable("id") String id) {
		return ResponseEntity.ok(menuService.getMenu(id));
	}

	@PostMapping(value = "/savecart", consumes = "application/json")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void addToCart(@RequestBody CartItems cart, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		menuService.addToKot(cart);
	}

	@GetMapping(value = "/{tableId}/cart", consumes = "application/json")
	public ResponseEntity<CartItems> getCartDetails(@PathVariable("tableId") String tableId) {
		return ResponseEntity.ok(menuService.getCartDetails(tableId));
	}

	@PostMapping(value = "/generateBill", consumes = "application/json")
	public ResponseEntity<CartItems> genrateBill(@RequestBody BillDetails billdetails) {
		return ResponseEntity.ok(menuService.genrateBillForTable(billdetails));
	}

	@PostMapping(value = "/paymentSettlement", consumes = "application/json")
	public void updatePaymentDetails(@RequestBody PaymentDetails paymentDetails) {
		menuService.updatePaymentDetails(paymentDetails);
	}
}
