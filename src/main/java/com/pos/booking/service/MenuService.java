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
import com.pos.booking.exception.DaoException;
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
		List<Category> categories = menuItemsRepository.fetchCategories();
		log.info("Available total category is: {}", categories.size());
		return categories;
	}

	@Transactional(readOnly = true)
	public List<MenuItems> getMenu(String tableCode) {
		List<MenuItems> menuItems = menuItemsRepository.fetchMenuItems(tableCode);
		log.info("Available total menu items is: <{}>  for tablecode: <{}> ", menuItems.size(), tableCode);
		return menuItems;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = DaoException.class)
	public boolean addToKot(CartItems cartItems) throws DaoException {
		log.info("Adding cart details: {} ", cartItems);
		String srl = "00" + (menuItemsRepository.getSrlNumber() + 1);
		cartItems.setDoctime(
				BookingUtil.getCurrentDateTime().getHour() + ":" + BookingUtil.getCurrentDateTime().getMinute());
		cartItems.setDocdate(BookingUtil.getCurrentDate());
		cartItems.setPrefix(BookingUtil.createPrefix(LocalDate.now()));
		cartItems.setSrl(srl);
		cartItems.setType("KOT");
		if (null == cartItems.getPaxNo()) {
			cartItems.setPaxNo("0");
		}
		if (null == cartItems.getCaptain()) {
			cartItems.setCaptain("0");
		}
		userRepository.updateTableStatus(TableStatus.TABLE_STATUS_OCCUPIED.getStatusCode(), cartItems.getTableCode());
		boolean status = menuItemsRepository.addToKot(cartItems) && menuItemsRepository.addToOpenTable(cartItems);
		log.info("Sucessfully Added To KOT with status code {}", status);
		return status;
	}

	public CartItems getCartDetails(String tableId) {
		CartItems cartItems = menuItemsRepository.getKotDetailsById(tableId);
		String tableStatus = menuItemsRepository.getTableStatus(tableId);
		cartItems.setTableStatus(tableStatus);
		cartItems.setRoundoff(String.valueOf(Double.valueOf(cartItems.getTotalbillAmount())
				- Math.floor(Double.valueOf(cartItems.getTotalbillAmount()))));
		log.info("Total item ordered: <{}> for table ID: <{}>", cartItems.getItems(), tableId);
		boolean status = TableStatus.TABLE_STATUS_SETTLEMENT_PENDING.getStatusCode().equals(tableStatus);
		cartItems.setBillGenerate(status);
		return cartItems;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public CartItems generateBillForTable(BillDetails billDetails) {
		log.info("Generate Bill Request accepted for table ID <{}>", billDetails.getTableCode());
		CartItems cartItems = menuItemsRepository.getKotDetailsById(billDetails.getTableCode());
		if (cartItems != null) {
			String billType = menuItemsRepository.getBillType(cartItems.getStoreCode());
			log.info("Bill Type is: <{}> for store code: <{}> ",billType,cartItems.getStoreCode());
			cartItems.setType(billType);
			cartItems.setRoundoff(String.valueOf(Double.valueOf(cartItems.getTotalbillAmount())
					- Math.floor(Double.valueOf(cartItems.getTotalbillAmount()))));
			cartItems.setDocdate(BookingUtil.getCurrentDate());
			LocalDateTime localDateTime = LocalDateTime.now();
			cartItems.setTouchValue(localDateTime.getYear() + "" + localDateTime.getMonthValue() + ""
					+ localDateTime.getDayOfMonth() + "" + localDateTime.getHour() + "" + localDateTime.getMinute());
			cartItems.setTaxAmt(String.valueOf(
					cartItems.getItems().stream().mapToDouble(cart -> Double.valueOf(cart.getTaxamt())).sum())); // SGST
			cartItems.setAddtaxamt(String.valueOf(
					cartItems.getItems().stream().mapToDouble(cart -> Double.valueOf(cart.getAddtaxAmt())).sum())); // CGST
			// tax
			cartItems.setPrefix(BookingUtil.createPrefix(LocalDate.now()));
			cartItems.setDoctime(BookingUtil.getCurrentDateTime().getHour() + ":" + BookingUtil.getCurrentDateTime().getMinute());
			cartItems.setSystemDate(BookingUtil.getCurrentDateinMill());
			cartItems.setSepecialDiscount(billDetails.getSpecialDiscount());
			if (Integer.valueOf(billDetails.getSpecialDiscount()) > 0) {
				double disCount = (Double.valueOf(billDetails.getSpecialDiscount()) / 100)
						* Double.valueOf(cartItems.getTotalbillAmount());
				String payableAmount = String.valueOf(Double.valueOf(cartItems.getTotalbillAmount()) - disCount);
				cartItems.setTotalbillAmount(payableAmount);
				double totalDiscAmnt = Double.valueOf(cartItems.getTotalDiscAmt()) + disCount;
				cartItems.setTotalDiscAmt(String.valueOf(totalDiscAmnt));
				log.info("Discount Amount: {} and Payable Amount: {} for table ID: {}", disCount, payableAmount,
						billDetails.getTableCode());
			}
			cartItems.setDisPrcnt(Integer.valueOf(billDetails.getSpecialDiscount()));
			boolean isExist = menuItemsRepository.isSrlExist(cartItems.getBranch(), billDetails.getSrl());
			log.info("Is srl exist for srl: {} and branch: {} status: {} ",billDetails.getSrl(), cartItems.getBranch(),isExist);
			if (isExist) {
				log.info("Bill is generated already for SRL: {}", billDetails.getSrl());
				cartItems.setBillGenerate(true);
				return cartItems;
			}
			cartItems.setSrl("00" + ((Integer.valueOf(menuItemsRepository.getSRL(cartItems.getBranch())) + 1)));
			cartItems.setBillno(cartItems.getType() + cartItems.getSrl());
			log.info("Generating bill for total items: {} ", cartItems.getItems().size());
			menuItemsRepository.updateRsales(cartItems);
			menuItemsRepository.updateRstock(cartItems);
			userRepository.updateTableStatus(TableStatus.TABLE_STATUS_SETTLEMENT_PENDING.getStatusCode(),
					billDetails.getTableCode());
			cartItems.setTableStatus(menuItemsRepository.getTableStatus(billDetails.getTableCode()));
			log.info("Generate Bill Request sucessfully completd for total item's: <{}> and total amount: <{}>",
					cartItems.getItems(), cartItems.getTotalbillAmount());
			cartItems.setBillGenerate(true);
			return cartItems;
		} else {
			log.error("No items available in cart for table code {}", billDetails.getTableCode());
			// will refactor
			throw new RuntimeException("No items available in cart");
		}
	}

	public void updatePaymentDetails(PaymentDetails paymentDetails) {
		log.info("Updating payment details for bill no {}", paymentDetails.getSrl());
		LocalDateTime localDateTime = LocalDateTime.now();
		paymentDetails.setSettletime(localDateTime.getHour() + "" + localDateTime.getMinute());
		paymentDetails.setStatus("S");
		menuItemsRepository.updatePaymentDetails(paymentDetails);
		log.info("Cleaning KOT");
		menuItemsRepository.clearKotTableForTable(paymentDetails.getTableCode());
		userRepository.updateTableStatus(TableStatus.TABLE_STATUS_AVAILABLE.getStatusCode(),paymentDetails.getTableCode());
		log.info("Settlement done for table ID <{}>.Current table status: {}", paymentDetails.getTableCode(),
		menuItemsRepository.getTableStatus(paymentDetails.getTableCode()));
	}

}
