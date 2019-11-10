package com.pos.booking.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
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
import com.pos.booking.exception.DaoException;
import com.pos.booking.service.MenuService;

@RestController
@RequestMapping("v1/menu")
public class MenuController {
	
	private org.slf4j.Logger log = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	private MenuService menuService;

	@GetMapping(value = "/categories")
	public ResponseEntity<List<Category>> getCategories() {
		log.info("Calling getCategories service..");
		return ResponseEntity.ok(menuService.getCategory());
	}

	@GetMapping(value = "/{id}/items")
	public ResponseEntity<List<MenuItems>> getUserTable(@PathVariable("id") String id) {
		log.info("Calling getUserTable service..");
		return ResponseEntity.ok(menuService.getMenu(id));
	}

	@PostMapping(value = "/savecart", consumes = "application/json")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void addToCart(@RequestBody CartItems cart, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		try {
			log.info("Calling savecart service..");
			menuService.addToKot(cart);
		} catch (DaoException e) {
			throw new Exception("Failed"); 
		}
	}

	@GetMapping(value = "/{tableId}/cart", consumes = "application/json")
	public ResponseEntity<CartItems> getCartDetails(@PathVariable("tableId") String tableId) {
		log.info("Calling getCartDetails service..");
		return ResponseEntity.ok(menuService.getCartDetails(tableId));
	}

	@PostMapping(value = "/generateBill", consumes = "application/json")
	public ResponseEntity<CartItems> generateBill(@RequestBody BillDetails billdetails) {
		log.info("Calling generateBill service..");
		return ResponseEntity.ok(menuService.generateBillForTable(billdetails));
	}
	
	@PostMapping(value = "/paymentSettlement", consumes = "application/json")
	public void updatePaymentDetails(@RequestBody PaymentDetails paymentDetails) {
		log.info("Calling paymentSettlement service..");
		menuService.updatePaymentDetails(paymentDetails);
	}
}
