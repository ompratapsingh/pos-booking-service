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

import com.pos.booking.cache.ApplicationCahce;
import com.pos.booking.domain.BillDetails;
import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Category;
import com.pos.booking.domain.Items;
import com.pos.booking.domain.MenuItems;
import com.pos.booking.domain.PaymentDetails;
import com.pos.booking.domain.RestaurantDetails;
import com.pos.booking.domain.TableStatus;
import com.pos.booking.exception.DaoException;
import com.pos.booking.print.service.PrinterService;
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

	@Autowired
	private PrinterService printerService;

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
		cartItems.setSystemDate(BookingUtil.getCurrentDateinMill());
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
		// String printContent = PrintServiceUtil.getKotPrintContent(cartItems);
		// log.info("Kot print content {}",printContent);
		// printerService.printString("PrinterName", printContent);
		log.info("Sucessfully Added To KOT with status code {}", status);
		return status;
	}

	public CartItems getCartDetails(String tableId) {
		log.info("Calling cart details service for tableId: {}", tableId);
		CartItems cartItems = menuItemsRepository.getKotDetailsById(tableId);
		if (null == cartItems) {
			return new CartItems();
		}
		log.info("Total item ordered: <{}> for table ID: <{}>", cartItems.getItems(), tableId);
		String tableStatus = menuItemsRepository.getTableStatus(tableId);
		cartItems.setTableStatus(tableStatus);
		cartItems.setBillGenerate(TableStatus.TABLE_STATUS_SETTLEMENT_PENDING.getStatusCode().equals(tableStatus));
		return cartItems;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public CartItems generateBillForTable(BillDetails billDetails) {
		log.info("Generate Bill Request accepted for table ID <{}>", billDetails.getTableCode());
		CartItems cartItems = menuItemsRepository.getKotDetailsById(billDetails.getTableCode());
		if (cartItems != null) {
			String billType = menuItemsRepository.getBillType(cartItems.getStoreCode());
			log.info("Bill Type is: <{}> for store code: <{}> ", billType, cartItems.getStoreCode());
			cartItems.setType(billType);
			cartItems.setDocdate(BookingUtil.getCurrentDate());
			LocalDateTime localDateTime = LocalDateTime.now();
			cartItems.setTouchValue(localDateTime.getYear() + "" + localDateTime.getMonthValue() + ""
					+ localDateTime.getDayOfMonth() + "" + localDateTime.getHour() + "" + localDateTime.getMinute());
			cartItems.setTaxAmt(String.valueOf(
					cartItems.getItems().stream().mapToDouble(cart -> Double.valueOf(cart.getTaxamt())).sum())); // SGST
			cartItems.setAddtaxamt(String.valueOf(
					cartItems.getItems().stream().mapToDouble(cart -> Double.valueOf(cart.getAddtaxAmt())).sum())); // CGST
			//tax
			cartItems.setPrefix(BookingUtil.createPrefix(LocalDate.now()));
			cartItems.setDoctime(
					BookingUtil.getCurrentDateTime().getHour() + ":" + BookingUtil.getCurrentDateTime().getMinute());
			cartItems.setSystemDate(BookingUtil.getCurrentDateinMill());
			cartItems.setSepecialDiscount(billDetails.getSpecialDiscount());
			cartItems.setEnteredBy(billDetails.getEnteredBy());
			if (Integer.valueOf(billDetails.getSpecialDiscount()) > 0) {
				double disCount = (Double.valueOf(billDetails.getSpecialDiscount()) / 100)
						* Double.valueOf(cartItems.getTotalbillAmount());
				disCount = Math.floor(disCount);
				String payableAmount = String
						.valueOf(Math.round(Double.valueOf(cartItems.getTotalbillAmount()))-disCount);
				
				cartItems.setTotalbillAmount(payableAmount);
				
				double totalDiscAmnt = Double.valueOf(cartItems.getTotalDiscAmt()) + disCount;
				cartItems.setTotalDiscAmt(String.valueOf(totalDiscAmnt));
				ApplicationCahce<String, String> applicationCahce = new ApplicationCahce<>();
				applicationCahce.put(billDetails.getTableCode(), String.valueOf(totalDiscAmnt));
				log.info("Discount Amount: {} and Payable Amount: {} for table ID: {}", disCount, payableAmount,
						billDetails.getTableCode());
			}
			long netAmount = 0;
			for(Items items: cartItems.getItems()) {
				netAmount = netAmount + (Double.valueOf(items.getRate()).intValue()* Double.valueOf(items.getQty()).intValue());
			}
			cartItems.setNetAmount(String.valueOf(netAmount));
			cartItems.setDisPrcnt(Integer.valueOf(billDetails.getSpecialDiscount()));
			boolean isExist = menuItemsRepository.isSrlExist(cartItems.getBranch(), billDetails.getSrl());
			log.info("Is srl exist for srl: {} and branch: {} status: {} ", billDetails.getSrl(), cartItems.getBranch(),
					isExist);
			if (isExist) {
				log.info("Bill allready generated for SRL: {}", billDetails.getSrl());
				cartItems.setBillGenerate(true);
				return cartItems;
			}
			String srl = "00" + ((Integer.valueOf(menuItemsRepository.getSRL(cartItems.getBranch(),cartItems.getPrefix(),cartItems.getType())) + 1));
			log.info("Genrated new SRL:  {} for table ID: {}", srl, billDetails.getTableCode());
			cartItems.setSrl(srl);
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
			RestaurantDetails restaurantDetails = menuItemsRepository.getrestaurantDetails();
			cartItems.setRestaurantDetails(restaurantDetails);
			return cartItems;
		} else {
			log.error("No items available in cart for table code {}", billDetails.getTableCode());
			// will refactor
			throw new RuntimeException("No items available in cart");
		}
	}

	public void updatePaymentDetails(PaymentDetails paymentDetails) {
		log.info("Updating payment details for table details: {}", paymentDetails);
		LocalDateTime localDateTime = LocalDateTime.now();
		paymentDetails.setSettletime(localDateTime.getHour() + ":" + localDateTime.getMinute());
		paymentDetails.setStatus("S");
		menuItemsRepository.updatePaymentDetails(paymentDetails);
		log.info("Cleaning KOT");
		menuItemsRepository.clearKotTableForTable(paymentDetails.getTableCode());
		userRepository.updateTableStatus(TableStatus.TABLE_STATUS_AVAILABLE.getStatusCode(),
				paymentDetails.getTableCode());
		ApplicationCahce<String, String> applicationCahce = new ApplicationCahce<>();
		applicationCahce.remove(paymentDetails.getTableCode());
		log.info("Settlement done for table ID <{}>.Current table status: {}", paymentDetails.getTableCode(),
				menuItemsRepository.getTableStatus(paymentDetails.getTableCode()));
	}

}
