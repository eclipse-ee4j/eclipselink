/*
 * Copyright (c) 2020, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020, 2025 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     05/11/2020-2.7.0 Jody Grassel
//       - 538296: Wrong month is returned if OffsetDateTime is used in JPA 2.2 code

package org.eclipse.persistence.jpa.test.conversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.junit.Assert;
import org.junit.Test;

public class TestJavaTimeTypeConverter { 
    private ConversionManager cm = ConversionManager.getDefaultManager();
    
    // LocalDate
    @Test
    public void timeConvertLocalDateToLocalDate() {
        LocalDate src = LocalDate.of(2020, 1, 1);
        LocalDate ld = (LocalDate) cm.convertObject(src, ClassConstants.TIME_LDATE);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(src, ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
    }
    
    @Test
    public void timeConvertCalendarToLocalDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 0, 1, 0, 0, 0);
        
        LocalDate ld = (LocalDate) cm.convertObject(cal, ClassConstants.TIME_LDATE);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
    }

    @Test
    public void timeConvertUtilDateToLocalDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = dateFormat.parse("2020-01-01 24:00:00.000");
        Assert.assertEquals(2020 - 1900, date.getYear());
        Assert.assertEquals(0, date.getMonth());
        Assert.assertEquals(2, date.getDate());

        LocalDate ld = (LocalDate) cm.convertObject(date, ClassConstants.TIME_LDATE);
        ZonedDateTime check = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        Assert.assertNotNull(ld);
        Assert.assertEquals(check.getYear(), ld.getYear());
        Assert.assertEquals(check.getMonth(), ld.getMonth());
        Assert.assertEquals(check.getDayOfMonth(), ld.getDayOfMonth());
        Assert.assertEquals(2020, ld.getYear());
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(2, ld.getDayOfMonth());
    }

    @Test
    public void timeConvertSqlDateToLocalDate() {
        java.sql.Date date = java.sql.Date.valueOf("2020-01-01"); 
        
        LocalDate ld = (LocalDate) cm.convertObject(date, ClassConstants.TIME_LDATE);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
    }
    
    @Test
    public void timeConvertSqlTimestampToLocalDate() {
        java.sql.Timestamp ts = java.sql.Timestamp.valueOf("2020-01-01 01:00:00");
        
        LocalDate ld = (LocalDate) cm.convertObject(ts, ClassConstants.TIME_LDATE);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
    }
    
    @Test
    public void timeConvertStringToLocalDate() {
        String date = "2020-01-01T1:15:30";
        LocalDate ld = (LocalDate) cm.convertObject(date, ClassConstants.TIME_LDATE);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
    }
    
    @Test
    public void timeConvertLongToLocalDate() {
        long l = 18262; // 2020-01-01
        LocalDate ld = (LocalDate) cm.convertObject(l, ClassConstants.TIME_LDATE);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
    }
    
    @Test
    public void timeConvertToLocalDateException() {
        String date = "Bogus";
        
        try {
            cm.convertObject(date, ClassConstants.TIME_LDATE);
            Assert.fail("Expected Exception was not thrown.");
        } catch (ConversionException ce) {
            // Expected
        }
       
    }
    
    // LocalDateTime
    @Test
    public void timeConvertLocalDateTimeToLocalDateTime() {
        LocalDateTime src = LocalDateTime.of(2020, 1, 1, 16, 0, 0);
        LocalDateTime ld = (LocalDateTime) cm.convertObject(src, ClassConstants.TIME_LDATETIME);

        Assert.assertNotNull(ld);
        Assert.assertEquals(src, ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(0, ld.getMinute());
        Assert.assertEquals(0, ld.getSecond());
    }
    
    @Test
    public void timeConvertStringToLocalDateTime() {
        String date = "2020-01-01T16:15:30";
        LocalDateTime ld = (LocalDateTime) cm.convertObject(date, ClassConstants.TIME_LDATETIME);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(Month.JANUARY, ld.getMonth());
        Assert.assertEquals(1, ld.getDayOfMonth());
        Assert.assertEquals(2020,  ld.getYear());
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(15, ld.getMinute());
        Assert.assertEquals(30, ld.getSecond());
    }
    
    @Test
    public void timeConvertUtilDateToLocalDateTime() {
        Calendar cal = Calendar.getInstance();
        java.util.Date date = null;
        LocalDateTime ldt = null;
        
        cal.set(2020, 0, 1, 16, 15, 30);
        date = cal.getTime();
               
        ldt = (LocalDateTime) cm.convertObject(date, ClassConstants.TIME_LDATETIME);
        
        Assert.assertNotNull(ldt);
        Assert.assertEquals(Month.JANUARY, ldt.getMonth());
        Assert.assertEquals(1, ldt.getDayOfMonth());
        Assert.assertEquals(2020,  ldt.getYear());
        Assert.assertEquals(16, ldt.getHour());
        Assert.assertEquals(15, ldt.getMinute());
        Assert.assertEquals(30, ldt.getSecond());
    }
    
    @Test
    public void timeConvertCalendarToLocalDateTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 0, 1, 16, 15, 30);
        
        LocalDateTime ldt = (LocalDateTime) cm.convertObject(cal, ClassConstants.TIME_LDATETIME);
        
        Assert.assertNotNull(ldt);
        Assert.assertEquals(Month.JANUARY, ldt.getMonth());
        Assert.assertEquals(1, ldt.getDayOfMonth());
        Assert.assertEquals(2020,  ldt.getYear());
        Assert.assertEquals(16, ldt.getHour());
        Assert.assertEquals(15, ldt.getMinute());
        Assert.assertEquals(30, ldt.getSecond());
    }
    
    @Test
    public void timeConvertLongToLocalDateTime() {
        long l = 18262 * (60 * 60 * 24); // 2020-01-01      
        LocalDateTime ldt = (LocalDateTime) cm.convertObject(l, ClassConstants.TIME_LDATETIME);
        
        Assert.assertNotNull(ldt);
        Assert.assertEquals(Month.JANUARY, ldt.getMonth());
        Assert.assertEquals(1, ldt.getDayOfMonth());
        Assert.assertEquals(2020,  ldt.getYear());
    }
    
    @Test
    public void timeConvertToLocalDateTimeException() {
        String date = "Bogus";
        
        try {
            cm.convertObject(date, ClassConstants.TIME_LDATETIME);
            Assert.fail("Expected Exception was not thrown.");
        } catch (ConversionException ce) {
            // Expected
        }
    }
    
    // LocalTime
    @Test
    public void timeConvertLocalTimeToLocalTime() {
        LocalTime src = LocalTime.of(16, 15, 30);
        LocalTime ld = (LocalTime) cm.convertObject(src, ClassConstants.TIME_LTIME);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(src, ld);
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(15, ld.getMinute());
        Assert.assertEquals(30, ld.getSecond());
    }
    
    @Test
    public void timeConvertStringToLocalTime() {
        String date = "T16:15:30";
        LocalTime ld = (LocalTime) cm.convertObject(date, ClassConstants.TIME_LTIME);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(15, ld.getMinute());
        Assert.assertEquals(30, ld.getSecond());
    }
    
    @Test
    public void timeConvertTimestampToLocalTime() {
        java.sql.Timestamp ts = java.sql.Timestamp.valueOf("2020-01-01 16:15:30");
        
        LocalTime ld = (LocalTime) cm.convertObject(ts, ClassConstants.TIME_LTIME);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(15, ld.getMinute());
        Assert.assertEquals(30, ld.getSecond());
    }
    
    @Test
    public void timeConvertTimeToLocalTime() {
        java.sql.Time ts = java.sql.Time.valueOf("16:15:30");
        
        LocalTime ld = (LocalTime) cm.convertObject(ts, ClassConstants.TIME_LTIME);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(15, ld.getMinute());
        Assert.assertEquals(30, ld.getSecond());
    }
    
    @Test
    public void timeConvertUtilDateToLocalTime() {
        Calendar cal = Calendar.getInstance();
        java.util.Date date = null;
        LocalTime ld = null;
        
        cal.set(2020, 0, 1, 16, 15, 30);
        date = cal.getTime();
               
        ld = (LocalTime) cm.convertObject(date, ClassConstants.TIME_LTIME);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(15, ld.getMinute());
        Assert.assertEquals(30, ld.getSecond());
    }
    
    @Test
    public void convertLongToLocalTime() {
        long sod = 16*60*60 + 60*15 + 30;
        LocalTime ld = (LocalTime) cm.convertObject(sod, ClassConstants.TIME_LTIME);
        
        Assert.assertNotNull(ld);
        Assert.assertEquals(16, ld.getHour());
        Assert.assertEquals(15, ld.getMinute());
        Assert.assertEquals(30, ld.getSecond());
    }
    
    @Test
    public void timeConvertToLocalTimeException() {
        String date = "Bogus";
        
        try {
            cm.convertObject(date, ClassConstants.TIME_LTIME);
            Assert.fail("Expected Exception was not thrown.");
        } catch (ConversionException ce) {
            // Expected
        }
    }
        
    // OffsetDateTime
    
    @Test
    public void timeConvertCalendarToOffsetDateTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 0, 1, 16, 15, 30);
        
        OffsetDateTime odt = (OffsetDateTime) cm.convertObject(cal, ClassConstants.TIME_ODATETIME);
        
        Assert.assertNotNull(odt);
        Assert.assertEquals(Month.JANUARY, odt.getMonth());
        Assert.assertEquals(1, odt.getDayOfMonth());
        Assert.assertEquals(2020,  odt.getYear());
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15, odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void timeConvertUtilDateToOffsetDateTime() {
        Calendar cal = Calendar.getInstance();
        java.util.Date date = null;
        OffsetDateTime odt = null;
        
        cal.set(2020, 0, 1, 16, 15, 30);
        date = cal.getTime();
               
        odt = (OffsetDateTime) cm.convertObject(date, ClassConstants.TIME_ODATETIME);
        
        Assert.assertNotNull(odt);
        Assert.assertEquals(Month.JANUARY, odt.getMonth());
        Assert.assertEquals(1, odt.getDayOfMonth());
        Assert.assertEquals(2020,  odt.getYear());
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15, odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void timeConvertStringToOffsetDateTime() {
        String date = "2020-01-01T16:15:30";
        OffsetDateTime odt = null;
               
        odt = (OffsetDateTime) cm.convertObject(date, ClassConstants.TIME_ODATETIME);
        
        Assert.assertNotNull(odt);
        Assert.assertEquals(Month.JANUARY, odt.getMonth());
        Assert.assertEquals(1, odt.getDayOfMonth());
        Assert.assertEquals(2020,  odt.getYear());
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15,  odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void timeConvertOffsetDateTimeToOffsetDateTime() {
        OffsetDateTime original = OffsetDateTime.of(2020, 1, 1, 16, 15, 30, 0, ZoneOffset.UTC);
        OffsetDateTime odt = null;
               
        odt = (OffsetDateTime) cm.convertObject(original, ClassConstants.TIME_ODATETIME);
        
        Assert.assertNotNull(odt);
        Assert.assertSame(original,  odt);
        Assert.assertEquals(Month.JANUARY, odt.getMonth());
        Assert.assertEquals(1, odt.getDayOfMonth());
        Assert.assertEquals(2020,  odt.getYear());
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15,  odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void timeConvertToOffsetDateTimeException() {
        String date = "Bogus";
        
        try {
            cm.convertObject(date, ClassConstants.TIME_ODATETIME);
            Assert.fail("Expected Exception was not thrown.");
        } catch (ConversionException ce) {
            // Expected
        }
    }

    // OffSetTime
    
    @Test
    public void timeConvertOffsetTimeToOffsetTime() {
        OffsetTime original = OffsetTime.of(16, 15, 30, 0, ZoneOffset.UTC);
        OffsetTime odt = null;
               
        odt = (OffsetTime) cm.convertObject(original, ClassConstants.TIME_OTIME);
        
        Assert.assertNotNull(odt);
        Assert.assertSame(original,  odt);
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15,  odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void timeConvertStringToOffsetTime() {
        String date = "T16:15:30";
        OffsetTime odt = null;
               
        odt = (OffsetTime) cm.convertObject(date, ClassConstants.TIME_OTIME);
        
        Assert.assertNotNull(odt);
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15,  odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void timeConvertUtilDateToOffsetTime() {
        Calendar cal = Calendar.getInstance();
        java.util.Date date = null;
        OffsetTime odt = null;
        
        cal.set(2020, 0, 1, 16, 15, 30);
        date = cal.getTime();
               
        odt = (OffsetTime) cm.convertObject(date, ClassConstants.TIME_OTIME);
        
        Assert.assertNotNull(odt);
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15,  odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void timeConvertCalendarToOffsetTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(2020, 0, 1, 16, 15, 30);
        
        OffsetTime odt = (OffsetTime) cm.convertObject(cal, ClassConstants.TIME_OTIME);
        
        Assert.assertNotNull(odt);
        Assert.assertEquals(16, odt.getHour());
        Assert.assertEquals(15,  odt.getMinute());
        Assert.assertEquals(30, odt.getSecond());
    }
    
    @Test
    public void testConvertLongToOffsetTime() {
        long l = 18262 * (60 * 60 * 24) + (16*60*60 + 60*15 + 30); // 2020-01-01  T01:15:30
        OffsetTime ldt = (OffsetTime) cm.convertObject(l, ClassConstants.TIME_OTIME);
        
        Assert.assertNotNull(ldt);
        Assert.assertEquals(16, ldt.getHour());
        Assert.assertEquals(15,  ldt.getMinute());
        Assert.assertEquals(30, ldt.getSecond());
    }
    
    @Test
    public void timeConvertToOffsetTimeException() {
        String date = "Bogus";
        
        try {
            cm.convertObject(date, ClassConstants.TIME_OTIME);
            Assert.fail("Expected Exception was not thrown.");
        } catch (ConversionException ce) {
            // Expected
        }
    }
    
}