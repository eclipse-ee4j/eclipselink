/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLConversionException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.TimeZoneHolder;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;
import org.eclipse.persistence.internal.queries.ContainerPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Primarily used to convert objects from a given XML Schema type to a different type in Java.
 * Uses a singleton instance</p>
 * @since    OracleAS TopLink 10<i>g</i>
 */

public class XMLConversionManager extends ConversionManager implements TimeZoneHolder {
    protected static final String GMT_ID = "GMT";
    protected static final String GMT_SUFFIX = "Z";

    protected static XMLConversionManager defaultXMLManager;

    // Static hash tables for the default conversion pairs
    protected static HashMap defaultXMLTypes;
    protected static HashMap defaultJavaTypes;

    protected boolean timeZoneQualified;
    protected TimeZone timeZone;

    protected static int TOTAL_MS_DIGITS = 3;  // total digits for millisecond formatting
    protected static int TOTAL_NS_DIGITS = 9;  // total digits for nanosecond  formatting
    protected static long YEAR_ONE_AD_TIME = -62135769600000L; // time of 1 AD

    private static final char PLUS = '+';

    protected DatatypeFactory datatypeFactory;

    private boolean trimGMonth = false;
    
    public XMLConversionManager() {
        super();
        timeZoneQualified = false;
    }

    /**
     * INTERNAL:
     *
     * Return the DatatypeFactory instance.
     *
     * @return
     */
    protected DatatypeFactory getDatatypeFactory() {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        return datatypeFactory;
    }

    public static XMLConversionManager getDefaultXMLManager() {
        if (defaultXMLManager == null) {
            defaultXMLManager = new XMLConversionManager();
        }
        return defaultXMLManager;
    }

    /**
     * Return the specified TimeZone used for all String to date object
     * conversions.
     */
    public TimeZone getTimeZone() {
        if (timeZone == null) {
            return TimeZone.getDefault();
        } else {
            return timeZone;
        }
    }

    /**
     * The specified TimeZone will be used for all String to date object
     * conversions.  By default the TimeZone from the JVM is used.
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
      *
      */
    public boolean isTimeZoneQualified() {
        return timeZoneQualified;
    }

    /**
     * Specify if
     * Specify if when date objects are converted to Strings in the XML Schema
     * time or dateTime format
     */

    public void setTimeZoneQualified(boolean timeZoneQualified) {
        this.timeZoneQualified = timeZoneQualified;
    }

    /**
     * Convert the given object to the appropriate type by invoking the appropriate
     * ConversionManager method.
     *
     * @param sourceObject - will always be a string if read from XML
     * @param javaClass - the class that the object must be converted to
     * @return - the newly converted object
     */
    public Object convertObject(Object sourceObject, Class javaClass) throws ConversionException {
        if (sourceObject == null) {//Let the parent handle default null values
            return super.convertObject(sourceObject, javaClass);
        } else if (javaClass == null || javaClass == CoreClassConstants.OBJECT || sourceObject.getClass() == javaClass) {
            return sourceObject;
        } else if (javaClass == CoreClassConstants.STRING) {
           if(sourceObject instanceof List){
        	   return convertListToString(sourceObject);
           }else{           
               return convertObjectToString(sourceObject);
           }
        } else if ((javaClass == Constants.QNAME_CLASS) && (sourceObject != null)) {
            return convertObjectToQName(sourceObject);
        } else if ((javaClass == CoreClassConstants.List_Class) && (sourceObject instanceof String)) {
            return convertStringToList(sourceObject);
        } else if ((javaClass == CoreClassConstants.CALENDAR)) {
            return convertObjectToCalendar(sourceObject);
        } else if ((javaClass == CoreClassConstants.UTILDATE)) {
            return convertObjectToUtilDate(sourceObject, Constants.DATE_TIME_QNAME);
        } else if ((javaClass == CoreClassConstants.SQLDATE)) {
            return convertObjectToSQLDate(sourceObject, Constants.DATE_QNAME);
        } else if ((javaClass == CoreClassConstants.TIME)) {
            return convertObjectToSQLTime(sourceObject, Constants.TIME_QNAME);
        } else if ((javaClass == CoreClassConstants.TIMESTAMP)) {
            return convertObjectToTimestamp(sourceObject, Constants.DATE_TIME_QNAME);
        } else if ((javaClass == java.net.URI.class)) {
            return convertObjectToURI(sourceObject);
        } else if ((javaClass == CoreClassConstants.XML_GREGORIAN_CALENDAR)) {
            return convertObjectToXMLGregorianCalendar(sourceObject);
        } else if ((javaClass == CoreClassConstants.DURATION)) {
            return convertObjectToDuration(sourceObject);
        } else {
            try {
                return super.convertObject(sourceObject, javaClass);
            } catch (ConversionException ex) {
                if (sourceObject.getClass() == CoreClassConstants.STRING) {
                    return super.convertObject(((String) sourceObject).trim(), javaClass);
                }
                throw ex;
            }
        }
    }

    /**
     * Convert the given object to the appropriate type by invoking the appropriate
     * ConversionManager method.
     *
     * @param sourceObject - will always be a string if read from XML
     * @param javaClass - the class that the object must be converted to
     * @param schemaTypeQName - the XML schema that the object is being converted from
     * @return - the newly converted object
     */
    public Object convertObject(Object sourceObject, Class javaClass, QName schemaTypeQName) throws ConversionException {
        if (schemaTypeQName == null) {
            return convertObject(sourceObject, javaClass);
        }

        if (sourceObject == null) {
            return super.convertObject(sourceObject, javaClass);
        } else if ((sourceObject.getClass() == javaClass) || (javaClass == null) || (javaClass == CoreClassConstants.OBJECT)) {
            return sourceObject;
        } else if ((javaClass == CoreClassConstants.CALENDAR) || (javaClass == CoreClassConstants.GREGORIAN_CALENDAR)) {
            return convertObjectToCalendar(sourceObject, schemaTypeQName);
        } else if (javaClass == CoreClassConstants.ABYTE) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(Constants.HEX_BINARY)) {
                return super.convertObjectToByteObjectArray(sourceObject);
            } else if (schemaTypeQName.getLocalPart().equalsIgnoreCase(Constants.BASE_64_BINARY)) {
                return convertSchemaBase64ToByteObjectArray(sourceObject);
            }
        } else if (javaClass == CoreClassConstants.APBYTE) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(Constants.HEX_BINARY)) {
                return super.convertObjectToByteArray(sourceObject);
            } else if (schemaTypeQName.getLocalPart().equalsIgnoreCase(Constants.BASE_64_BINARY)) {
                return convertSchemaBase64ToByteArray(sourceObject);
            }
        } else if ((javaClass == CoreClassConstants.List_Class) && (sourceObject instanceof String)) {
            return convertStringToList(sourceObject);
        } else if ((javaClass == CoreClassConstants.STRING) && (sourceObject instanceof List)) {
            return convertListToString(sourceObject);
        } else if (sourceObject instanceof byte[]) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(Constants.BASE_64_BINARY)) {
                return buildBase64StringFromBytes((byte[]) sourceObject);
            }
            return Helper.buildHexStringFromBytes((byte[]) sourceObject);
        } else if (sourceObject instanceof Byte[]) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(Constants.BASE_64_BINARY)) {
                return buildBase64StringFromObjectBytes((Byte[]) sourceObject);
            }
            return buildHexStringFromObjectBytes((Byte[]) sourceObject);
        } else if ((javaClass == CoreClassConstants.UTILDATE)) {
            return convertObjectToUtilDate(sourceObject, schemaTypeQName);
        } else if (javaClass == CoreClassConstants.SQLDATE) {
            return convertObjectToSQLDate(sourceObject, schemaTypeQName);
        } else if (javaClass == CoreClassConstants.TIME) {
            return convertObjectToSQLTime(sourceObject, schemaTypeQName);
        } else if (javaClass ==  CoreClassConstants.TIMESTAMP) {
            return convertObjectToTimestamp(sourceObject, schemaTypeQName);
        } else if ((javaClass == Constants.QNAME_CLASS) && (sourceObject != null)) {
            return convertObjectToQName(sourceObject);
        } else if (javaClass == CoreClassConstants.STRING) {
            return convertObjectToString(sourceObject, schemaTypeQName);
        } else if ((javaClass == java.net.URI.class)) {
            return convertObjectToURI(sourceObject);
        } else if ((javaClass == CoreClassConstants.XML_GREGORIAN_CALENDAR)) {
            return convertObjectToXMLGregorianCalendar(sourceObject, schemaTypeQName);
        } else if ((javaClass == CoreClassConstants.DURATION)) {
            return convertObjectToDuration(sourceObject);
        } else if ((javaClass == CoreClassConstants.CHAR)) {
            return convertObjectToChar(sourceObject, schemaTypeQName);
        } else {
            try {
                return super.convertObject(sourceObject, javaClass);
            } catch (ConversionException ex) {
                if (sourceObject.getClass() == CoreClassConstants.STRING) {
                    return super.convertObject(((String) sourceObject).trim(), javaClass);
                }
                throw ex;
            }
        }

        throw ConversionException.couldNotBeConverted(sourceObject, javaClass);
    }

    /**
     * Build a valid instance of XMLGregorianCalendar from the provided sourceObject.
     *
     * @param sourceObject
     * @param schemaTypeQName
     */
    protected XMLGregorianCalendar convertObjectToXMLGregorianCalendar(Object sourceObject, QName schemaTypeQName) throws ConversionException {
        if (sourceObject instanceof XMLGregorianCalendar) {
            return (XMLGregorianCalendar) sourceObject;
        }

        if (sourceObject instanceof String) {
            return convertStringToXMLGregorianCalendar((String) sourceObject, schemaTypeQName);
        }
        throw ConversionException.couldNotBeConverted(sourceObject, CoreClassConstants.XML_GREGORIAN_CALENDAR);
    }

    /**
     * Build a valid instance of XMLGregorianCalendar from the provided sourceObject.
     *
     * @param sourceObject
     */
    protected XMLGregorianCalendar convertObjectToXMLGregorianCalendar(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof XMLGregorianCalendar) {
            return (XMLGregorianCalendar) sourceObject;
        }

        if (sourceObject instanceof String) {
            return convertStringToXMLGregorianCalendar((String) sourceObject);
        }
        throw ConversionException.couldNotBeConverted(sourceObject, CoreClassConstants.XML_GREGORIAN_CALENDAR);
    }

    /**
     * Build a valid instance of Duration from the provided sourceObject.
     *
     * @param sourceObject
     */
    protected Duration convertObjectToDuration(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof Duration) {
            return (Duration) sourceObject;
        }
        if (sourceObject instanceof String) {
            return convertStringToDuration((String) sourceObject);
        }
        throw ConversionException.couldNotBeConverted(sourceObject, CoreClassConstants.DURATION);
    }

    /**
     * Build a valid instance of Character from the provided sourceObject.
     *
     * @param sourceObject
     */
    protected Character convertObjectToChar(Object sourceObject, QName schemaTypeQName) throws ConversionException {    	
        
        if (sourceObject == null || sourceObject.equals(Constants.EMPTY_STRING)) {
            return (char) 0;            
        }
        
        if(sourceObject instanceof String && isNumericQName(schemaTypeQName)){
        	int integer = Integer.parseInt((String)sourceObject);
        	
        	return Character.valueOf((char)integer);
        	
        }
        
        return super.convertObjectToChar(sourceObject);
    	
    }

    /**
     * Build a valid instance of Character from the provided sourceObject.
     *
     * @param sourceObject
     */
    protected Character convertObjectToChar(Object sourceObject) throws ConversionException {
        if (sourceObject == null || sourceObject.equals(Constants.EMPTY_STRING)) {
            return (char) 0;
        }

        return super.convertObjectToChar(sourceObject);
    }

    /**
     * Convert a String to a URI.
     *
     * @param sourceObject
     * @return
     * @throws ConversionException
     */
    protected java.net.URI convertObjectToURI(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof String) {
            try {
                return new java.net.URI((String) sourceObject);
            } catch (Exception ex) {
            }
        }

        throw ConversionException.couldNotBeConverted(sourceObject, java.net.URI.class);
    }

    /**
     * INTERNAL:
     * Converts given object to a QName object
     */
    protected QName convertObjectToQName(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof QName) {
            return (QName) sourceObject;
        }

        if (sourceObject instanceof String) {
            return qnameFromString((String) sourceObject);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, Constants.QNAME_CLASS);
    }

    /**
     * INTERNAL:
     * Converts given object to a Calendar object
     */
    protected Calendar convertObjectToCalendar(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof String) {
            String sourceString = (String) sourceObject;
            if (sourceString.lastIndexOf('T') != -1) {
                return convertStringToCalendar((String) sourceObject, Constants.DATE_TIME_QNAME);
            } else {
                if (sourceString.lastIndexOf(Constants.COLON) != -1) {
                    return convertStringToCalendar((String) sourceObject, Constants.TIME_QNAME);
                } else {
                    return convertStringToCalendar((String) sourceObject, Constants.DATE_QNAME);
                }
            }
        }
        return super.convertObjectToCalendar(sourceObject);
    }

    /**
     * INTERNAL:
     * Converts objects to their string representations.
     */
    protected String convertObjectToString(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof Calendar) {
            return stringFromCalendar((Calendar) sourceObject);
        }
        if (sourceObject instanceof Character && sourceObject.equals((char) 0)) {
            return Constants.EMPTY_STRING;
        }
        if (sourceObject instanceof QName) {
            return stringFromQName((QName) sourceObject);
        }
        if (sourceObject instanceof java.sql.Date) {
            return stringFromSQLDate((java.sql.Date) sourceObject);
        }
        if (sourceObject instanceof java.sql.Time) {
            return stringFromSQLTime((java.sql.Time) sourceObject);
        }
        if (sourceObject instanceof java.sql.Timestamp) {
            return stringFromTimestamp((Timestamp) sourceObject);
        }
        if (sourceObject instanceof java.util.Date) {
            return stringFromDate((java.util.Date) sourceObject);
        }
        if (sourceObject instanceof XMLGregorianCalendar) {
            return stringFromXMLGregorianCalendar((XMLGregorianCalendar) sourceObject);
        }
        if (sourceObject instanceof Duration) {
            return stringFromDuration((Duration) sourceObject);
        }
        if(sourceObject instanceof Double){
            if(Double.POSITIVE_INFINITY == ((Double)sourceObject)){
                return Constants.POSITIVE_INFINITY;
            }
            if(Double.NEGATIVE_INFINITY == ((Double)sourceObject)){
                return Constants.NEGATIVE_INFINITY;
            }
            return ((Double)sourceObject).toString();
        }
        if(sourceObject instanceof Float){
            if(Float.POSITIVE_INFINITY == ((Float)sourceObject)){
                return Constants.POSITIVE_INFINITY;
            }
            if(Float.NEGATIVE_INFINITY == ((Float)sourceObject)){
                return Constants.NEGATIVE_INFINITY;
            }
            return ((Float)sourceObject).toString();
        }

        return super.convertObjectToString(sourceObject);
    }

    protected String convertObjectToString(Object sourceObject, QName schemaTypeQName) throws ConversionException {
        if (sourceObject instanceof Calendar) {
            return stringFromCalendar((Calendar) sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof Character){
        	
        	if(isNumericQName(schemaTypeQName)){
        		return Integer.toString((int) (Character)sourceObject);        	
        	} else {        	
                    if(sourceObject.equals((char) 0)) {        
                        return Constants.EMPTY_STRING;
                    }
                   super.convertObjectToString(sourceObject);
        	}
        }
        if (sourceObject instanceof QName) {
            return stringFromQName((QName) sourceObject);
        }
        if (sourceObject instanceof java.sql.Date) {
            return stringFromSQLDate((java.sql.Date) sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof java.sql.Time) {
            return stringFromSQLTime((java.sql.Time) sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof java.sql.Timestamp) {
            return stringFromTimestamp((Timestamp) sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof java.util.Date) {
            return stringFromDate((java.util.Date) sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof XMLGregorianCalendar) {
            return stringFromXMLGregorianCalendar((XMLGregorianCalendar) sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof Duration) {
            return stringFromDuration((Duration) sourceObject);
        }
        if(sourceObject instanceof Double){
            if(Double.POSITIVE_INFINITY == ((Double)sourceObject)){
                return Constants.POSITIVE_INFINITY;
            }
            if(Double.NEGATIVE_INFINITY == ((Double)sourceObject)){
                return Constants.NEGATIVE_INFINITY;
            }
            return ((Double)sourceObject).toString();
        }
        if(sourceObject instanceof Float){
            if(Float.POSITIVE_INFINITY == ((Float)sourceObject)){
                return Constants.POSITIVE_INFINITY;
            }
            if(Float.NEGATIVE_INFINITY == ((Float)sourceObject)){
                return Constants.NEGATIVE_INFINITY;
            }
            return ((Float)sourceObject).toString();
        }

        return super.convertObjectToString(sourceObject);

    }

    private Calendar convertObjectToCalendar(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            return convertStringToCalendar((String) sourceObject, schemaTypeQName);
        }
        return super.convertObjectToCalendar(sourceObject);
    }

    protected java.sql.Date convertObjectToDate(Object sourceObject) throws ConversionException {
        Object o = sourceObject;
        if (sourceObject instanceof Calendar) {
            // Clone the calendar, because calling get() methods
            // later on will alter the original calendar
            o = ((Calendar) sourceObject).clone();
        }
        return super.convertObjectToDate(o);
    }

    /**
     * Convert the object to an instance of Double.
     * @param                    sourceObject Object of type String or Number.
     * @caught exception    The Double(String) constructor throws a
     *         NumberFormatException if the String does not contain a
     *        parsable double.
     */
   protected Double convertObjectToDouble(Object sourceObject) throws ConversionException {
       if (sourceObject instanceof String) {
           if(((String) sourceObject).length() == 0) {
               return 0d;
           }else if(Constants.POSITIVE_INFINITY.equals(sourceObject)){
               return Double.valueOf(Double.POSITIVE_INFINITY);
           }else if(Constants.NEGATIVE_INFINITY.equals(sourceObject)){
               return Double.valueOf(Double.NEGATIVE_INFINITY);
           }else{
               return super.convertObjectToDouble(sourceObject);
           }
       }else{
           return super.convertObjectToDouble(sourceObject);
       }
   }

   /**
    * Build a valid Float instance from a String or another Number instance.
    * @caught exception    The Float(String) constructor throws a
    *         NumberFormatException if the String does not contain a
    *        parsable Float.
    */
   protected Float convertObjectToFloat(Object sourceObject) throws ConversionException {
       if (sourceObject instanceof String) {
           if(((String) sourceObject).length() == 0) {
               return 0f;
           } else if(Constants.POSITIVE_INFINITY.equals(sourceObject)){
               return new Float(Float.POSITIVE_INFINITY);
           }else if(Constants.NEGATIVE_INFINITY.equals(sourceObject)){
               return new Float(Float.NEGATIVE_INFINITY);
           }
       }
       return super.convertObjectToFloat(sourceObject);
   }

   /**
    * Build a valid Integer instance from a String or another Number instance.
    * @caught exception    The Integer(String) constructor throws a
    *         NumberFormatException if the String does not contain a
    *        parsable integer.
    */
   protected Integer convertObjectToInteger(Object sourceObject) throws ConversionException {
       if(sourceObject instanceof String) {
           String sourceString = (String) sourceObject;
           if(sourceString.length() == 0) {
               return 0;
           } else if(sourceString.charAt(0) == PLUS) {
               return super.convertObjectToInteger(sourceString.substring(1));
           }
       }
       return super.convertObjectToInteger(sourceObject);
   }

   /**
    * Build a valid Long instance from a String or another Number instance.
    * @caught exception    The Long(String) constructor throws a
    *         NumberFormatException if the String does not contain a
    *        parsable long.
    *
    */
  protected Long convertObjectToLong(Object sourceObject) throws ConversionException {
      if(sourceObject instanceof String) {
          String sourceString = (String) sourceObject;
          if(sourceString.length() == 0) {
              return 0l;
          } else if(sourceString.charAt(0) == PLUS) {
              return super.convertObjectToLong(sourceString.substring(1));
          }
      }
      return super.convertObjectToLong(sourceObject);
  }

  /**
   * INTERNAL:
   * Build a valid Short instance from a String or another Number instance.
   * @caught exception    The Short(String) constructor throws a
   *     NumberFormatException if the String does not contain a
   *    parsable short.
   */
  protected Short convertObjectToShort(Object sourceObject) throws ConversionException {
      if(sourceObject instanceof String) {
          String sourceString = (String) sourceObject;
          if(sourceString.length() == 0) {
              return 0;
          } else if(sourceString.charAt(0) == PLUS) {
              return super.convertObjectToShort(sourceString.substring(1));
          }
      }
      return super.convertObjectToShort(sourceObject);
  }

  /**
   * INTERNAL:
   * Build a valid BigDecimal instance from a String or another
   * Number instance.  BigDecimal is the most general type so is
   * must be returned when an object is converted to a number.
   * @caught exception    The BigDecimal(String) constructor throws a
   *     NumberFormatException if the String does not contain a
   *    parsable BigDecimal.
   */
  protected BigDecimal convertObjectToNumber(Object sourceObject) throws ConversionException {
      if(sourceObject instanceof String) {
          String sourceString = (String) sourceObject;
          if(sourceString.length() == 0) {
              return BigDecimal.ZERO;
          } else if(sourceString.charAt(0) == PLUS) {
              return super.convertObjectToNumber(sourceString.substring(1));
          }
      }
      return super.convertObjectToNumber(sourceObject);
  }

   /**
    * Build a valid instance of BigInteger from the provided sourceObject.
    *    @param sourceObject    Valid instance of String, BigDecimal, or any Number
    */
   protected BigInteger convertObjectToBigInteger(Object sourceObject) throws ConversionException {
       if(sourceObject instanceof String) {
           String sourceString = (String) sourceObject;
           if(sourceString.length() == 0) {
               return null;
           } else if(sourceString.charAt(0) == PLUS) {
               return super.convertObjectToBigInteger(sourceString.substring(1));
           }
       }
       return super.convertObjectToBigInteger(sourceObject);
   }

   /**
    * Build a valid instance of BigDecimal from the given sourceObject
    *    @param sourceObject    Valid instance of String, BigInteger, any Number
    */
   protected BigDecimal convertObjectToBigDecimal(Object sourceObject) throws ConversionException {
       if(sourceObject instanceof String) {
           String sourceString = (String) sourceObject;
           if(sourceString.length() == 0) {
               return null;
           }
           try {
               return new BigDecimal(sourceString);
           } catch (NumberFormatException exception) {
               throw ConversionException.couldNotBeConverted(sourceObject, CoreClassConstants.BIGDECIMAL, exception);
           }
       }
       return super.convertObjectToBigDecimal(sourceObject);
    }

   @Override
   protected Boolean convertObjectToBoolean(Object sourceObject) {
       if (sourceObject == null || sourceObject.equals(Constants.EMPTY_STRING)) {
           return false;
       }

       return super.convertObjectToBoolean(sourceObject);
   }

/**
    * Build a valid instance of Byte from the provided sourceObject
    * @param sourceObject    Valid instance of String or any Number
    * @caught exception        The Byte(String) constructor throws a
    *     NumberFormatException if the String does not contain a
    *        parsable byte.
    *
    */
    protected Byte convertObjectToByte(Object sourceObject) throws ConversionException {
        if(sourceObject instanceof String) {
            String sourceString = (String) sourceObject;
            if(sourceString.length() == 0) {
                return 0;
            } else if(sourceString.charAt(0) == PLUS) {
                return super.convertObjectToByte(sourceString.substring(1));
            }
        }
        return super.convertObjectToByte(sourceObject);
    }

    public XMLGregorianCalendar convertStringToXMLGregorianCalendar(String sourceString, QName schemaTypeQName) {
        XMLGregorianCalendar xmlGregorianCalender = null;
        try {
            xmlGregorianCalender = convertStringToXMLGregorianCalendar(sourceString);
        } catch (Exception ex) {
             if (Constants.DATE_QNAME.equals(schemaTypeQName)) {
                 throw ConversionException.incorrectDateFormat(sourceString);
             } else if (Constants.TIME_QNAME.equals(schemaTypeQName)) {
                 throw ConversionException.incorrectTimeFormat(sourceString);
             } else if (Constants.G_DAY_QNAME.equals(schemaTypeQName)) {
                 throw XMLConversionException.incorrectGDayFormat(sourceString);
             } else if (Constants.G_MONTH_QNAME.equals(schemaTypeQName)) {
                 throw XMLConversionException.incorrectGMonthFormat(sourceString);
             } else if (Constants.G_MONTH_DAY_QNAME.equals(schemaTypeQName)) {
                 throw XMLConversionException.incorrectGMonthDayFormat(sourceString);
             } else if (Constants.G_YEAR_QNAME.equals(schemaTypeQName)) {
                 throw XMLConversionException.incorrectGYearFormat(sourceString);
             } else if (Constants.G_YEAR_MONTH_QNAME.equals(schemaTypeQName)) {
                 throw XMLConversionException.incorrectGYearMonthFormat(sourceString);
             } else if (Constants.DURATION_QNAME.equals(schemaTypeQName)) {
                 throw new IllegalArgumentException();
             } else {
                 throw ConversionException.incorrectDateTimeFormat(sourceString);
             }
        }

        if(xmlGregorianCalender == null){
        	return null;
        }
            
        QName calendarQName = xmlGregorianCalender.getXMLSchemaType();
	    if (!calendarQName.equals(schemaTypeQName)) {
	        if (Constants.DATE_QNAME.equals(schemaTypeQName)) {
	            if (calendarQName.equals(Constants.DATE_TIME_QNAME)) {
	                //clear out the time portion
	                xmlGregorianCalender.setHour(DatatypeConstants.FIELD_UNDEFINED);
	                xmlGregorianCalender.setMinute(DatatypeConstants.FIELD_UNDEFINED);
	                xmlGregorianCalender.setSecond(DatatypeConstants.FIELD_UNDEFINED);
	                xmlGregorianCalender.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
	                return xmlGregorianCalender;
	            } else {
	                throw ConversionException.incorrectDateFormat(sourceString);
	            }
	         } else if (Constants.TIME_QNAME.equals(schemaTypeQName)) {
	             throw ConversionException.incorrectTimeFormat(sourceString);
	         } else if (Constants.G_DAY_QNAME.equals(schemaTypeQName)) {
	             throw XMLConversionException.incorrectGDayFormat(sourceString);
	         } else if (Constants.G_MONTH_QNAME.equals(schemaTypeQName)) {
	             throw XMLConversionException.incorrectGMonthFormat(sourceString);
	         } else if (Constants.G_MONTH_DAY_QNAME.equals(schemaTypeQName)) {
	             throw XMLConversionException.incorrectGMonthDayFormat(sourceString);
	         } else if (Constants.G_YEAR_QNAME.equals(schemaTypeQName)) {
	             throw XMLConversionException.incorrectGYearFormat(sourceString);
	         } else if (Constants.G_YEAR_MONTH_QNAME.equals(schemaTypeQName)) {
	             throw XMLConversionException.incorrectGYearMonthFormat(sourceString);
	         } else if (Constants.DURATION_QNAME.equals(schemaTypeQName)) {
	             throw new IllegalArgumentException();
	         } else if (Constants.DATE_TIME_QNAME.equals(schemaTypeQName)) {
	             throw ConversionException.incorrectDateTimeFormat(sourceString);
	         }	        
        }
        return xmlGregorianCalender;
    }

    /**
     * Return an XMLGregorianCalander created with a given date string
     *
     * @param dateString
     * @return
     */
    public XMLGregorianCalendar convertStringToXMLGregorianCalendar(String sourceString) {
        // Trim in case of leading or trailing whitespace
        String trimmedSourceString = sourceString.trim();
        if(trimmedSourceString.length() == 0) {
            return null;
        }
        XMLGregorianCalendar calToReturn = null;
        try {
            calToReturn = getDatatypeFactory().newXMLGregorianCalendar(trimmedSourceString);
        } catch (IllegalArgumentException e1) {
            try {
                // GMonths may have different representations:
                // JDK 1.5: "--MM--"   JDK 1.6: "--MM"
                // If we caught an IllegalArgumentException, try the other syntax

                int length = trimmedSourceString.length();
                String retryString = null;
                if (length >= 6 && (trimmedSourceString.charAt(4) == '-') && (trimmedSourceString.charAt(5) == '-')) {
                    // Try to omit the trailing dashes if any (--MM--),
                    // but preserve time zone specifier, if any.
                    retryString = new StringBuilder(
                            trimmedSourceString.substring(0, 4)).append(
                            length > 6 ? trimmedSourceString.substring(6) : "")
                            .toString();
                } else if (length >= 4) {
                    // For "--MM" add the trailing dashes, preserving
                    // any trailing time zone specifier.
                    retryString = new StringBuilder(
                            trimmedSourceString.substring(0, 4)).append("--").append(
                            length > 4 ? trimmedSourceString.substring(4) : "")
                            .toString();
                }
                if (retryString != null) {
                    calToReturn = getDatatypeFactory().newXMLGregorianCalendar(retryString);
                } else {
                    throw e1;
                }
            } catch (IllegalArgumentException e2) {
                throw e1;
            }
        }

        return calToReturn;
    }

    /**
     * Return a Duration created with a given date string.
     *
     * @param dateString
     * @return
     */
    public Duration convertStringToDuration(String sourceString) {
        if(sourceString == null || sourceString.length() == 0) {
            return null;
        }
        return getDatatypeFactory().newDuration(sourceString);
    }

    public Calendar convertStringToCalendar(String sourceString, QName schemaTypeQName) {
        XMLGregorianCalendar xmlGregorianCalender = convertStringToXMLGregorianCalendar(sourceString, schemaTypeQName);
        if(xmlGregorianCalender == null) {
            return null;
        }
        return toCalendar(xmlGregorianCalender);
    }

    private Date convertObjectToUtilDate(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            return convertStringToDate((String) sourceObject, schemaTypeQName);
        }
        return super.convertObjectToUtilDate(sourceObject);
    }

    protected java.sql.Date convertObjectToSQLDate(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            Date date = convertStringToDate((String) sourceObject, schemaTypeQName);
            return new java.sql.Date((date.getTime() / 1000) * 1000);
        }
        return convertObjectToDate(sourceObject);
    }

    protected Time convertObjectToSQLTime(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            Date date = convertStringToDate((String) sourceObject, schemaTypeQName);
            return new java.sql.Time((date.getTime() / 1000) * 1000);
        }
        return super.convertObjectToTime(sourceObject);
    }

    protected Timestamp convertStringToTimestamp(String sourceObject) {
        return convertStringToTimestamp(sourceObject, Constants.DATE_TIME_QNAME);
    }

    protected Timestamp convertObjectToTimestamp(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            return convertStringToTimestamp((String) sourceObject, schemaTypeQName);
        }
        return super.convertObjectToTimestamp(sourceObject);
    }

    public java.sql.Timestamp convertStringToTimestamp(String sourceString, QName schemaType) {

        XMLGregorianCalendar xmlGregorianCalender = null;
        try {
            xmlGregorianCalender = convertStringToXMLGregorianCalendar(sourceString);
        } catch(Exception ex) {

             if (Constants.DATE_QNAME.equals(schemaType)) {
                 throw ConversionException.incorrectDateFormat(sourceString);
             }else if (Constants.TIME_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectTimestampTimeFormat(sourceString);
             }else if (Constants.G_DAY_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGDayFormat(sourceString);
             } else if (Constants.G_MONTH_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGMonthFormat(sourceString);
             }else if (Constants.G_MONTH_DAY_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGMonthDayFormat(sourceString);
             }else if (Constants.G_YEAR_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGYearFormat(sourceString);
             }else if (Constants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGYearMonthFormat(sourceString);
             }else if (Constants.DURATION_QNAME.equals(schemaType)) {
                 throw new IllegalArgumentException();
             }else{
                 throw XMLConversionException.incorrectTimestampDateTimeFormat(sourceString);
             }
        }
        if(xmlGregorianCalender == null) {
            return null;
        }
        GregorianCalendar cal = xmlGregorianCalender.toGregorianCalendar();
        if(xmlGregorianCalender.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            cal.setTimeZone(getTimeZone());
        }
        QName  calendarQName = xmlGregorianCalender.getXMLSchemaType();
        if(!calendarQName.equals(schemaType)){
            if (Constants.DATE_QNAME.equals(schemaType)){
                if(calendarQName.equals(Constants.DATE_TIME_QNAME)) {
                    //clear out the time portion
                    cal.clear(Calendar.HOUR_OF_DAY);
                    cal.clear(Calendar.MINUTE);
                    cal.clear(Calendar.SECOND);
                    cal.clear(Calendar.MILLISECOND);
                    return Helper.timestampFromCalendar(cal);

                }else{
                    throw ConversionException.incorrectDateFormat(sourceString);
                }
             }else if (Constants.TIME_QNAME.equals(schemaType)){
                 throw XMLConversionException.incorrectTimestampTimeFormat(sourceString);
             }else if (Constants.G_DAY_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGDayFormat(sourceString);

             } else if (Constants.G_MONTH_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGMonthFormat(sourceString);

             }else if (Constants.G_MONTH_DAY_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGMonthDayFormat(sourceString);

             }else if (Constants.G_YEAR_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGYearFormat(sourceString);

             }else if (Constants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
                 throw XMLConversionException.incorrectGYearMonthFormat(sourceString);

             }else if (Constants.DURATION_QNAME.equals(schemaType)) {
                 throw new IllegalArgumentException();

             }else if (Constants.DATE_TIME_QNAME.equals(schemaType)) { //&& Constants.DATE_QNAME.equals(calendarQName)) {
                 throw XMLConversionException.incorrectTimestampDateTimeFormat(sourceString);

             }
        }
        Timestamp timestamp = Helper.timestampFromCalendar(cal);
        String trimmedSourceString = sourceString.trim();
        int decimalIndex = trimmedSourceString.lastIndexOf('.');

        if(-1 == decimalIndex) {
            return timestamp;
        }else{
            int timeZoneIndex = trimmedSourceString.lastIndexOf(GMT_SUFFIX);
            if(-1 == timeZoneIndex) {
                timeZoneIndex = trimmedSourceString.lastIndexOf('-');
                if(timeZoneIndex < decimalIndex) {
                    timeZoneIndex = -1;
                }
                if(-1 == timeZoneIndex) {
                    timeZoneIndex = trimmedSourceString.lastIndexOf('+');
                }

            }

            String nsString;
            if(-1 == timeZoneIndex) {
                nsString = trimmedSourceString.substring(decimalIndex + 1);
            } else {
                nsString = trimmedSourceString.substring((decimalIndex + 1), timeZoneIndex);
            }
            double ns = Long.valueOf(nsString).doubleValue();
            ns = ns * Math.pow(10, 9 - nsString.length());
            timestamp.setNanos((int) ns);
            return timestamp;
        }

    }

    public String stringFromCalendar(Calendar sourceCalendar, QName schemaTypeQName) {
        // Clone the calendar, because calling get() methods will
        // alter the original calendar
        Calendar cal = (Calendar) sourceCalendar.clone();

        XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
        // use the timezone info on source calendar, if any
        if (sourceCalendar.isSet(Calendar.ZONE_OFFSET) || isTimeZoneQualified()) {
            if(sourceCalendar.isSet(Calendar.DST_OFFSET)) {
                xgc.setTimezone((cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / 60000);
            } else {
                xgc.setTimezone((cal.get(Calendar.ZONE_OFFSET)) / 60000);
            }
        }

        // gDay
        if (Constants.G_DAY_QNAME.equals(schemaTypeQName)) {
            xgc.setDay(cal.get(Calendar.DATE));
            return xgc.toXMLFormat();
        }
        // gMonth
        if (Constants.G_MONTH_QNAME.equals(schemaTypeQName)) {
            xgc.setMonth(cal.get(Calendar.MONTH) + 1);
            // Note: 'XML Schema:  Datatypes' indicates that the lexical representation is "--MM--"
            // but the truncated representation as described in 5.2.1.3 of ISO 8601:1988 is "--MM".
            // We always want to return the 1.5 syntax ("--MM--") to comply with the JAXB RI.
            String xmlFormat = xgc.toXMLFormat();
            String pre  = xmlFormat.substring(0, 4); // will always be --MM
            String post = Constants.EMPTY_STRING;

            // --MM--
            if (xmlFormat.length() == 6) {
                if (trimGMonth()) {
                    return pre;
                }
                return xmlFormat;
            }

            // --MM--Z or --MM--+03:00
            if (xmlFormat.length() == 7 || xmlFormat.length() == 12) {
                if (trimGMonth()) {
                    return pre + xmlFormat.substring(6);
                }
                return xmlFormat;
            }

            // --MM
            if (xmlFormat.length() == 4) {
                if (trimGMonth()) {
                    return xmlFormat;
                }
                post = "--";
            }

            // --MMZ or --MM+03:00
            if (xmlFormat.length() == 5 || xmlFormat.length() == 10) {
                if (trimGMonth()) {
                    return xmlFormat;
                }
                post = "--" + xmlFormat.substring(4);
            }

            return pre + post;
        }
        // gMonthDay
        if (Constants.G_MONTH_DAY_QNAME.equals(schemaTypeQName)) {
            xgc.setMonth(cal.get(Calendar.MONTH) + 1);
            xgc.setDay(cal.get(Calendar.DATE));
            return xgc.toXMLFormat();
        }
        // gYear
        if (Constants.G_YEAR_QNAME.equals(schemaTypeQName)) {
            if (cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            } else {
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            return xgc.toXMLFormat();
        }
        // gYearMonth
        if (Constants.G_YEAR_MONTH_QNAME.equals(schemaTypeQName)) {
            if (cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            } else {
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH) + 1);
            return xgc.toXMLFormat();
        }
        // Date
        if (Constants.DATE_QNAME.equals(schemaTypeQName)) {
            if (cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            } else {
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH) + 1);
            xgc.setDay(cal.get(Calendar.DATE));
            return xgc.toXMLFormat();
        }
        // Time
        if (Constants.TIME_QNAME.equals(schemaTypeQName)) {
            int milliseconds = cal.get(Calendar.MILLISECOND);
            if(0 == milliseconds) {
                milliseconds = DatatypeConstants.FIELD_UNDEFINED;
            }
            xgc.setTime(
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND),
                    milliseconds);
            return truncateMillis(xgc.toXMLFormat());
        }
        // DateTime
        if (cal.get(Calendar.ERA) == GregorianCalendar.BC){
            xgc.setYear(-cal.get(Calendar.YEAR));
        } else {
            xgc.setYear(cal.get(Calendar.YEAR));
        }
        xgc.setMonth(cal.get(Calendar.MONTH) + 1);
        xgc.setDay(cal.get(Calendar.DATE));
        int milliseconds = cal.get(Calendar.MILLISECOND);
        if(0 == milliseconds) {
            milliseconds = DatatypeConstants.FIELD_UNDEFINED;
        }
        xgc.setTime(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                milliseconds);

        return truncateMillis(xgc.toXMLFormat());
    }

    /**
     * Truncate any trailing zeros from the millisecond portion of a given string.
     * The string is assumed to be in dateTime or time format, as returned by
     * XMLGregorianCalendar.toXMLFormat().
     *
     * @param xmlFormat
     * @return
     */
    private String truncateMillis(String xmlFormat) {
        String result = xmlFormat;
        int dotIdx = xmlFormat.indexOf('.');
        if (dotIdx > 0) {
            String pre = xmlFormat.substring(0, dotIdx);
            String post = Constants.EMPTY_STRING;
            if (xmlFormat.length() > (dotIdx + 4)) {
                post = xmlFormat.substring(dotIdx + 4, xmlFormat.length());
            }
            String milliStr = xmlFormat.substring(dotIdx + 1, dotIdx + 4);
            char[] numbChar = new char[milliStr.length()];
            milliStr.getChars(0, milliStr.length(), numbChar, 0);
            int truncIndex = 2;
            while (truncIndex >= 1 && numbChar[truncIndex] == '0') {
                truncIndex--;
            }
            milliStr = new String(numbChar, 0, truncIndex + 1);
            if (milliStr.length() > 0 && !"0".equals(milliStr)) {
                milliStr = '.' + milliStr;
                result = pre + milliStr + post;
            } else {
                result = pre + post;
            }
        }
        return result;
    }

    private String stringFromCalendar(Calendar sourceCalendar) {
        if (!(sourceCalendar.isSet(Calendar.HOUR) || sourceCalendar.isSet(Calendar.MINUTE) || sourceCalendar.isSet(Calendar.SECOND) || sourceCalendar.isSet(Calendar.MILLISECOND))) {
            return stringFromCalendar(sourceCalendar, Constants.DATE_QNAME);
        } else if (!(sourceCalendar.isSet(Calendar.YEAR) || sourceCalendar.isSet(Calendar.MONTH) || sourceCalendar.isSet(Calendar.DATE))) {
            return stringFromCalendar(sourceCalendar, Constants.TIME_QNAME);
        } else {
            return stringFromCalendar(sourceCalendar, Constants.DATE_TIME_QNAME);
        }
    }

    public java.util.Date convertStringToDate(String sourceString, QName schemaType) {
        XMLGregorianCalendar xmlGregorianCalender = convertStringToXMLGregorianCalendar(sourceString, schemaType);
        if(xmlGregorianCalender == null) {
            return null;
        }
        Calendar cal = toCalendar(xmlGregorianCalender);
        Date returnDate = cal.getTime();

        return returnDate;
    }

    private Calendar toCalendar(XMLGregorianCalendar xgc) {
        TimeZone tz = null;

        if (xgc.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            tz = getTimeZone();
        } else {
            tz = xgc.getTimeZone(xgc.getTimezone());
        }


        Calendar cal = Calendar.getInstance(tz, Locale.getDefault());
        cal.clear();

        if (xgc.getTimezone() != DatatypeConstants.FIELD_UNDEFINED) {
            cal.set(Calendar.ZONE_OFFSET, xgc.getTimezone() * 60000);
        }

        BigInteger year = xgc.getEonAndYear();
        if (year != null) {
            cal.set(Calendar.ERA, year.signum() < 0 ? GregorianCalendar.BC : GregorianCalendar.AD);
            cal.set(Calendar.YEAR, year.abs().intValue());
        }

        if (xgc.getDay() != DatatypeConstants.FIELD_UNDEFINED)
            cal.set(Calendar.DAY_OF_MONTH, xgc.getDay());

        if (xgc.getMonth() != DatatypeConstants.FIELD_UNDEFINED)
            cal.set(Calendar.MONTH, xgc.getMonth() - 1);

        if (xgc.getHour() != DatatypeConstants.FIELD_UNDEFINED)
            cal.set(Calendar.HOUR_OF_DAY, xgc.getHour());

        if (xgc.getMinute() != DatatypeConstants.FIELD_UNDEFINED)
            cal.set(Calendar.MINUTE, xgc.getMinute());

        if (xgc.getSecond() != DatatypeConstants.FIELD_UNDEFINED)
            cal.set(Calendar.SECOND, xgc.getSecond());

        if (xgc.getFractionalSecond() != null)
            cal.set(Calendar.MILLISECOND, xgc.getMillisecond());

        return cal;
    }

    /**
     * This method returns a dateTime string representing a given
     * java.util.Date.
     *
     * BC dates (sourceDate.getTime() < YEAR_ONE_AD_TIME) are handled
     * as follows: '2007 BC' --> '-2006 AD'
     *
     * @param sourceDate
     * @return
     */
    private String stringFromDate(java.util.Date sourceDate) {
        XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();

        GregorianCalendar cal = new GregorianCalendar(getTimeZone());
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        cal.setTime(sourceDate);

        if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
            xgc.setYear(-cal.get(Calendar.YEAR));
        }else{
            xgc.setYear(cal.get(Calendar.YEAR));
        }
        xgc.setMonth(cal.get(Calendar.MONTH)+1);
        xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

        xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
        xgc.setMinute(cal.get(Calendar.MINUTE));
        xgc.setSecond(cal.get(Calendar.SECOND));

        String string= xgc.toXMLFormat();
        string = appendMillis(string, sourceDate.getTime());
        string = appendTimeZone(string, sourceDate);
        return string;

    }

    /**
     * This method returns a string representing a given java.util.Date
     * based on a given schema type QName.
     *
     * BC dates (sourceDate.getTime() < YEAR_ONE_AD_TIME) are handled
     * as follows: '2007 BC' --> '-2006 AD'.
     *
     * @param sourceDate
     * @param schemaType
     * @return
     */
    public String stringFromDate(java.util.Date sourceDate, QName schemaType) {

        XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();


        if (Constants.DATE_QNAME.equals(schemaType)) {

            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTime(sourceDate);

            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));
            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }

            return xgc.toXMLFormat();

        }
        if (Constants.TIME_QNAME.equals(schemaType)) {

            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTime(sourceDate);

            xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
            xgc.setMinute(cal.get(Calendar.MINUTE));
            xgc.setSecond(cal.get(Calendar.SECOND));

            String string = xgc.toXMLFormat();
            string = appendMillis(string, sourceDate.getTime());
            return appendTimeZone(string, sourceDate);
        }
        if (Constants.G_DAY_QNAME.equals(schemaType)) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTime(sourceDate);
            xgc.setDay(cal.get(Calendar.DATE));
            return xgc.toXMLFormat();
        }
        if (Constants.G_MONTH_QNAME.equals(schemaType)) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTime(sourceDate);

            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return stringFromXMLGregorianCalendar(xgc, schemaType);
        }
        if (Constants.G_MONTH_DAY_QNAME.equals(schemaType)) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTime(sourceDate);
            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));
            return xgc.toXMLFormat();
        }
        if (Constants.G_YEAR_QNAME.equals(schemaType)) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTime(sourceDate);
            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }

            return xgc.toXMLFormat();
        }
        if (Constants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setTime(sourceDate);
            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return xgc.toXMLFormat();
        }
        if (Constants.DURATION_QNAME.equals(schemaType)) {
            throw new IllegalArgumentException();
        }
        // default is dateTime
        GregorianCalendar cal = new GregorianCalendar(getTimeZone());
        cal.setTime(sourceDate);
        xgc = getDatatypeFactory().newXMLGregorianCalendar(cal);

        if(!isTimeZoneQualified()){
            xgc.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        }

        String string =  xgc.toXMLFormat();
        return truncateMillis(string);
    }

    private String stringFromSQLDate(java.sql.Date sourceDate) {
        XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();

        GregorianCalendar cal = new GregorianCalendar(getTimeZone());
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        cal.setTime(sourceDate);

        if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
            xgc.setYear(-cal.get(Calendar.YEAR));
        }else{
            xgc.setYear(cal.get(Calendar.YEAR));
        }
        xgc.setMonth(cal.get(Calendar.MONTH)+1);
        xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

        return xgc.toXMLFormat();
    }

    private String stringFromSQLDate(java.sql.Date sourceDate, QName schemaType) {
        if (Constants.DATE_TIME_QNAME.equals(schemaType)) {

            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

            xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
            xgc.setMinute(cal.get(Calendar.MINUTE));
            xgc.setSecond(cal.get(Calendar.SECOND));

            String string = xgc.toXMLFormat();
            return appendTimeZone(string, sourceDate);
        } else if (Constants.TIME_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
            xgc.setMinute(cal.get(Calendar.MINUTE));
            xgc.setSecond(cal.get(Calendar.SECOND));

            String string = xgc.toXMLFormat();
            return appendTimeZone(string, sourceDate);

        } else if (Constants.G_DAY_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();

            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));
            return xgc.toXMLFormat();

        } else if (Constants.G_MONTH_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return stringFromXMLGregorianCalendar(xgc, schemaType);
        } else if (Constants.G_MONTH_DAY_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));
            return xgc.toXMLFormat();
        } else if (Constants.G_YEAR_QNAME.equals(schemaType)) {
             XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
             GregorianCalendar cal = new GregorianCalendar(getTimeZone());
             cal.setGregorianChange(new Date(Long.MIN_VALUE));
             cal.setTime(sourceDate);
             if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            return xgc.toXMLFormat();
        } else if (Constants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return xgc.toXMLFormat();
        } else if (Constants.DURATION_QNAME.equals(schemaType)) {
            throw new IllegalArgumentException();
        } else {
            return stringFromSQLDate(sourceDate);
        }
    }

    private String stringFromSQLTime(Time sourceTime) {
        XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();

        GregorianCalendar cal = new GregorianCalendar(getTimeZone());
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        cal.setTime(sourceTime);

        xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
        xgc.setMinute(cal.get(Calendar.MINUTE));
        xgc.setSecond(cal.get(Calendar.SECOND));

        String string= xgc.toXMLFormat();
        return appendTimeZone(string, sourceTime);
    }

    private String stringFromSQLTime(Time sourceTime, QName schemaType) {
        if (Constants.DATE_TIME_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceTime);

            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

            xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
            xgc.setMinute(cal.get(Calendar.MINUTE));
            xgc.setSecond(cal.get(Calendar.SECOND));
            String string= xgc.toXMLFormat();
            return appendTimeZone(string, sourceTime);

        } else if (Constants.DATE_QNAME.equals(schemaType)) {

            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceTime);

            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

            return xgc.toXMLFormat();
        } else if (Constants.G_DAY_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceTime);

            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

            return xgc.toXMLFormat();
        } else if (Constants.G_MONTH_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceTime);

            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return stringFromXMLGregorianCalendar(xgc, schemaType);
        } else if (Constants.G_MONTH_DAY_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceTime);

            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

            return xgc.toXMLFormat();

        } else if (Constants.G_YEAR_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceTime);
            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }

            return xgc.toXMLFormat();
        } else if (Constants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceTime);

            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return xgc.toXMLFormat();
        } else if (Constants.DURATION_QNAME.equals(schemaType)) {
            throw new IllegalArgumentException();
        } else {
            return stringFromSQLTime(sourceTime);
        }
    }


    /**
     * This method returns a dateTime string representing a given
     * Timestamp.
     *
     * BC dates (sourceDate.getTime() < YEAR_ONE_AD_TIME) are handled
     * as follows: '2007 BC' --> '-2006 AD'
     *
     * @param sourceDate
     * @return
     */
    private String stringFromTimestamp(Timestamp sourceDate) {
        GregorianCalendar cal = new GregorianCalendar(getTimeZone());
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        cal.setTime(sourceDate);

        XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
        if(cal.get(Calendar.ERA)== GregorianCalendar.BC){
            xgc.setYear(-cal.get(Calendar.YEAR));
        }else{
            xgc.setYear(cal.get(Calendar.YEAR));
        }
        xgc.setMonth(cal.get(Calendar.MONTH)+1);
        xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

        xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
        xgc.setMinute(cal.get(Calendar.MINUTE));
        xgc.setSecond(cal.get(Calendar.SECOND));

        String string= xgc.toXMLFormat();
        string = appendNanos(string, sourceDate);
        return appendTimeZone(string, sourceDate);
    }

    /**
     * This method returns a string representing a given Timestamp
     * based on a given schema type QName.
     *
     * BC dates (sourceDate.getTime() < YEAR_ONE_AD_TIME) are handled
     * as follows: '2007 BC' --> '-2006 AD'.
     *
     * @param sourceDate
     * @param schemaType
     * @return
     */
    private String stringFromTimestamp(Timestamp sourceDate, QName schemaType) {

        if (Constants.DATE_QNAME.equals(schemaType)) {
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();

            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);
            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

            return xgc.toXMLFormat();
        }
        if (Constants.TIME_QNAME.equals(schemaType)) {

            Calendar cal = Calendar.getInstance(getTimeZone());
            cal.setTimeInMillis(sourceDate.getTime());

            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
            xgc.setMinute(cal.get(Calendar.MINUTE));
            xgc.setSecond(cal.get(Calendar.SECOND));

            String string = xgc.toXMLFormat();
            string = appendNanos(string, sourceDate);
            return appendTimeZone(string, sourceDate);
        }
        if (Constants.G_DAY_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);

            xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

            return xgc.toXMLFormat();
        }
        if (Constants.G_MONTH_QNAME.equals(schemaType)) {
             XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
             GregorianCalendar cal = new GregorianCalendar(getTimeZone());
             cal.setGregorianChange(new Date(Long.MIN_VALUE));
             cal.setTime(sourceDate);

            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return stringFromXMLGregorianCalendar(xgc, schemaType);
        }
        if (Constants.G_MONTH_DAY_QNAME.equals(schemaType)) {

             XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
             GregorianCalendar cal = new GregorianCalendar(getTimeZone());
             cal.setGregorianChange(new Date(Long.MIN_VALUE));
             cal.setTime(sourceDate);

             xgc.setMonth(cal.get(Calendar.MONTH)+1);
             xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));
             return xgc.toXMLFormat();
        }
        if (Constants.G_YEAR_QNAME.equals(schemaType)) {
             XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
             GregorianCalendar cal = new GregorianCalendar(getTimeZone());
             cal.setGregorianChange(new Date(Long.MIN_VALUE));
             cal.setTime(sourceDate);

            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }

            return xgc.toXMLFormat();
        }
        if (Constants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
            XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
            GregorianCalendar cal = new GregorianCalendar(getTimeZone());
            cal.setGregorianChange(new Date(Long.MIN_VALUE));
            cal.setTime(sourceDate);
            if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
                xgc.setYear(-cal.get(Calendar.YEAR));
            }else{
                xgc.setYear(cal.get(Calendar.YEAR));
            }
            xgc.setMonth(cal.get(Calendar.MONTH)+1);

            return xgc.toXMLFormat();
        }
        if (Constants.DURATION_QNAME.equals(schemaType)) {
            throw new IllegalArgumentException();
        }
        // default is dateTime
        XMLGregorianCalendar xgc = getDatatypeFactory().newXMLGregorianCalendar();
        GregorianCalendar cal = new GregorianCalendar(getTimeZone());
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        cal.setTime(sourceDate);
        if(cal.get(Calendar.ERA) == GregorianCalendar.BC){
            xgc.setYear(-cal.get(Calendar.YEAR));
        }else{
            xgc.setYear(cal.get(Calendar.YEAR));
        }
        xgc.setMonth(cal.get(Calendar.MONTH)+1);
        xgc.setDay(cal.get(Calendar.DAY_OF_MONTH));

        xgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
        xgc.setMinute(cal.get(Calendar.MINUTE));
        xgc.setSecond(cal.get(Calendar.SECOND));

        String string= xgc.toXMLFormat();

        string = appendNanos(string, sourceDate);
        return appendTimeZone(string, sourceDate);
    }

    private String stringFromXMLGregorianCalendar(XMLGregorianCalendar cal, QName schemaTypeQName) {
        GregorianCalendar gCal = cal.toGregorianCalendar();
        if(cal.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            gCal.clear(Calendar.ZONE_OFFSET);
        }
        return stringFromCalendar(gCal, schemaTypeQName);
    }

    private String stringFromXMLGregorianCalendar(XMLGregorianCalendar cal) {
        return cal.toXMLFormat();
    }

    private String stringFromDuration(Duration dur) {
        return dur.toString();
    }

    private String stringFromQName(QName sourceQName) {
        // String will be formatted as: {namespaceURI}localPart
        return sourceQName.toString();
    }

    private QName qnameFromString(String sourceString) {
        // String will be formatted as: {namespaceURI}localPart
        if (sourceString.indexOf('{') != -1) {
            String uri = sourceString.substring(sourceString.indexOf('{') + 1, sourceString.indexOf('}'));
            String localpart = sourceString.substring(sourceString.indexOf('}') + 1);
            return new QName(uri, localpart);
        } else {
            return new QName(sourceString);
        }
    }

    /**
     * INTERNAL:
     * Converts a String which is in Base64 format to a Byte[]
     */
    public byte[] convertSchemaBase64ToByteArray(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof String) {
            //the base64 string may have contained embedded whitespaces. Try again after
            //Removing any whitespaces.
            StringTokenizer tokenizer = new StringTokenizer((String)sourceObject);
            StringBuilder builder = new StringBuilder();
            while(tokenizer.hasMoreTokens()) {
                builder.append(tokenizer.nextToken());
            }
            byte[] bytes = Base64.base64Decode(builder.toString().getBytes());
            return bytes;
        }
        return convertObjectToByteArray(sourceObject);
    }

    protected Byte[] convertSchemaBase64ToByteObjectArray(Object sourceObject) throws ConversionException {
        byte[] bytes = convertSchemaBase64ToByteArray(sourceObject);
        Byte[] objectBytes = new Byte[bytes.length];
        for (int index = 0; index < bytes.length; index++) {
            objectBytes[index] = bytes[index];
        }
        return objectBytes;
    }

    public String buildBase64StringFromBytes(byte[] bytes) {
        byte[] convertedBytes = Base64.base64Encode(bytes);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < convertedBytes.length; i++) {
            buffer.append((char) convertedBytes[i]);
        }
        return buffer.toString();
    }

    public String buildBase64StringFromObjectBytes(Byte[] bytes) {
        byte[] primitiveBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            primitiveBytes[i] = bytes[i].byteValue();
        }
        return buildBase64StringFromBytes(primitiveBytes);
    }

    protected String buildHexStringFromObjectBytes(Byte[] bytes) {
        byte[] primitiveBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            primitiveBytes[i] = bytes[i].byteValue();
        }
        return Helper.buildHexStringFromBytes(primitiveBytes);
    }

    protected List convertStringToList(Object sourceObject) throws ConversionException {
        ArrayList list = new ArrayList();
        if (sourceObject instanceof String) {
            StringTokenizer tokenizer = new StringTokenizer((String) sourceObject, " ");
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                list.add(token);
            }
        }
        return list;
    }

    /**
     * Convert the given sourceObject (String) to the appropriate collection type specified by the
     * containerPolicy, using the elementType to properly convert each element of the list.
     *
     * @param sourceObject - will always be a string if read from XML
     * @param elementType - the type of the elements contained in the list
     * @return - the newly converted object
     */
    public Object convertStringToList(Object sourceObject, Class elementType, ContainerPolicy containerPolicy) throws ConversionException {
        Collection collection = (Collection) containerPolicy.containerInstance();

        if (sourceObject instanceof String) {
            StringTokenizer tokenizer = new StringTokenizer((String) sourceObject, " ");
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                collection.add(convertObject(token, elementType));
            }
        }

        return collection;
    }

    public String convertListToString(Object sourceObject) throws ConversionException {
        StringBuilder returnStringBuilder = new StringBuilder();
        if (sourceObject instanceof List) {
            List list = (List) sourceObject;
            for (int i = 0, listSize = list.size(); i < listSize; i++) {
                Object next = list.get(i);
                    if (i > 0) {
                        returnStringBuilder.append(' ');
                    }
                    returnStringBuilder.append(convertObjectToString(next));
            }
        }
        return returnStringBuilder.toString();
    }

    public static HashMap getDefaultXMLTypes() {
        if (defaultXMLTypes == null) {
            defaultXMLTypes = buildXMLTypes();
        }
        return defaultXMLTypes;
    }

    public static HashMap getDefaultJavaTypes() {
        if (defaultJavaTypes == null) {
            defaultJavaTypes = buildJavaTypes();
        }
        return defaultJavaTypes;
    }

    /**
    * Build and return a Hashtable containing the default XML to Java conversion pairs
    */
    private static HashMap buildXMLTypes() {
        HashMap XMLTypes = new HashMap();

        //jaxb 1.0 spec pairs
        XMLTypes.put(Constants.ANY_SIMPLE_TYPE_QNAME, CoreClassConstants.STRING);        
        XMLTypes.put(Constants.BASE_64_BINARY_QNAME, CoreClassConstants.APBYTE);
        XMLTypes.put(Constants.BOOLEAN_QNAME, CoreClassConstants.PBOOLEAN);
        XMLTypes.put(Constants.BYTE_QNAME, CoreClassConstants.PBYTE);
        XMLTypes.put(Constants.DATE_QNAME, CoreClassConstants.CALENDAR);
        XMLTypes.put(Constants.DATE_TIME_QNAME, CoreClassConstants.CALENDAR);
        XMLTypes.put(Constants.DECIMAL_QNAME, CoreClassConstants.BIGDECIMAL);
        XMLTypes.put(Constants.DOUBLE_QNAME, CoreClassConstants.PDOUBLE);
        XMLTypes.put(Constants.FLOAT_QNAME, CoreClassConstants.PFLOAT);
        XMLTypes.put(Constants.HEX_BINARY_QNAME, CoreClassConstants.APBYTE);
        XMLTypes.put(Constants.INT_QNAME, CoreClassConstants.PINT);
        XMLTypes.put(Constants.INTEGER_QNAME, CoreClassConstants.BIGINTEGER);
        XMLTypes.put(Constants.LONG_QNAME, CoreClassConstants.PLONG);
        XMLTypes.put(Constants.NAME_QNAME, CoreClassConstants.STRING);
        XMLTypes.put(Constants.NCNAME_QNAME, CoreClassConstants.STRING);        
        XMLTypes.put(Constants.QNAME_QNAME, Constants.QNAME_CLASS);
        XMLTypes.put(Constants.SHORT_QNAME, CoreClassConstants.PSHORT);
        XMLTypes.put(Constants.STRING_QNAME, CoreClassConstants.STRING);
        XMLTypes.put(Constants.TIME_QNAME, CoreClassConstants.CALENDAR);
        XMLTypes.put(Constants.UNSIGNED_BYTE_QNAME, CoreClassConstants.PSHORT);
        XMLTypes.put(Constants.UNSIGNED_INT_QNAME, CoreClassConstants.PLONG);
        XMLTypes.put(Constants.UNSIGNED_SHORT_QNAME, CoreClassConstants.PINT);        

        XMLTypes.put(Constants.DURATION_QNAME,  CoreClassConstants.DURATION);
        XMLTypes.put(Constants.G_DAY_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);
        XMLTypes.put(Constants.G_MONTH_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);
        XMLTypes.put(Constants.G_MONTH_DAY_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);
        XMLTypes.put(Constants.G_YEAR_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);
        XMLTypes.put(Constants.G_YEAR_MONTH_QNAME, CoreClassConstants.XML_GREGORIAN_CALENDAR);

        XMLTypes.put(Constants.NEGATIVE_INTEGER_QNAME, CoreClassConstants.BIGINTEGER);
        XMLTypes.put(Constants.NOTATION_QNAME, Constants.QNAME_CLASS);
        XMLTypes.put(Constants.NON_NEGATIVE_INTEGER_QNAME, CoreClassConstants.BIGINTEGER);
        XMLTypes.put(Constants.NON_POSITIVE_INTEGER_QNAME,CoreClassConstants.BIGINTEGER);
        XMLTypes.put(Constants.NORMALIZEDSTRING_QNAME, CoreClassConstants.STRING);
        XMLTypes.put(Constants.POSITIVE_INTEGER_QNAME, CoreClassConstants.BIGINTEGER);        
        XMLTypes.put(Constants.UNSIGNED_LONG_QNAME, CoreClassConstants.BIGINTEGER);

        
        return XMLTypes;
    }

    /**
     * Build and return a Hashtable containing the default Java to XML conversion pairs
     */
    private static HashMap buildJavaTypes() {
        HashMap javaTypes = new HashMap();

        //jaxb 1.0 spec pairs
        javaTypes.put(CoreClassConstants.APBYTE, Constants.HEX_BINARY_QNAME);
        javaTypes.put(CoreClassConstants.BIGDECIMAL, Constants.DECIMAL_QNAME);
        javaTypes.put(CoreClassConstants.BIGINTEGER, Constants.INTEGER_QNAME);
        javaTypes.put(CoreClassConstants.PBOOLEAN, Constants.BOOLEAN_QNAME);
        javaTypes.put(CoreClassConstants.PBYTE, Constants.BYTE_QNAME);
        javaTypes.put(CoreClassConstants.CALENDAR, Constants.DATE_TIME_QNAME);
        javaTypes.put(CoreClassConstants.PDOUBLE, Constants.DOUBLE_QNAME);
        javaTypes.put(CoreClassConstants.PFLOAT, Constants.FLOAT_QNAME);
        javaTypes.put(CoreClassConstants.PINT, Constants.INT_QNAME);
        javaTypes.put(CoreClassConstants.PLONG, Constants.LONG_QNAME);
        javaTypes.put(CoreClassConstants.PSHORT, Constants.SHORT_QNAME);
        javaTypes.put(Constants.QNAME_CLASS, Constants.QNAME_QNAME);
        javaTypes.put(CoreClassConstants.STRING, Constants.STRING_QNAME);

        //other pairs
        javaTypes.put(CoreClassConstants.ABYTE, Constants.HEX_BINARY_QNAME);
        javaTypes.put(CoreClassConstants.BOOLEAN, Constants.BOOLEAN_QNAME);
        javaTypes.put(CoreClassConstants.BYTE, Constants.BYTE_QNAME);
        javaTypes.put(CoreClassConstants.GREGORIAN_CALENDAR, Constants.DATE_TIME_QNAME);
        javaTypes.put(CoreClassConstants.DOUBLE, Constants.DOUBLE_QNAME);
        javaTypes.put(CoreClassConstants.FLOAT, Constants.FLOAT_QNAME);
        javaTypes.put(CoreClassConstants.INTEGER, Constants.INT_QNAME);
        javaTypes.put(CoreClassConstants.LONG, Constants.LONG_QNAME);
        javaTypes.put(CoreClassConstants.SHORT, Constants.SHORT_QNAME);
        javaTypes.put(CoreClassConstants.UTILDATE, Constants.DATE_TIME_QNAME);

        javaTypes.put(CoreClassConstants.CHAR, Constants.UNSIGNED_INT_QNAME);
        javaTypes.put(CoreClassConstants.PCHAR, Constants.UNSIGNED_INT_QNAME);
        javaTypes.put(CoreClassConstants.DURATION, Constants.DURATION_QNAME);
        javaTypes.put(Constants.UUID, Constants.STRING_QNAME);
        javaTypes.put(Constants.URI, Constants.STRING_QNAME);
        javaTypes.put(CoreClassConstants.URL_Class, Constants.ANY_URI_QNAME);

        return javaTypes;
    }

    private String appendTimeZone(String string, Date date) {
        if (!timeZoneQualified) {
            return string;
        }

        StringBuilder stringBuilder = new StringBuilder(string);

        // GMT Time Zone
        int rawMinuteOffset = getTimeZone().getOffset(date.getTime()) / 60000;
        if (0 == rawMinuteOffset) {
            stringBuilder.append(GMT_SUFFIX);
            return stringBuilder.toString();
        }

        // +HH:MM
        if (rawMinuteOffset < 0) {
            stringBuilder.append('-');
            rawMinuteOffset = Math.abs(rawMinuteOffset);
        } else {
            stringBuilder.append('+');
        }
        int hourOffset = rawMinuteOffset / 60;
        if (hourOffset < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(hourOffset);
        stringBuilder.append(Constants.COLON);
        int minuteOffset = rawMinuteOffset % 60;
        if (minuteOffset < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(minuteOffset);
        return stringBuilder.toString();
    }

    /**
     * INTERNAL:
     */
    public Object clone() {
        XMLConversionManager clone = (XMLConversionManager) super.clone();
        return clone;
    }

    /**
     * Convenience method that appends nanosecond values from a given
     * time to a given string.
     *
     * @param string
     * @param time
     * @return
     */
    private String appendNanos(String string, Timestamp ts) {
        StringBuilder strBldr = new StringBuilder(string);
        int nanos = ts.getNanos();
        strBldr.append(nanos==0 ? "" : '.' + Helper.buildZeroPrefixAndTruncTrailZeros(nanos, TOTAL_NS_DIGITS)).toString();
        return strBldr.toString();
    }

    /**
     * Convenience method that appends millisecond values from a given
     * time to a given string.
     *
     * @param string
     * @param time
     * @return
     */
    private String appendMillis(String string, long time) {
        StringBuilder strBldr = new StringBuilder(string);
        int msns = (int) (time % 1000);
        if (msns < 0) {
            // adjust for negative time values, i.e. before Epoch
            msns = msns + 1000;
        }
        strBldr.append(msns==0 ? "" : '.' + Helper.buildZeroPrefixAndTruncTrailZeros(msns, TOTAL_MS_DIGITS)).toString();
        return strBldr.toString();
    }

    public void setTrimGMonth(boolean value) {
        this.trimGMonth = value;
    }

    public boolean trimGMonth() {
        return this.trimGMonth;
    }

    public QName buildQNameFromString(String stringValue, AbstractUnmarshalRecord record){     
        int index = stringValue.lastIndexOf(Constants.COLON);
        if(index > -1) {
            String prefix =  stringValue.substring(0, index);
            String localName = stringValue.substring(index + 1);
            
            String namespaceURI = record.resolveNamespacePrefix(prefix);            
            return new QName(namespaceURI, localName, prefix);
        } else {
            String namespaceURI = record.resolveNamespacePrefix(Constants.EMPTY_STRING);
            if(namespaceURI == null){
                namespaceURI = record.resolveNamespacePrefix(null);
            }
            return new QName(namespaceURI, stringValue);
        }
    }

    /**
     * Replaces any CR, Tab or LF characters in the string with a single ' ' character.
     */
    public String normalizeStringValue(String value) {
        int i = 0;        
        int length = value.length();
        
        //check for the first whitespace
        while(i < length) {
            if(isWhitespace(value.charAt(i), false)) {
                break;
            }
            i++;
        }
        if(i == length) {
            return value;
        }
        
        char[] buffer = value.toCharArray();
        buffer[i] = ' ';
        i++;
        for(; i < length; i++) {
            if(isWhitespace(buffer[i], false)) {
                buffer[i] = ' ';
            }
        }
        return new String(buffer);
    }
    
    /**
     * Removes all leading and trailing whitespaces, and replaces any sequences of whitespaces
     * that occur in the string with a single ' ' character.
     */
    public String collapseStringValue(String value) {
        int length = value.length();
        
        int start = 0;
        while(start < length) {
            if(isWhitespace(value.charAt(start), true)) {
                break;
            }
            start++;
        }
        if(start == length) {
            return value;
        }
        
        StringBuffer collapsedString = new StringBuffer(length);
        if(start != 0) {
            for(int i = 0; i < start; i++) {
                collapsedString.append(value.charAt(i));
            }
            collapsedString.append(' ');
        }
        
        boolean inSequence = true;
        for(int i = start + 1; i < length; i++) {
            char nextCharacter = value.charAt(i);
            if(!isWhitespace(nextCharacter, true)) {
                collapsedString.append(nextCharacter);
                inSequence = false;
            } else {
                if(inSequence) {
                    continue;
                } else {
                    collapsedString.append(' ');
                    inSequence = true;
                }
            }
        }
        length = collapsedString.length();
        if(length > 0 && collapsedString.charAt(length -1) == ' ') {
            collapsedString.setLength(length - 1);
        }
        return collapsedString.toString();
    }
    
    private boolean isWhitespace(char character, boolean includeSpace) {
        if(character > 0x20) {
            return false;
        }
        if(character == 0x9 || character == 0xA || character == 0xD) {
            return true;
        }
        if(includeSpace) {
            return character == 0x20;
        }
        return false;
    }
    
    private boolean isNumericQName(QName schemaTypeQName){
    	if(schemaTypeQName == null){
    		return false;
    	}
    	return(schemaTypeQName.equals(Constants.BYTE_QNAME ))
  			||(schemaTypeQName.equals(Constants.DECIMAL_QNAME ))
  			||(schemaTypeQName.equals(Constants.INT_QNAME ))
  			||(schemaTypeQName.equals(Constants.INTEGER_QNAME ))
  			||(schemaTypeQName.equals(Constants.FLOAT_QNAME ))
  			||(schemaTypeQName.equals(Constants.LONG_QNAME ))
            ||(schemaTypeQName.equals(Constants.NEGATIVE_INTEGER_QNAME))        			
  			||(schemaTypeQName.equals(Constants.NON_NEGATIVE_INTEGER_QNAME))
  			||(schemaTypeQName.equals(Constants.NON_POSITIVE_INTEGER_QNAME))
  			||(schemaTypeQName.equals(Constants.POSITIVE_INTEGER_QNAME))	        			
  			||(schemaTypeQName.equals(Constants.SHORT_QNAME ))
  			||(schemaTypeQName.equals(Constants.UNSIGNED_SHORT_QNAME ))
  			||(schemaTypeQName.equals(Constants.UNSIGNED_LONG_QNAME ))
  			||(schemaTypeQName.equals(Constants.UNSIGNED_INT_QNAME ))
  			||(schemaTypeQName.equals(Constants.UNSIGNED_BYTE_QNAME ));  			
    }
}