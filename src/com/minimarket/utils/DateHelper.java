package com.minimarket.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateHelper {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SQL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(date);
    }

    public static Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDateTime(String datetimeString) {
        if (datetimeString == null || datetimeString.trim().isEmpty()) return null;

        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        try {
            return sdf.parse(datetimeString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatToSQLDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(SQL_DATE_FORMAT);
        return sdf.format(date);
    }

    public static String formatToSQLDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(SQL_DATETIME_FORMAT);
        return sdf.format(date);
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return new java.sql.Timestamp(date.getTime()).toLocalDateTime();
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return java.sql.Timestamp.valueOf(localDateTime);
    }

    public static String getCurrentDateString() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String getCurrentDateTimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    public static java.sql.Date toSQLDate(Date date) {
        if (date == null) return null;
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp toSQLTimestamp(Date date) {
        if (date == null) return null;
        return new java.sql.Timestamp(date.getTime());
    }
}