package com.minimarket.utils;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        // Indonesian phone number pattern
        String phoneRegex = "^(\\+62|62|0)[0-9]{9,12}$";
        return Pattern.compile(phoneRegex).matcher(phone.replaceAll("[^0-9+]", "")).matches();
    }

    public static boolean isValidPrice(double price) {
        return price >= 0;
    }

    public static boolean isValidStock(int stock) {
        return stock >= 0;
    }

    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }

    public static boolean isValidString(String str, int minLength) {
        return str != null && str.trim().length() >= minLength;
    }

    public static boolean isPositiveNumber(String str) {
        try {
            double num = Double.parseDouble(str);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeNumber(String str) {
        try {
            double num = Double.parseDouble(str);
            return num >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String dateString) {
        try {
            DateHelper.parseDate(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return false;
        }

        // Barcode should be alphanumeric
        return barcode.matches("^[A-Za-z0-9]+$");
    }

    public static boolean isValidDiscount(double discount, double maxDiscount) {
        return discount >= 0 && discount <= maxDiscount;
    }

    public static boolean isValidTax(double tax) {
        return tax >= 0 && tax <= 100;
    }

    public static boolean isValidPaymentAmount(double amountPaid, double totalAmount) {
        return amountPaid >= totalAmount;
    }

    public static String sanitizeInput(String input) {
        if (input == null) return "";

        // Remove potentially dangerous characters
        return input.trim()
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}