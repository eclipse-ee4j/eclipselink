/**
 * <copyright>
 *
 * Service Data Objects
 * Version 2.1.1
 * Licensed Materials
 *
 * (c) Copyright BEA Systems, Inc., International Business Machines Corporation, 
 * Oracle Corporation, Primeton Technologies Ltd., Rogue Wave Software, SAP AG., 
 * Software AG., Sun Microsystems, Sybase Inc., Xcalia, Zend Technologies, 
 * 2005-2008. All rights reserved.
 *
 * </copyright>
 * 
 */

package commonj.sdo.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import commonj.sdo.Type;
import commonj.sdo.Property;

import commonj.sdo.impl.HelperProvider;

/**
 * Data helper methods.
 */
public interface DataHelper
{
  /**
   * Convert from a String representation of an SDO date type to a Date.
   * @param dateString the String representation of an SDO date type
   * @return a Date representation of an SDO date type.
   * @throws IllegalArgumentException for invalid formats.
   */
  Date toDate(String dateString);
  
  /**
   * Convert from a String representation of an SDO date type to a Calendar using the
   * default locale.  Same as toCalendar(dateString, null).
   * @param dateString the String representation of an SDO date type
   * @return a Calendar representation of an SDO date type.
   * @throws IllegalArgumentException for invalid formats.
   */
  Calendar toCalendar(String dateString);
  
  /**
   * Convert from a String representation of an SDO date type to a Calendar using the
   * specified locale, or the default locale if the locale is null.
   * @param dateString the String representation of an SDO date type
   * @param locale the locale or null for default locale.
   * @return a Calendar representation of an SDO date type.
   * @throws IllegalArgumentException for invalid formats.
   */
  Calendar toCalendar(String dateString, Locale locale);

  /**
   * Convert from a Date to a String representation of the DateTime type.
   * @param date the date
   * @return a Date to a String representation of the DateTime type.
   */
  String toDateTime(Date date);
  
  /**
   * @deprecated There is no accepted algorithm to convert a date into a Duration so
   * use of this method is discouraged.
   * Convert from a Date to a String representation of the Duration type.
   * @param date the date
   * @return a Date to a String representation of the Duration type.
   */
  String toDuration(Date date);

  /**
   * Convert from a Date to a String representation of the Time type.
   * @param date the date
   * @return a Date to a String representation of the Time type.
   */
  String toTime(Date date);
  
  /**
   * Convert from a Date to a String representation of the Day type.
   * @param date the date
   * @return a Date to a String representation of the Day type.
   */
  String toDay(Date date);
  
  /**
   * Convert from a Date to a String representation of the Month type.
   * @param date the date
   * @return a Date to a String representation of the Month type.
   */
  String toMonth(Date date);

  /**
   * Convert from a Date to a String representation of the MonthDay type.
   * @param date the date
   * @return a Date to a String representation of the MonthDay type.
   */
  String toMonthDay(Date date);

  /**
   * Convert from a Date to a String representation of the Year type.
   * @param date the date
   * @return a Date to a String representation of the Year type.
   */
  String toYear(Date date);

  /**
   * Convert from a Date to a String representation of the YearMonth type.
   * @param date the date
   * @return a Date to a String representation of the YearMonth type.
   */
  String toYearMonth(Date date);

  /**
   * Convert from a Date to a String representation of the YearMonthDay type.
   * @param date the date
   * @return a Date to a String representation of the YearMonthDay type.
   */
  String toYearMonthDay(Date date);

  /**
   * Convert from a Calendar to a String representation of the DateTime type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the DateTime type.
   */
  String toDateTime(Calendar calendar);

  /**
   * @deprecated There is no accepted algorithm to convert a date into a Duration so
   * use of this method is discouraged.
   * Convert from a Calendar to a String representation of the Duration type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the Duration type.
   */
  String toDuration(Calendar calendar);

  /**
   * Convert from a Calendar to a String representation of the Time type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the Time type.
   */
  String toTime(Calendar calendar);

  /**
   * Convert from a Calendar to a String representation of the Day type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the Day type.
   */
  String toDay(Calendar calendar);

  /**
   * Convert from a Calendar to a String representation of the Month type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the Month type.
   */
  String toMonth(Calendar calendar);

  /**
   * Convert from a Calendar to a String representation of the MonthDay type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the MonthDay type.
   */
  String toMonthDay(Calendar calendar);

  /**
   * Convert from a Calendar to a String representation of the Year type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the Year type.
   */
  String toYear(Calendar calendar);

  /**
   * Convert from a Calendar to a String representation of the YearMonth type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the YearMonth type.
   */
  String toYearMonth(Calendar calendar);

  /**
   * Convert from a Calendar to a String representation of the YearMonthDay type.
   * @param calendar the calendar to convert
   * @return a Calendar to a String representation of the YearMonthDay type.
   */
  String toYearMonthDay(Calendar calendar);
  
  /**
   * Convert the specified value to an {@link Type#getInstanceClass() instance}
   * of the specified type.
   * Supported conversions are listed in Section 16 of the SDO specification.
   * @param type the target {@link Type#isDataType() data type}.
   * @param value the value to convert
   * @return a value of the specified type's instance class
   * @throws IllegalArgumentException if the value could not be converted
   * @see #convert(Property, Object)
   */
  Object convert(Type type, Object value);
  
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
  Object convert(Property property, Object value);
  
  /**
   * The default DataHelper.
   */
  DataHelper INSTANCE = HelperProvider.getDataHelper();
}
