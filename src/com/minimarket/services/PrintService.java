package com.minimarket.services;

import com.minimarket.models.Sale;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PrintService {

    public static boolean printReceipt(String receiptText) {
        try {
            // Convert receipt text to input stream
            InputStream is = new ByteArrayInputStream(receiptText.getBytes(StandardCharsets.UTF_8));

            // Create a doc flavor
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

            // Create a print job
            javax.print.PrintService service = javax.print.PrintServiceLookup.lookupDefaultPrintService();

            if (service != null) {
                DocPrintJob job = service.createPrintJob();

                // Create print attributes
                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                attributes.add(new Copies(1));

                // Create a simple doc
                Doc doc = new SimpleDoc(is, flavor, null);

                // Print the document
                job.print(doc, attributes);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean printReceipt58mm(Sale sale) {
        String receiptText = com.minimarket.utils.ReceiptGenerator.generate58mmReceipt(sale);
        return printReceipt(receiptText);
    }
}