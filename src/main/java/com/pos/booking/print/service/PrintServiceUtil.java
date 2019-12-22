package com.pos.booking.print.service;

import com.pos.booking.domain.CartItems;
import com.pos.booking.domain.Items;

public class PrintServiceUtil {

	public static String getKotPrintContent(CartItems cartItems) {

		StringBuilder invoiceData = new StringBuilder();
		invoiceData.append("Dept").append(space(3)).append(IPrintConstant.COLON_STR).append("INDIAN1")
				.append(IPrintConstant.NEWLINE_STR);
		invoiceData.append(IPrintConstant.LINEBREAK_STR).append(IPrintConstant.NEWLINE_STR);
		invoiceData.append("TABLE").append(space(1)).append(IPrintConstant.COLON_STR).append(space(1))
				.append(cartItems.getTableCode());
		invoiceData.append(space(12)).append("DATE").append(space(1)).append(IPrintConstant.COLON_STR)
				.append(cartItems.getDocdate());
		invoiceData.append(IPrintConstant.NEWLINE_STR);
		invoiceData.append("TIME").append(space(2)).append(IPrintConstant.COLON_STR).append(space(1))
				.append(cartItems.getDoctime());
		invoiceData.append(space(12)).append("PAX").append(space(1)).append(IPrintConstant.COLON_STR)
				.append(cartItems.getPaxNo()).append(IPrintConstant.NEWLINE_STR);

		invoiceData.append(IPrintConstant.LINEBREAK_STR).append(IPrintConstant.NEWLINE_STR);
		invoiceData.append("Item Name").append(space(24)).append("Qty").append(IPrintConstant.NEWLINE_STR);
		invoiceData.append(IPrintConstant.LINEBREAK_STR).append(IPrintConstant.NEWLINE_STR)
				.append(IPrintConstant.NEWLINE_STR);
		int loop = 0;
		// ITEM'S
		for (Items items : cartItems.getItems()) {
			invoiceData.append(items.getItem_name()).append(space(18)).append(items.getQty())
					.append(IPrintConstant.NEWLINE_STR);
			loop++;
		}

		invoiceData.append(IPrintConstant.LINEBREAK_STR).append(IPrintConstant.NEWLINE_STR);

		invoiceData.append("USER").append(space(1)).append(IPrintConstant.COLON_STR).append("Super").append(space(10))
				.append("TOTAL").append(space(1));
		invoiceData.append(IPrintConstant.COLON_STR).append(space(3)).append(loop);

		return invoiceData.toString();
	}

	private static String space(int len) {
		StringBuilder spaceContain = new StringBuilder();
		while (len > 0) {
			spaceContain.append(" ");
			len--;
		}
		return spaceContain.toString();
	}

}
