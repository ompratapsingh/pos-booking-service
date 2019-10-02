package com.pos.booking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.MenuItems;
import com.pos.booking.repository.MenuItemsRepository;
import com.pos.booking.util.BookingUtil;

/**
 * 
 * @author Ompratap
 *
 */
@Service
public class MenuService {

	@Autowired
	private MenuItemsRepository menuItemsRepository;

	public List<Category> getCategory() {
		return menuItemsRepository.fetchCategories();
	}

	@Transactional(readOnly = true)
	public List<MenuItems> getMenu(String tableCode) {
		return menuItemsRepository.fetchMenuItems(tableCode);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean addToKot(CartItems cartItems) {
		String srl = "000" + (menuItemsRepository.getSrlNumber() + 1);
		cartItems.setDoctime(
				BookingUtil.getCurrentDateTime().getHour() + ":" + BookingUtil.getCurrentDateTime().getMinute());
		cartItems.setDocdate(BookingUtil.getCurrentDate());
		cartItems.setPrefix(BookingUtil.createPrefix(LocalDate.now()));
		cartItems.setSrl(srl);
		cartItems.setType("KOT");
		return (menuItemsRepository.addToKot(cartItems) && menuItemsRepository.addToOpenTable(cartItems));
	}
}
