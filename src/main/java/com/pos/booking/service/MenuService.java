package com.pos.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pos.booking.domain.BillDetails;
import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.MenuItems;
import com.pos.booking.domain.PaymentDetails;
import com.pos.booking.domain.TableStatus;
import com.pos.booking.repository.MenuItemsRepository;
import com.pos.booking.repository.UserRepository;
import com.pos.booking.util.BookingUtil;

/**
 * 
 * @author Ompratap
 *
 */
@Service
public class MenuService {

	private static final Logger log = LoggerFactory.getLogger(MenuService.class);

	@Autowired
	private MenuItemsRepository menuItemsRepository;

	@Autowired
	private UserRepository userRepository;

	public List<Category> getCategory() {
		return menuItemsRepository.fetchCategories();
	}

	@Transactional(readOnly = true)
	public List<MenuItems> getMenu(String tableCode) {
		return menuItemsRepository.fetchMenuItems(tableCode);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean addToKot(CartItems cartItems) {
		log.info("Adding below cart details {} ", cartItems);
		String srl = "000" + (menuItemsRepository.getSrlNumber() + 1);
		cartItems.setDoctime(
				BookingUtil.getCurrentDateTime().getHour() + ":" + BookingUtil.getCurrentDateTime().getMinute());
		cartItems.setDocdate(BookingUtil.getCurrentDate());
		cartItems.setPrefix(BookingUtil.createPrefix(LocalDate.now()));
		cartItems.setSrl(srl);
		cartItems.setType("KOT");
		userRepository.updateTableStatus(TableStatus.TABALE_STATUS_SETTLEMENT_PENDING.getStatusCode(),
				cartItems.getTableCode());
		return (menuItemsRepository.addToKot(cartItems) && menuItemsRepository.addToOpenTable(cartItems));
	}

	public CartItems getCartDetails(String tableId) {
		return menuItemsRepository.getKotDetailsById(tableId);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public CartItems genrateBillForTable(BillDetails billDetails) {
		CartItems cartItems = menuItemsRepository.getKotDetailsById(billDetails.getTableCode());
		if (cartItems != null) {
			cartItems.setRoundoff(String.valueOf(Double.valueOf(cartItems.getTotalbillAmount())
					- Math.floor(Double.valueOf(cartItems.getTotalbillAmount()))));
			LocalDateTime localDateTime = LocalDateTime.now();
			cartItems.setTouchValue(localDateTime.getYear() + "" + localDateTime.getMonthValue() + ""
					+ localDateTime.getDayOfMonth() + "" + localDateTime.getHour() + "" + localDateTime.getMinute());
			cartItems.setTaxAmt(String.valueOf(
					cartItems.getItems().stream().mapToDouble(cart -> Double.valueOf(cart.getTaxamt())).sum())); // SGST
																													// tax
			cartItems.setAddtaxamt(String.valueOf(
					cartItems.getItems().stream().mapToDouble(cart -> Double.valueOf(cart.getAddtaxAmt())).sum())); // CGST
																													// tax
			cartItems.setPrefix(BookingUtil.createPrefix(LocalDate.now()));
			cartItems.setSrl("00" + ((Double.valueOf(menuItemsRepository.getSRL(cartItems.getBranch())) + 1)));
			cartItems.setBillno(cartItems.getType() + cartItems.getSrl());	
			log.info("Generating bill for total items: {} ", cartItems.getItems().size());
			menuItemsRepository.updateRsales(cartItems);
			menuItemsRepository.updateRstock(cartItems);
			userRepository.updateTableStatus(TableStatus.TABALE_STATUS_SETTLEMENT_PENDING.getStatusCode(),
					billDetails.getTableCode());
			log.info("Cleaning KOT");
			// menuItemsRepository.clearKotTableForTable(billDetails.getTableCode());
			return cartItems;
		} else {
			log.error("No items available in cart for table code {}", billDetails.getTableCode());
			throw new RuntimeException("No items available in cart");
		}
	}

	public void updatePaymentDetails(PaymentDetails paymentDetails) {
		log.info("Updating payment details for bill no {}",paymentDetails.getSrl());
		LocalDateTime localDateTime= LocalDateTime.now();
		paymentDetails.setSettletime(localDateTime.getHour()+""+localDateTime.getMinute());
		paymentDetails.setStatus("S");
		menuItemsRepository.updatePaymentDetails(paymentDetails);
		userRepository.updateTableStatus(TableStatus.TABLE_STATUS_AVAILABLE.getStatusCode(), paymentDetails.getTableCode());
	}
}
