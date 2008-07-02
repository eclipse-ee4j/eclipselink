/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLConstants;

/**
* <p><b>Purpose</b>: A helper class for performing data conversions.</p>
*/
public class SDODataHelper implements DataHelper {
    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDODataHelper() {
        // TODO: JIRA129 - default to static global context - Do Not use this convenience constructor outside of JUnit testing
    }

    public SDODataHelper(HelperContext aContext) {
        aHelperContext = aContext;
    }
    
    private XMLConversionManager getXMLConversionManager(){
        return ((SDOXMLHelper)getHelperContext().getXMLHelper()).getXmlConversionManager();
    }
    
    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
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
     * Convert from a String representation of an SDO date type to a Date.
     * @param dateString the String representation of an SDO date type
     * @return a Date representation of an SDO date type.
     * @throws IllegalArgumentException for invalid formats.
     */
    public Date toDate(String dateString) {
        Calendar c = toCalendar(dateString);
        return c.getTime();
    }

    /**
     * Convert from a String representation of an SDO date type to a Calendar using the
     * default locale.  Same as toCalendar(dateString, null).
     * @param dateString the String representation of an SDO date type
     * @return a Calendar representation of an SDO date type.
     * @throws IllegalArgumentException for invalid formats.
     */
    public Calendar toCalendar(String dateString) {
        if (dateString == null) {
            throw new IllegalArgumentException(SDOException.conversionError(null));
        } else {
            Calendar c = Calendar.getInstance();
            c.clear();
            if (Pattern.matches("(^--0[1-9]-(([0-2][0-9])|(3[0-1])))|(^--1[0-2]-" + "(([0-2][0-9])|(3[0-1])))", dateString)) {
                //gMonthDay
                int _case = 1;
                iterDateString(dateString, _case, "-", c);
            } else if (Pattern.matches("(^----(([0-2][0-9])|(3[0-1])))", dateString)) {
                // gDay
                int _case = 2;
                iterDateString(dateString, _case, "-", c);
            } else if (Pattern.matches("--((0[1-9])|(1[0-2]))--", dateString)) {
                // gMonth
                int _case = 1;
                iterDateString(dateString, _case, "-", c);
            } else if (Pattern.matches("[0-9]{4}", dateString)) {
                // gYear
                int _case = 0;
                iterDateString(dateString, _case, "-", c);
            } else if (Pattern.matches("[0-9]{4}-((0[0-9])|(1[0-2]))", dateString)) {
                // gYearMonth
                int _case = 0;
                iterDateString(dateString, _case, "-", c);
            } else if (Pattern.matches("[0-9]{4}-((0[0-9])|(1[0-2]))-(([0-2][0-9])|(3[0-1]))", dateString)) {
                // Date
                int _case = 0;
                iterDateString(dateString, _case, "-", c);
            } else if (Pattern.matches("[0-9]{4}-((0[0-9])|(1[0-2]))-(([0-2][0-9])|(3[0-1]))T(([0-1]" + "[0-9])|(2[0-4]))(:[0-5][0-9]){2}((.[0-9]{3})|)", dateString)) {
                //DateTime
                int _case = 0;
                iterDateString(dateString, _case, "-T:.", c);
            } else if (Pattern.matches("(([0-1][0-9])|(2[0-4]))(:[0-5][0-9]){2}((.[0-9]{3})|)", dateString)) {
                // Time
                int _case = 3;
                iterDateString(dateString, _case, ":.", c);
            } else if (Pattern.matches("[Pp][0-9]+[Yy][0-9]+[Mm][0-9]+[Dd][Tt]" + "[0-9]+[Hh][0-9]+[Mm][0-9]+.[0-9]{1,3}[Ss]", dateString)) {
                //DateTime// Duration
                int _case = 0;
                iterDateStringDur(dateString, _case, "PYMDTHS", c);
            } else {
                throw new IllegalArgumentException(SDOException.conversionError(null));
            }
            c.setTimeZone(TimeZone.getTimeZone("GMT"));
            return c;
        }
    }

    /**
     * Convert from a String representation of an SDO date type to a Calendar using the
     * specified locale, or the default locale if the locale is null.
     * @param dateString the String representation of an SDO date type
     * @param locale the locale or null for default locale.
     * @return a Calendar representation of an SDO date type.
     * @throws IllegalArgumentException for invalid formats.
     */
    public Calendar toCalendar(String dateString, Locale locale) {
        if (dateString == null) {
            throw new IllegalArgumentException(SDOException.conversionError(null));
        }
        Calendar c = null;
        if (locale == null) {
            c = Calendar.getInstance();
        } else {
            c = Calendar.getInstance(locale);
        }
        c.clear();
        if (Pattern.matches("(^--0[1-9]-(([0-2][0-9])|(3[0-1])))|(^--1[0-2]-" +//gMonthDay
                                "(([0-2][0-9])|(3[0-1])))", dateString)) {
            int _case = 1;
            iterDateString(dateString, _case, "-", c);
        } else if (Pattern.matches("(^----(([0-2][0-9])|(3[0-1])))", dateString))// gDay
         {
            int _case = 2;
            iterDateString(dateString, _case, "-", c);
        } else if (Pattern.matches("--((0[1-9])|(1[0-2]))--", dateString))// gMonth
         {
            int _case = 1;
            iterDateString(dateString, _case, "-", c);
        } else if (Pattern.matches("[0-9]{4}", dateString))// gYear
         {
            int _case = 0;
            iterDateString(dateString, _case, "-", c);
        } else if (Pattern.matches("[0-9]{4}-((0[0-9])|(1[0-2]))", dateString))// gYearMonth
         {
            int _case = 0;
            iterDateString(dateString, _case, "-", c);
        } else if (Pattern.matches("[0-9]{4}-((0[0-9])|(1[0-2]))-(([0-2][0-9])|(3[0-1]))", dateString)) {// Date
            int _case = 0;
            iterDateString(dateString, _case, "-", c);
        } else if (Pattern.matches("[0-9]{4}-((0[0-9])|(1[0-2]))-(([0-2][0-9])|(3[0-1]))T(([0-1]" + "[0-9])|(2[0-4]))(:[0-5][0-9]){2}((.[0-9]{3})|)", dateString)) {//DateTime
            int _case = 0;
            iterDateString(dateString, _case, "-T:.", c);
        } else if (Pattern.matches("(([0-1][0-9])|(2[0-4]))(:[0-5][0-9]){2}((.[0-9]{3})|)", dateString)) {// Time
            int _case = 3;
            iterDateString(dateString, _case, ":.", c);
        } else if (Pattern.matches("[Pp][0-9]+[Yy][0-9]+[Mm][0-9]+[Dd][Tt]" + "[0-9]+[Hh][0-9]+[Mm][0-9]+.[0-9]{1,3}[Ss]", dateString)) {// Duration
            int _case = 0;
            iterDateStringDur(dateString, _case, "PYMDTHS", c);
        } else {
            throw new IllegalArgumentException(SDOException.conversionError(null));
        }
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        return c;
    }

    /**
     * Convert from a Date to a String representation of the DateTime type.
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
     * Convert from a Date to a String representation of the Duration type.
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
     * Convert from a Date to a String representation of the Time type.
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
     * Convert from a Date to a String representation of the Day type.
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
     * Convert from a Date to a String representation of the Month type.
     * @param date the date
     * @return a Date to a String representation of the Month type.
     */
    public String toMonth(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.G_MONTH_QNAME);
    }

    /**
     * Convert from a Date to a String representation of the MonthDay type.
     * @param date the date
     * @return a Date to a String representation of the MonthDay type.
     */
    public String toMonthDay(Date date) {
        if (date == null) {
            return null;
        }
        return getXMLConversionManager().stringFromDate(date, XMLConstants.G_MONTH_DAY_QNAME);
    }

    /**
     * Convert from a Date to a String representation of the Year type.
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
     * Convert from a Calendar to a String representation of the DateTime type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the DateTime type.
     */
    public String toDateTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.DATE_TIME_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the Duration type.
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
     * Convert from a Calendar to a String representation of the Time type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Time type.
     */
    public String toTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.TIME_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the Day type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Day type.
     */
    public String toDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_DAY_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the Month type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Month type.
     */
    public String toMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_MONTH_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the MonthDay type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the MonthDay type.
     */
    public String toMonthDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_MONTH_DAY_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the Year type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the Year type.
     */
    public String toYear(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_YEAR_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the YearMonth type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the YearMonth type.
     */
    public String toYearMonth(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.G_YEAR_MONTH_QNAME);
    }

    /**
     * Convert from a Calendar to a String representation of the YearMonthDay type.
     * @param calendar the calendar to convert
     * @return a Calendar to a String representation of the YearMonthDay type.
     */
    public String toYearMonthDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return getXMLConversionManager().stringFromCalendar(calendar, XMLConstants.DATE_QNAME);
    }

    /**
     *  Perform Calendar setting based on passed in parameters.
     * @param c   the Calendar to be set
     * @param i   an offset indicate what value to be set
     * @param data  the value to set to the Calendar
     */
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
     * Set a Calendar's value according to a string, e.g. "2001-11-23T01:12:23.001".
     * @param dateString  a string of duration
     * @param _case       an int to indicate calendar's value setting offset,
     *                    e.g. year = 0, month = 1 etc.
     * @param pattern     delimiter of StringTokenizer.
     * @param c           the calendar to be set.
     */
    private void iterDateString(String dateString, int _case, String pattern, Calendar c) {
        StringTokenizer tokn = new StringTokenizer(dateString, pattern);
        while (tokn.hasMoreTokens()) {
            int data = Integer.parseInt(tokn.nextToken());
            setCalendar(c, _case, data);
            _case++;
        }
    }

    /**
     * Set a Calendar's value according to a Duration string,
     * e.g. "P12Y10M2DT0H40M27.87S".
     * @param dateString  a string of duration
     * @param _case       an int to indicate calendar's value setting offset,
     *                    e.g. year = 0, month = 1 etc.
     * @param pattern     delimiter of StringTokenizer.
     * @param c           the calendar to be set.
     */
    private void iterDateStringDur(String dateString, int _case, String pattern, Calendar c) {
        StringTokenizer tokn = new StringTokenizer(dateString, pattern);
        while (tokn.hasMoreTokens()) {
            if (_case < 5) {// process Year, month, date, hour, and minute
                int data = Integer.parseInt(tokn.nextToken());
                setCalendar(c, _case, data);
            } else {// process second with or without millisecond 
                String secMec = tokn.nextToken();
                int data = Integer.parseInt(secMec.substring(0, 2));//extract seconds
                setCalendar(c, _case, data);
                double da = Double.parseDouble(secMec);
                int mec = (int)((da * 1000) - (data * 1000));// extract possible milliseconds
                setCalendar(c, _case + 1, mec);
            }
            _case++;
        }
    }

    /**
       * Convert a value based to the appropriate type.
       * @param value  The value to convert.
           * @param convertClass  The class to convert the value to.
           * @param schemaType  The schema type if available.
           * @return the original value converted based on the convertClass parameter.
       */
    public Object convertValue(Object value, Class convertClass, QName schemaType) {
        return getXMLConversionManager().convertObject(value, convertClass, schemaType);

    }

    /**
      * Convert a String value based to the appropriate type.
      * @param value  The String value to convert.
      * @param convertClass  The class to convert the value to.
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
         * @param value  The String value to convert.
         * @param sdoType  The SDO type of the value to convert the value to.
         * @return the original value converted based on the SDO type.
         */
    public Object convertFromStringValue(String value, Type sdoType) {
        return convertFromStringValue(value, sdoType, null);
    }

    /**
         * Convert a String value based to the appropriate type.
         * @param value  The String value to convert.
         * @param sdoType  The SDO type of the value to convert the value to.
         * @param schemaType  The schema type if available.
         * @return the original value converted based on the convertClass parameter.
         */
    public Object convertFromStringValue(String value, Type sdoType, QName schemaType) {
        Class convertClass = ((SDOTypeHelper)getHelperContext().getTypeHelper()).getJavaWrapperTypeForSDOType(sdoType);
        if (convertClass != null) {
            if (schemaType == null) {
                return ((SDODataHelper)getHelperContext().getDataHelper()).convertFromStringValue(value, convertClass);
            } else {
                return ((SDODataHelper)getHelperContext().getDataHelper()).convertFromStringValue(value, convertClass, schemaType);
            }
        }
        return value;
    }

    /**
         * Convert a String value based to the appropriate type.
         * @param value  The String value to convert.
         * @param convertClass  The class to convert the value to.
         * @param schemaType  The schema type if available.
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
      * @param value  The value to convert.
      * @param sdoType the SDO type
      * @return the original value converted to a String based on the SDO type specified.
      */
    public String convertToStringValue(Object value, Type sdoType) {
      return convertToStringValue(value, sdoType, null);
    }


    /**
      * Convert to a String value based to the SDO type.
      * @param value  The value to convert.
      * @param sdoType the SDO type
      * @return the original value converted to a String based on the SDO type specified.
      */
    private String convertToStringValue(Object value, Type sdoType, QName xsdType) {
        if (value.getClass() == ClassConstants.CALENDAR) {
            if (sdoType.equals(SDOConstants.SDO_DATETIME)) {
                return toDateTime((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_TIME)) {
                return toTime((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_DAY)) {
                return toDay((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_DURATION)) {
                return toDuration((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTH)) {
                return toMonth((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTHDAY)) {
                return toMonthDay((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_YEAR)) {
                return toYear((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTH)) {
                return toYearMonth((Calendar)value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTHDAY)) {
                return toYearMonthDay((Calendar)value);
            }
        } else if (value.getClass() == ClassConstants.UTILDATE) {
            if (sdoType.equals(SDOConstants.SDO_DATETIME)) {
                return toDateTime((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_TIME)) {
                return toTime((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_DAY)) {
                return toDay((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_DURATION)) {
                return toDuration((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTH)) {
                return toMonth((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_MONTHDAY)) {
                return toMonthDay((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_YEAR)) {
                return toYear((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTH)) {
                return toYearMonth((Date)value);
            } else if (sdoType.equals(SDOConstants.SDO_YEARMONTHDAY)) {
                return toYearMonthDay((Date)value);
            }
        } else if(value.getClass().getName().equals("javax.activation.DataHandler")) {
            //convert a DataHandler to a string, by getting the bytes and then calling into conversion manager
            try {
                Class binaryDataHelper = PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper");
                java.lang.reflect.Method getHelperMethod = PrivilegedAccessHelper.getMethod(binaryDataHelper, "getXMLBinaryDataHelper", new Class[] {}, false);
                java.lang.reflect.Method stringToDataHandlerMethod = PrivilegedAccessHelper.getMethod(binaryDataHelper, "stringFromDataHandler", new Class[]{Object.class, QName.class, AbstractSession.class}, false);
                
                Object helper = PrivilegedAccessHelper.invokeMethod(getHelperMethod, binaryDataHelper, new Object[] {});
                String result = (String)PrivilegedAccessHelper.invokeMethod(stringToDataHandlerMethod, helper, new Object[] {value, xsdType, ((SDOXMLHelper)getHelperContext().getXMLHelper()).getXmlContext().getSession(0)});
                return result;
            } catch(Exception ex) {
                return (String)getXMLConversionManager().convertObject(value, ClassConstants.STRING, xsdType);
            }
        }
        return (String)getXMLConversionManager().convertObject(value, ClassConstants.STRING, xsdType);
    }

    /**
    * Convert the specified value to an {@link Type#getInstanceClass() instance}
    * of the specified type.
    * Supported conversions are listed in Section 14 of the SDO specification.
    * @param type the target {@link Type#isDataType() data type}.
    * @param value the value to convert
    * @return a value of the specified type's instance class
    * @throws IllegalArgumentException if the value could not be converted
    * @see #convert(Property, Object)
    */
    public Object convert(Type type, Object value) {
        Class convertClass;
        if (type.isDataType()) {
            convertClass = type.getInstanceClass();
        } else {
            convertClass = ((org.eclipse.persistence.sdo.SDOType)type).getImplClass();
        }

        if (value.getClass() == ClassConstants.STRING) {
            return convertFromStringValue((String)value, type);
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
     * Convert the specified value to an {@link Type#getInstanceClass() instance}
     * of the specified property's {@link Property#getType() type}.
     * The specified value must be a List if the property is {@link Property#isMany()
     * many valued}. In this case, all the values in the List are converted.
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
        Type convertType = property.getType();
        if (property.isMany()) {
            if (value == null) {
                return null;
            } else if (!(value instanceof List)) {
                throw new IllegalArgumentException(SDOException.conversionError(null));
            } else {
                List theList = (List)value;
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
                return convertToStringValue(valueToConvert, prop.getType(), ((SDOProperty)prop).getXsdType());
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