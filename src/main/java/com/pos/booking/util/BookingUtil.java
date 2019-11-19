package com.pos.booking.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;

public class BookingUtil {

	public static String getCurrentDate() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDate = LocalDate.now();
		LocalDateTime dateTime = getCurrentDateTime();
		if (dateTime.getHour() >= 24 || dateTime.getHour() <= 6) {
			localDate = localDate.minusDays(1);
		}
		return localDate.format(dateTimeFormatter);
	}
	
	public static LocalDate getCurrentDateinMill() {
		LocalDateTime localDate = LocalDateTime.now();
		LocalDateTime dateTime = getCurrentDateTime();
		if (dateTime.getHour() >= 24 || dateTime.getHour() <= 6) {
			localDate = localDate.minusDays(1);
		}
		return localDate.toLocalDate();
	}
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}

	public static String createPrefix(LocalDate localDate) {
		String preFix = "";
		if (localDate.getMonthValue() <= 3) {
			preFix = String.valueOf(localDate.minusYears(1).getYear()).substring(2);
		} else {
			preFix = String.valueOf(localDate.getYear()).substring(2);
		}
		preFix = preFix + "04";

		if (localDate.getMonthValue() <= 3) {
			preFix = preFix + String.valueOf(localDate.getYear()).substring(2);
		} else {
			preFix = preFix + String.valueOf(localDate.plusYears(1).getYear()).substring(2);
		}
		return (preFix = preFix + "03");
	}
}
