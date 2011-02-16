/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLConstants;

/**
 * <p>
 * <b>Purpose</b>: A helper class for performing data conversions.
 * </p>
 */
public class SDODataHelper implements DataHelper {
    // hold the context containing all helpers so that we can preserve
    // inter-helper relationships
    private HelperContext aHelperContext;

    public SDODataHelper() {
    }

    public SDODataHelper(HelperContext aContext) {
        aHelperContext = aContext;
    }

    private XMLConversionManager getXMLConversionManager() {
        return ((SDOXMLHelper) getHelperContext().getXMLHelper()).getXmlConversionManager();
    }

    /**
     * The specified TimeZone will be used for all String to Date object
     * conversions.  By default the GMT time zone is used.
     */
    public void setTimeZone(TimeZone timeZone) {
        if (null == timeZone) {
            getXMLConversionManager().setTimeZone(TimeZone.getTimeZone("GMT"));
        } else {
            getXMLConversionManager().setTimeZone(timeZone);
        }
    }

    /**
     * By setting this flag to true the marshalled date objects marshalled to
     * the XML schema types time and dateTime will be qualified by a time zone.
     * By default time information is not time zone qualified.
     */
    public void setTimeZoneQualified(boolean timeZoneQualified) {
        getXMLConversionManager().setTimeZoneQualified(timeZoneQualified);
    }

    /**
     * Creates a Calendar based on a given Duration and Locale.
     * 
     * @param dur the Duration object to use to populate the Calendar
     * @param loc the Locale to use - null is a valid value
     *            
     * @return a Calendar 
     */
    private Calendar toCalendar(Duration dur, Locale loc) {
        Calendar cal;
        if (loc == null) {
            cal = Calendar.getInstance(getXMLConversionManager().getTimeZone());
        } else {
            cal = Calendar.getInstance(getXMLConversionManager().getTimeZone(), loc);
        }
        cal.setTimeInMillis(dur.getTimeInMillis(cal));
        cal.set(Calendar.YEAR, dur.getYears());
        cal.set(Calendar.MONTH, dur.getMonths() - 1);
        cal.set(Calendar.DATE, dur.getDays());
        cal.set(Calendar.HOUR_OF_DAY, dur.getHours());
        cal.set(Calendar.MINUTE, dur.getMinutes());
        cal.set(Calendar.SECOND, dur.getSeconds());
        return cal;
    }
    
    /**
     * Convert from a String representation of an SDO date type to a Calendar
     * using the default locale. Same as toCalendar(dateString, null).
     * 
     * @param dateString
     *            the String representation of an SDO date type
     * @return a Calendar representation of an SDO date type.
     * @throws IllegalArgumentException
     *             for invalid formats.
     */
    public Calendar toCalendar(String dateString) {
        return toCalendar(dateString, null);
    }

    /**
     * Convert from a String representation of an SDO date type to a Calendar
     * using the specified locale, or the default locale if the locale is null.
     * 
     * @param dateString
     *            the String representation of an SDO date type
     * @param locale
     *            the locale or null for default locale.
     * @return a Calendar representation of an SDO date type.
     * @throws IllegalArgumentException
     *             for invalid formats.
     */
    public Calendar toCalendar(String dateString, Locale locale) {
        if (null == dateString) {
            return null;
        }
        // Handle duration if necessary
        if (dateString.startsWith("P")) {
            return toCalendar(getXMLConversionManager().convertStringToDuration(dateString), locale);
        }
        // An XMLGregorianCalendar will be used to parse the date string
        XMLGregorianCalendar xgc = getXMLConversionManager().convertStringToXMLGregorianCalendar(dateString);
        Calendar cal;
        if (xgc.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            cal = xgc.toGregorianCalendar(getXMLConversionManager().getTimeZone(), locale, null);
            cal.clear(Calendar.ZONE_OFFSET);
        } else {
            cal = xgc.toGregorianCalendar(xgc.getTimeZone(xgc.getTimezone()), locale, null);
        }
        return cal;
    }

    /**
     * Convert from a String representation of the Date type to a Date.
     * 
     * @param dateString a String representation of the Date type
     * @return a Date from a String representation of the Date type.
     */
    public Date toDate(String dateString) {
        if (null == dateString) {
            return null;
        }
        if (dateString.startsWith("P")) {
            Calendar cal = toCalendar(getXMLConversionManager().convertStringToDuration(dateString), null);
            return cal.getTime();
        }

        try {
            return getXMLConversionManager().convertStringToDate(dateString, null);
        } catch(ConversionException e){
            throw new IllegalArgumentException(e);
        }

    }

    /**
     * Convert from a Calendar to a String representation of the DateTime type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the DateTime type.
     */
    public String toDateTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.DATE_TIME_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);

        return s;
    }

    /**
     * Convert from a Date to a String representation of the DateTime type.
     * 
     * @param date the date
     * @return a Date to a String representation of the DateTime type.
     */
    public String toDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.DATE_TIME_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the Day type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Day type.
     */
    public String toDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_DAY_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);

        return s;
    }

    /**
     * Convert from a Date to a String representation of the Day type.
     * 
     * @param date the date
     * @return a Date to a String representation of the Day type.
     */
    public String toDay(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.G_DAY_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the Duration type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Duration type.
     */
    public String toDuration(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        StringBuffer dur = new StringBuffer();
        dur.append("P");
        dur.append(calendar.get(Calendar.YEAR));
        dur.append("Y");
        dur.append(calendar.get(Calendar.MONTH) + 1);
        dur.append("M");
        dur.append(calendar.get(Calendar.DATE));
        dur.append("DT");
        dur.append(calendar.get(Calendar.HOUR));
        dur.append("H");
        dur.append(calendar.get(Calendar.MINUTE));
        dur.append("M");
        float s = (calendar.get(Calendar.SECOND) * 1000) + calendar.get(Calendar.MILLISECOND);
        dur.append(s / 1000);
        dur.append("S");
        return dur.toString();
    }

    /**
     * Convert from a Date to a String representation of the Duration type.
     * 
     * @param date the date
     * @return a Date to a String representation of the Duration type.
     */
    public String toDuration(Date date) {
        if (date == null) {
            return null;
        }
        Calendar outPut = Calendar.getInstance();
        outPut.setTime(date);
        outPut.setTimeZone(TimeZone.getTimeZone("GMT"));
        return toDuration(outPut);
    }

    /**
     * Convert from a Calendar to a String representation of the Month type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Month type.
     */
    public String toMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_MONTH_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);
        return s;
    }

    /**
     * Convert from a Date to a String representation of the Month type.
     * 
     * @param date the date
     * @return a Date to a String representation of the Month type.
     */
    public String toMonth(Date date) {
        if (date == null) {
            return null;
        }
        
        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromDate(date, XMLConstants.G_MONTH_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);
        return s;
    }

    /**
     * Convert from a Calendar to a String representation of the MonthDay type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the MonthDay type.
     */
    public String toMonthDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        String monthDay = "--";
        boolean isSetZoneOffset = calendar.isSet(Calendar.ZONE_OFFSET);

        if (!calendar.isSet(Calendar.MONTH) || !calendar.isSet(Calendar.DAY_OF_MONTH)) {
            // WE COULD REQUIRE AN EXCEPTION BE THROWN HERE
            // throw new RuntimeException();
        }

        // MONTH PORTION
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) {
            monthDay += "0";
        }
        monthDay += month + "-";

        // DAY PORTION
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            monthDay += "0";
        }
        monthDay += day;

        // TIME ZONE PORTION
        if (isSetZoneOffset) {
            int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
            if (0 != zoneOffset) {
                if (zoneOffset > 0) {
                    monthDay += "+";
                } else {
                    monthDay += "-";
                    zoneOffset = zoneOffset * -1;
                }
                double hours = (double) zoneOffset / 3600000;
                if (hours < 10) {
                    monthDay += "0";
                }
                monthDay += (int) hours + ":";
                int minutes = (int) ((hours - (int) hours) * 60);
                if (minutes < 10) {
                    monthDay += "0";
                }
                monthDay += minutes;
            } else {
                monthDay += "Z";
            }
        }

        return monthDay;
    }

    /**
     * Convert from a Date to a String representation of the MonthDay type.
     * 
     * @param date the date to convert
     * @return a Date to a String representation of the MonthDay type.
     */
    public String toMonthDay(Date date) {
        if (null == date) {
            return null;
        }
        GregorianCalendar dateCalendar = new GregorianCalendar(getXMLConversionManager().getTimeZone());

        dateCalendar.clear();
        dateCalendar.setTime(date);
        dateCalendar.clear(Calendar.ZONE_OFFSET);
        return toMonthDay(dateCalendar);
    }

    /**
     * Convert from a Calendar to a String representation of the Time type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Time type.
     */
    public String toTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.TIME_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);
        return s;
    }

    /**
     * Convert from a Date to a String representation of the Time type.
     * 
     * @param date the date
     * @return a Date to a String representation of the Time type.
     */
    public String toTime(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.TIME_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the Year type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Year type.
     */
    public String toYear(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_YEAR_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);
        return s;
    }

    /**
     * Convert from a Date to a String representation of the Year type.
     * 
     * @param date the date
     * @return a Date to a String representation of the Year type.
     */
    public String toYear(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.G_YEAR_QNAME);
    }

    /**
     * Convert from a Date to a String representation of the YearMonth type.
     * 
     * @param date the date
     * @return a Date to a String representation of the YearMonth type.
     */
    public String toYearMonth(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.G_YEAR_MONTH_QNAME);
    }

    /**
     * Convert from a Date to a String representation of the YearMonthDay type.
     * 
     * @param date the date
     * @return a Date to a String representation of the YearMonthDay type.
     */
    public String toYearMonthDay(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.DATE_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the YearMonth type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the YearMonth type.
     */
    public String toYearMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_YEAR_MONTH_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);
        return s;
    }

    /**
     * Convert from a Calendar to a String representation of the YearMonthDay
     * type.
     * 
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the YearMonthDay type.
     */
    public String toYearMonthDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        // Temporarily turn off TZ qualification
        boolean wasTimezoneQualified = getXMLConversionManager().isTimeZoneQualified();
        getXMLConversionManager().setTimeZoneQualified(false);
        String s = getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.DATE_QNAME);
        getXMLConversionManager().setTimeZoneQualified(wasTimezoneQualified);
        return s;
    }

    /**
     * Perform Calendar setting based on passed in parameters.
     * 
     * @param c the Calendar to be set
     * @param i an offset indicate what value to be set
     * @param data the value to set to the Calendar
     */
    @SuppressWarnings("unused")
    private void setCalendar(Calendar c, int i, int data) {
        switch (i) {
        case 0:
            c.set(Calendar.YEAR, data);
            break;
        case 1:
            c.set(Calendar.MONTH, data - 1);
            break;
        case 2:
            c.set(Calendar.DATE, data);
            break;
        case 3:
            c.set(Calendar.HOUR, data);
            break;
        case 4:
            c.set(Calendar.MINUTE, data);
            break;
        case 5:
            c.set(Calendar.SECOND, data);
            break;
        case 6:
            c.set(Calendar.MILLISECOND, data);
            break;
        }
    }

    /**
     * Convert a value based to the appropriate type.
     * 
     * @param value The value to convert.
     * @param convertClass The class to convert the value to.
     * @param schemaType The schema type if available.
     * @return the original value converted based on the convertClass parameter.
     */
    public Object convertValue(Object value, Class convertClass, QName schemaType) {
        return getXMLConversionManager().convertObject(value, convertClass, schemaType);

    }

    /**
     * Convert a String value based to the appropriate type.
     * 
     * @param value The String value to convert.
     * @param convertClass The class to convert the value to.
     * @return the original value converted based on the convertClass parameter.
     */
    public Object convertFromStringValue(String value, Class convertClass) {
        if (convertClass == ClassConstants.UTILDATE) {
            return toDate(value);
        } else if (convertClass == ClassConstants.CALENDAR) {
            return toCalendar(value);
        } else {
            return getXMLConversionManager().convertObject(value, convertClass);
        }
    }

    /**
     * Convert a String value based to the appropriate type.
     * 
     * @param value The String value to convert.
     * @param sdoType The SDO type of the value to convert the value to.
     * @return the original value converted based on the SDO type.
     */
    public Object convertFromStringValue(String value, Type sdoType) {
        return convertFromStringValue(value, sdoType, null);
    }

    /**
     * Convert a String value based to the appropriate type.
     * 
     * @param value The String value to convert.
     * @param sdoType The SDO type of the value to convert the value to.
     * @param schemaType The schema type if available.
     * @return the original value converted based on the convertClass parameter.
     */
    public Object convertFromStringValue(String value, Type sdoType, QName schemaType) {
        Class convertClass = ((SDOTypeHelper) getHelperContext().getTypeHelper()).getJavaWrapperTypeForSDOType(sdoType);
        if (convertClass != null) {
            if (schemaType == null) {
                return ((SDODataHelper) getHelperContext().getDataHelper()).convertFromStringValue(value, convertClass);
            } else {
                return ((SDODataHelper) getHelperContext().getDataHelper()).convertFromStringValue(value, convertClass, schemaType);
            }
        }
        return value;
    }

    /**
     * Convert a String value based to the appropriate type.
     * 
     * @param value The String value to convert.
     * @param convertClass The class to convert the value to.
     * @param schemaType The schema type if available.
     * @return the original value converted based on the convertClass parameter.
     */
    public Object convertFromStringValue(String value, Class convertClass, QName schemaType) {
        if (convertClass == ClassConstants.UTILDATE) {
            return toDate(value);
        } else if (convertClass == ClassConstants.CALENDAR) {
            return toCalendar(value);
        } else {
            return getXMLConversionManager().convertObject(value, convertClass, schemaType);
        }
    }

    /**
     * Convert to a String value based to the SDO type.
     * 
     * @param value The value to convert.
     * @param sdoType the SDO type
     * @return the original value converted to a String based on the SDO type
     *         specified.
     */
    public String convertToStringValue(Object value, Type sdoType) {
        return convertToStringValue(value, sdoType, null);
    }

    /**
     * Convert to a String value based to the SDO type.
     * 
     * @param value The value to convert.
     * @param sdoType the SDO type
     * @return the original value converted to a String based on the SDO type
     *         specified.
     */
    private String convertToStringValue(Object value, Type sdoType, QName xsdType) {
        if (value.getClass() == ClassConstants.CALENDAR) {
            if (sdoType.equals(SDOConstants.SDO_DATETIME)) {
                return toDateTime((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_TIME)) {
                return toTime((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_DAY)) {
                return toDay((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_DURATION)) {
                return toDuration((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTH)) {
                return toMonth((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTHDAY)) {
                return toMonthDay((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_YEAR)) {
                return toYear((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTH)) {
                return toYearMonth((Calendar) value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTHDAY)) {
                return toYearMonthDay((Calendar) value);
            }
        } else if (value.getClass() == ClassConstants.UTILDATE) {
            if (sdoType.equals(SDOConstants.SDO_DATETIME)) {
                return toDateTime((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_TIME)) {
                return toTime((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_DAY)) {
                return toDay((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_DURATION)) {
                return toDuration((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTH)) {
                return toMonth((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTHDAY)) {
                return toMonthDay((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_YEAR)) {
                return toYear((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTH)) {
                return toYearMonth((Date) value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTHDAY)) {
                return toYearMonthDay((Date) value);
            }
        } else if (value.getClass().getName().equals("javax.activation.DataHandler")) {
            // convert a DataHandler to a string, by getting the bytes and then
            // calling into conversion manager
            try {
                Class binaryDataHelper = PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper");
                java.lang.reflect.Method getHelperMethod = PrivilegedAccessHelper.getMethod(binaryDataHelper, "getXMLBinaryDataHelper", new Class[] {}, false);
                java.lang.reflect.Method stringToDataHandlerMethod = PrivilegedAccessHelper.getMethod(binaryDataHelper, "stringFromDataHandler", new Class[] { Object.class, QName.class, AbstractSession.class }, false);

                Object helper = PrivilegedAccessHelper.invokeMethod(getHelperMethod, binaryDataHelper, new Object[] {});
                String result = (String) PrivilegedAccessHelper.invokeMethod(stringToDataHandlerMethod, helper, new Object[] { value, xsdType, ((SDOXMLHelper) getHelperContext().getXMLHelper()).getXmlContext().getSession(0) });
                return result;
            } catch (Exception ex) {
                return (String) getXMLConversionManager().convertObject(value, ClassConstants.STRING, xsdType);
            }
        }
        return (String) getXMLConversionManager().convertObject(value, ClassConstants.STRING, xsdType);
    }

    /**
     * Convert the specified value to an {@link Type#getInstanceClass()
     * instance} of the specified type. Supported conversions are listed in
     * Section 14 of the SDO specification.
     * 
     * @param type the target {@link Type#isDataType() data type}.
     * @param value the value to convert
     * @return a value of the specified type's instance class
     * @throws IllegalArgumentException if the value could not be converted
     * @see #convert(Property, Object)
     */
    public Object convert(Type type, Object value) {
        Class convertClass = null;
        if (((SDOType) type).isDataType()) {
            convertClass = type.getInstanceClass();
        } /*else {
            convertClass = ((org.eclipse.persistence.sdo.SDOType) type).getImplClass();
        }*/

        if (value.getClass() == ClassConstants.STRING) {
            return convertFromStringValue((String) value, type);
        } else if (convertClass == ClassConstants.STRING) {
            return convertToStringValue(value, type);
        } else {
            try {
                return getXMLConversionManager().convertObject(value, convertClass);
            } catch (ConversionException e) {
                throw new IllegalArgumentException(SDOException.conversionError(e));
            }
        }
    }

    /**
     * Convert the specified value to an {@link Type#getInstanceClass()
     * instance} of the specified property's {@link Property#getType() type}.
     * The specified value must be a List if the property is
     * {@link Property#isMany() many valued}. In this case, all the values in
     * the List are converted.
     * 
     * @param property the target {@link Type#isDataType() data type} property.
     * @param value the value or List of values to convert
     * @return a converted value or list of converted values
     * @throws IllegalArgumentException if the value could not be converted
     * @see #convert(Type, Object)
     */
    public Object convert(Property property, Object value) {
        if (null == property) {
            throw new IllegalArgumentException(SDOException.conversionError(null));
        }
        try {
            Type convertType = property.getType();
            if (property.isMany()) {
                if (value == null) {
                    return null;
                } else if (!(value instanceof List)) {
                    throw new IllegalArgumentException(SDOException.conversionError(null));
                } else {
                    List theList = (List) value;
                    Object nextItem = null;
                    for (int i = 0; i < theList.size(); i++) {
                        nextItem = theList.get(i);
                        theList.set(i, convert(convertType, nextItem));
                    }
                    return theList;
                }
            } else {
                return convert(convertType, value);
            }
        } catch (ConversionException e) {
            throw SDOException.invalidPropertyValue(property.getName(), property.getType().getURI() + '#' + property.getType().getName(), value.getClass().getName(), value.toString(), e);
        }
    }

    /**
     * INTERNAL:
     */
    public Object convertValueToClass(Property prop, Object valueToConvert, Class convertToClass) {
        try {
            if (valueToConvert == null) {
                return null;
            }
            if (convertToClass == ClassConstants.STRING) {
                return convertToStringValue(valueToConvert, prop.getType(), ((SDOProperty) prop).getXsdType());
            } else {
                SDOProperty sdoProp = (SDOProperty) prop;
                DatabaseMapping xmlMapping = sdoProp.getXmlMapping();
                if (xmlMapping != null && xmlMapping.isDirectToFieldMapping() && sdoProp.getXsdType() != null) {
                    return getXMLConversionManager().convertObject(valueToConvert, convertToClass, sdoProp.getXsdType());
                } else {
                    return getXMLConversionManager().convertObject(valueToConvert, convertToClass);
                }
            }
        } catch (ConversionException e) {
            throw new IllegalArgumentException(SDOException.conversionError(e));
        }
    }

    /**
     * INTERNAL:
     */
    public HelperContext getHelperContext() {
        if (null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    /**
     * INTERNAL:
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}
