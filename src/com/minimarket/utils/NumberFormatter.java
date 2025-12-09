package com.minimarket.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }

    public static String formatNumber(double number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

    public static String formatNumber(int number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

    public static String formatDecimal(double number, int decimalPlaces) {
        String pattern = "#,###.";
        for (int i = 0; i < decimalPlaces; i++) {
            pattern += "#";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(number);
    }

    public static double parseCurrency(String currencyString) {
        try {
            if (currencyString == null || currencyString.trim().isEmpty()) {
                return 0;
            }

            // Remove currency symbol and thousand separators
            String cleaned = currencyString.replace("Rp", "")
                    .replace(",", "")
                    .trim();

            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int parseInteger(String numberString) {
        try {
            if (numberString == null || numberString.trim().isEmpty()) {
                return 0;
            }

            // Remove thousand separators
            String cleaned = numberString.replace(",", "").trim();
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double parseDouble(String numberString) {
        try {
            if (numberString == null || numberString.trim().isEmpty()) {
                return 0;
            }

            // Remove thousand separators
            String cleaned = numberString.replace(",", "").trim();
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String formatPercentage(double percentage) {
        DecimalFormat df = new DecimalFormat("#,##0.00'%'");
        return df.format(percentage);
    }

    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "";
        }

        String cleaned = phoneNumber.replaceAll("[^0-9]", "");

        if (cleaned.length() == 12) {
            return cleaned.replaceFirst("(\\d{4})(\\d{4})(\\d{4})", "$1-$2-$3");
        } else if (cleaned.length() == 13) {
            return cleaned.replaceFirst("(\\d{4})(\\d{4})(\\d{5})", "$1-$2-$3");
        } else {
            return phoneNumber;
        }
    }
}