package com.pos.booking.print.service;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PrinterService implements Printable {

	private org.slf4j.Logger log = LoggerFactory.getLogger(PrinterService.class);

	public List<String> getPrinters() {

		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

		PrintService printServices[] = PrintServiceLookup.lookupPrintServices(flavor, pras);

		List<String> printerList = new ArrayList<>();
		for (PrintService printerService : printServices) {
			printerList.add(printerService.getName());
		}
		log.info("Available printer is: {}", printerList);
		return printerList;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		if (page > 0) { /* We have only one page, and 'page' is zero-based */
			return NO_SUCH_PAGE;
		}

		/*
		 * User (0,0) is typically outside the imageable area, so we must translate by
		 * the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		/* Now we perform our rendering */

		g.setFont(new Font("Roman", 0, 8));
		g.drawString("Hello world !", 0, 10);

		return PAGE_EXISTS;
	}

	public void printString(String printerName, String text) {

		// find the printService of name printerName
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		PrintService service = findPrintService(printerName, printService);

		if (service != null) {
			DocPrintJob job = service.createPrintJob();

			try {

				byte[] bytes;

				// important for umlaut chars
				bytes = text.getBytes("CP437");

				Doc doc = new SimpleDoc(bytes, flavor, null);

				job.print(doc, null);
			} catch (Exception e) {
				log.error("Error while printing job", e);
			}
		} else {
			log.warn("Unable to find printer service for print job");
		}

	}

	public void printBytes(String printerName, byte[] bytes) {

		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		PrintService service = findPrintService(printerName, printService);

		DocPrintJob job = service.createPrintJob();

		try {

			Doc doc = new SimpleDoc(bytes, flavor, null);

			job.print(doc, null);

		} catch (Exception e) {
			log.error("Failed to print byte", e);
		}
	}

	private PrintService findPrintService(String printerName, PrintService[] services) {
		for (PrintService service : services) {
			if (service.getName().equalsIgnoreCase(printerName)) {
				return service;
			}
		}

		return null;
	}
}
