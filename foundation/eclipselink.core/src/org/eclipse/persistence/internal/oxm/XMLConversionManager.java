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
package org.eclipse.persistence.internal.oxm;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLConversionException;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.eclipse.persistence.oxm.XMLConstants;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Primarly used to convert objects from a given XML Schema type to a different type in Java.
 * Uses a singleton instance</p>
 * @since    OracleAS TopLink 10<i>g</i>
 */

public class XMLConversionManager extends ConversionManager implements TimeZoneHolder {
    protected static final String GMT_ID = "GMT";
    protected static final String GMT_SUFFIX = "Z";

    protected static XMLConversionManager defaultXMLManager;

    // Static hashtables for the default conversion pairs
    protected static HashMap defaultXMLTypes;
    protected static HashMap defaultJavaTypes;

    // The formatters XML Schema types xsd:date, xsd:time and xsd:dateTime,
    //  xsd:gDay, xsd:gMonth, xsd:gMonthDay, xsd:gYear, xsd:gYearMonth
    protected static final String XSD_DATE_FORMAT_STR =
        "yyyy-MM-dd";
    protected DateFormatThreadLocal dateFormatter;
    protected static final String XSD_TIME_FORMAT_STR =
        "HH:mm:ss";
    protected DateFormatThreadLocal timeFormatter;
    protected static final String XSD_DATE_TIME_FORMAT_STR =
        "yyyy-MM-dd'T'HH:mm:ss";
    protected DateFormatThreadLocal dateTimeFormatter;
    protected static final String XSD_GDAY_FORMAT_STR =
        "----dd";
    protected DateFormatThreadLocal gDayFormatter;
    protected static final String XSD_GMONTH_FORMAT_STR =
        "--MM--";
    protected DateFormatThreadLocal gMonthFormatter;
    protected static final String XSD_GMONTH_DAY_FORMAT_STR =
        "--MM-dd";
    protected DateFormatThreadLocal gMonthDayFormatter;
    protected static final String XSD_GYEAR_FORMAT_STR =
        "yyyy";
    protected DateFormatThreadLocal gYearFormatter;
    protected static final String XSD_GYEAR_MONTH_FORMAT_STR =
        "yyyy-MM";
    protected DateFormatThreadLocal gYearMonthFormatter;

    protected boolean timeZoneQualified;
    protected TimeZone timeZone;

    public XMLConversionManager() {
        super();
        buildFormatters(this);
      	timeZoneQualified = false;
    }

    protected static void buildFormatters(XMLConversionManager xmlConversionManager) {
      xmlConversionManager.dateFormatter =
        new DateFormatThreadLocal(XSD_DATE_FORMAT_STR, null);
      xmlConversionManager.timeFormatter =
        new DateFormatThreadLocal(XSD_TIME_FORMAT_STR, xmlConversionManager);
      xmlConversionManager.dateTimeFormatter =
        new DateFormatThreadLocal(XSD_DATE_TIME_FORMAT_STR, xmlConversionManager);
      xmlConversionManager.gDayFormatter =
        new DateFormatThreadLocal(XSD_GDAY_FORMAT_STR, null);
      xmlConversionManager.gMonthFormatter =
        new DateFormatThreadLocal(XSD_GMONTH_FORMAT_STR, null);
      xmlConversionManager.gMonthDayFormatter =
        new DateFormatThreadLocal(XSD_GMONTH_DAY_FORMAT_STR, null);
      xmlConversionManager.gYearFormatter =
        new DateFormatThreadLocal(XSD_GYEAR_FORMAT_STR, null);
      xmlConversionManager.gYearMonthFormatter =
        new DateFormatThreadLocal(XSD_GYEAR_MONTH_FORMAT_STR, null);
    }
    public static XMLConversionManager getDefaultXMLManager() {
        if (defaultXMLManager == null) {
            defaultXMLManager = new XMLConversionManager();
            //defaultXMLManager.setShouldUseClassLoaderFromCurrentThread(true);
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
      }
      else {
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
        } else if ((javaClass == XMLConstants.QNAME_CLASS) && (sourceObject != null)) {
            return convertObjectToQName(sourceObject);
        } else if ((javaClass == ClassConstants.List_Class) && (sourceObject instanceof String)) {
            return convertStringToList(sourceObject);
        } else if ((javaClass == ClassConstants.STRING) && (sourceObject instanceof List)) {
            return convertListToString(sourceObject);
        } else if ((javaClass == ClassConstants.CALENDAR)) {
            return convertObjectToCalendar(sourceObject);
        } else if ((javaClass == ClassConstants.UTILDATE)) {
            return convertObjectToUtilDate(sourceObject, XMLConstants.DATE_TIME_QNAME);
        } else if ((javaClass == ClassConstants.SQLDATE)) {
            return convertObjectToSQLDate(sourceObject, XMLConstants.DATE_QNAME);
        } else if ((javaClass == ClassConstants.TIME)) {
            return convertObjectToSQLTime(sourceObject, XMLConstants.TIME_QNAME);
        } else if ((javaClass == ClassConstants.TIMESTAMP)) {
            return convertObjectToTimestamp(sourceObject, XMLConstants.DATE_TIME_QNAME);
        } else if ((javaClass == java.net.URI.class)) {
            return convertObjectToURI(sourceObject);
        } else {
            try {
                return super.convertObject(sourceObject, javaClass);
            } catch (ConversionException ex) {
                if (sourceObject.getClass() == ClassConstants.STRING) {
                    return super.convertObject(((String)sourceObject).trim(), javaClass);
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
        } else if ((javaClass == ClassConstants.CALENDAR) || (javaClass == ClassConstants.GREGORIAN_CALENDAR)) {
            return convertObjectToCalendar(sourceObject, schemaTypeQName);
        } else if (javaClass == ClassConstants.ABYTE) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(XMLConstants.HEX_BINARY)) {
                return super.convertObjectToByteObjectArray(sourceObject);
            } else if (schemaTypeQName.getLocalPart().equalsIgnoreCase(XMLConstants.BASE_64_BINARY)) {
                return convertSchemaBase64ToByteObjectArray(sourceObject);
            }
        } else if (javaClass == ClassConstants.APBYTE) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(XMLConstants.HEX_BINARY)) {
                return super.convertObjectToByteArray(sourceObject);
            } else if (schemaTypeQName.getLocalPart().equalsIgnoreCase(XMLConstants.BASE_64_BINARY)) {
                return convertSchemaBase64ToByteArray(sourceObject);
            }
        } else if ((javaClass == ClassConstants.List_Class) && (sourceObject instanceof String)) {
            return convertStringToList(sourceObject);
        } else if ((javaClass == ClassConstants.STRING) && (sourceObject instanceof List)) {
            return convertListToString(sourceObject);
        } else if (sourceObject instanceof byte[]) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(XMLConstants.BASE_64_BINARY)) {
                return buildBase64StringFromBytes((byte[])sourceObject);
            }
            return Helper.buildHexStringFromBytes((byte[])sourceObject);
        } else if (sourceObject instanceof Byte[]) {
            if (schemaTypeQName.getLocalPart().equalsIgnoreCase(XMLConstants.BASE_64_BINARY)) {
                return buildBase64StringFromObjectBytes((Byte[])sourceObject);
            }
            return buildHexStringFromObjectBytes((Byte[])sourceObject);
        } else if ((javaClass == ClassConstants.UTILDATE)) {
            return convertObjectToUtilDate(sourceObject, schemaTypeQName);
        } else if (javaClass == ClassConstants.SQLDATE) {
            return convertObjectToSQLDate(sourceObject, schemaTypeQName);
        } else if (javaClass == ClassConstants.TIME) {
            return convertObjectToSQLTime(sourceObject, schemaTypeQName);
        } else if (javaClass == ClassConstants.TIMESTAMP) {
            return convertObjectToTimestamp(sourceObject, schemaTypeQName);
        } else if ((javaClass == XMLConstants.QNAME_CLASS) && (sourceObject != null)) {
            return convertObjectToQName(sourceObject);
        } else if (javaClass == ClassConstants.STRING) {
            return convertObjectToString(sourceObject, schemaTypeQName);
        } else if ((javaClass == java.net.URI.class)) {
            return convertObjectToURI(sourceObject);
        } else {
            try {
                return super.convertObject(sourceObject, javaClass);
            } catch (ConversionException ex) {
                if (sourceObject.getClass() == ClassConstants.STRING) {
                    return super.convertObject(((String)sourceObject).trim(), javaClass);
                }
                throw ex;
            }
        }

        throw ConversionException.couldNotBeConverted(sourceObject, javaClass);
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
            return (QName)sourceObject;
        }

        if (sourceObject instanceof String) {
            return qnameFromString((String)sourceObject);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, XMLConstants.QNAME_CLASS);
    }

    /**
     * INTERNAL:
     * Converts given object to a Calendar object
     */
    protected Calendar convertObjectToCalendar(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof String) {
            String sourceString = (String)sourceObject;
            if (sourceString.lastIndexOf('T') != -1) {
                return convertStringToCalendar((String)sourceObject, XMLConstants.DATE_TIME_QNAME);
            } else {
                if (sourceString.lastIndexOf(':') != -1) {
                    return convertStringToCalendar((String)sourceObject, XMLConstants.TIME_QNAME);
                } else {
                    return convertStringToCalendar((String)sourceObject, XMLConstants.DATE_QNAME);
                }
            }
        }
        return super.convertObjectToCalendar(sourceObject);
    }

    /**
     * INTERNAL:
     * Converts objects to thier string representations.
     */
    protected String convertObjectToString(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof Calendar) {
            return stringFromCalendar((Calendar)sourceObject);
        }

        if (sourceObject instanceof QName) {
            return stringFromQName((QName)sourceObject);
        }
        if (sourceObject instanceof java.sql.Date) {
            return stringFromSQLDate((java.sql.Date)sourceObject);
        }
        if (sourceObject instanceof java.sql.Time) {
            return stringFromSQLTime((java.sql.Time)sourceObject);
        }
        if (sourceObject instanceof java.sql.Timestamp) {
            return stringFromTimestamp((Timestamp)sourceObject);
        }
        if (sourceObject instanceof java.util.Date) {
            return stringFromDate((java.util.Date)sourceObject);
        }

        /*
                if (sourceObject instanceof byte[]) {
                    return buildBase64StringFromBytes((byte[]) sourceObject);
                }

                if (sourceObject instanceof Byte[]) {
                    return buildBase64StringFromObjectBytes((Byte[]) sourceObject);
                }
        */
        return super.convertObjectToString(sourceObject);
    }

    protected String convertObjectToString(Object sourceObject, QName schemaTypeQName) throws ConversionException {
        if (sourceObject instanceof Calendar) {
            return stringFromCalendar((Calendar)sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof QName) {
            return stringFromQName((QName)sourceObject);
        }
        if (sourceObject instanceof java.sql.Date) {
            return stringFromSQLDate((java.sql.Date)sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof java.sql.Time) {
            return stringFromSQLTime((java.sql.Time)sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof java.sql.Timestamp) {
            return stringFromTimestamp((Timestamp)sourceObject, schemaTypeQName);
        }
        if (sourceObject instanceof java.util.Date) {
            return stringFromDate((java.util.Date)sourceObject, schemaTypeQName);
        }

        return super.convertObjectToString(sourceObject);

    }

    private Calendar convertObjectToCalendar(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            return convertStringToCalendar((String) sourceObject, schemaTypeQName);
        }
        return super.convertObjectToCalendar(sourceObject);
    }

    public Calendar convertStringToCalendar(String sourceString, QName schemaTypeQName) {
    	java.util.Date date = convertStringToDate(sourceString, schemaTypeQName);
    	applyTimeZone(date, sourceString);
    	return Helper.calendarFromUtilDate(date);
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
        return super.convertObjectToDate(sourceObject);
    }

    protected Time convertObjectToSQLTime(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            Date date = convertStringToDate((String) sourceObject, schemaTypeQName);
            return new java.sql.Time((date.getTime() / 1000) * 1000);
        }
        return super.convertObjectToTime(sourceObject);
    }

    protected Timestamp convertStringToTimestamp(String sourceObject) {
    	return convertStringToTimestamp(sourceObject, XMLConstants.DATE_TIME_QNAME);
    }

    protected Timestamp convertObjectToTimestamp(Object sourceObject, QName schemaTypeQName) {
        if (sourceObject instanceof String) {
            return convertStringToTimestamp((String)sourceObject, schemaTypeQName);
        }
        return super.convertObjectToTimestamp(sourceObject);
    }

    public java.sql.Timestamp convertStringToTimestamp(String sourceString, QName schemaType) {
    	Date date;
    	if(XMLConstants.DATE_QNAME.equals(schemaType)) {
    		try {
    			date = dateFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw ConversionException.incorrectDateFormat(sourceString);
    		}
    	} else if(XMLConstants.TIME_QNAME.equals(schemaType)) {
    		try {
    			return convertStringToTimestamp(sourceString, timeFormatter.get());
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectTimestampTimeFormat(sourceString);
    		} catch(NumberFormatException e) {
    			throw XMLConversionException.incorrectTimestampTimeFormat(sourceString);
    		}
    	} else if(XMLConstants.G_DAY_QNAME.equals(schemaType)) {
    		try {
    			date = gDayFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGDayFormat(sourceString);
    		}
    	} else if(XMLConstants.G_MONTH_QNAME.equals(schemaType)) {
    		try {
    			date = gMonthFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGMonthFormat(sourceString);
    		}
    	} else if(XMLConstants.G_MONTH_DAY_QNAME.equals(schemaType)) {
    		try {
    			return Helper.timestampFromDate(gMonthDayFormatter.get().parse(sourceString));
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGMonthDayFormat(sourceString);
    		}
    	} else if(XMLConstants.G_YEAR_QNAME.equals(schemaType)) {
    		try {
    			date = gYearFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGYearFormat(sourceString);
    		}
    	} else if(XMLConstants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
    		try {
    			date = gYearMonthFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGYearMonthFormat(sourceString);
    		}
    	} else if(XMLConstants.DURATION_QNAME.equals(schemaType)) {
    		throw new IllegalArgumentException();
    	} else {
    		try {
    			return convertStringToTimestamp(sourceString, dateTimeFormatter.get());
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectTimestampDateTimeFormat(sourceString);
    		} catch(NumberFormatException e) {
    			throw XMLConversionException.incorrectTimestampDateTimeFormat(sourceString);
    		}
    	}
		applyTimeZone(date, sourceString);
		return Helper.timestampFromDate(date);
    }

    private Timestamp convertStringToTimestamp(String string, DateFormat dateFormat) throws ParseException {
    	int decimalIndex = string.lastIndexOf('.');
    	if(-1 == decimalIndex) {
    		Date date = dateFormat.parse(string);
    		return Helper.timestampFromDate(date);
    	}
    	Date date = dateFormat.parse(string.substring(0, decimalIndex));
		applyTimeZone(date, string);
    	Timestamp timestamp = Helper.timestampFromDate(date);

    	int timeZoneIndex = string.lastIndexOf(GMT_SUFFIX);
    	if(-1 == timeZoneIndex) {
    		timeZoneIndex = string.lastIndexOf('-');
    		if(timeZoneIndex < decimalIndex) {
    			timeZoneIndex = -1;
    		}
    		if(-1 == timeZoneIndex) {
        		timeZoneIndex = string.lastIndexOf('+');
    		}
    	}
    	String nsString;
    	if(-1 == timeZoneIndex) {
    		nsString = string.substring(decimalIndex + 1);
    	} else {
    		nsString = string.substring((decimalIndex + 1), timeZoneIndex);
    	}
    	double ns = Long.valueOf(nsString).doubleValue();
    	ns = ns * Math.pow(10, 9 - nsString.length());
    	timestamp.setNanos((int) ns);
    	return timestamp;
    }

    public String stringFromCalendar(Calendar sourceCalendar, QName schemaTypeQName) {
        return stringFromDate(sourceCalendar.getTime(), schemaTypeQName);
    }

    private String stringFromCalendar(Calendar sourceCalendar) {
        if (!(sourceCalendar.isSet(Calendar.HOUR) || sourceCalendar.isSet(Calendar.MINUTE) || sourceCalendar.isSet(Calendar.SECOND) || sourceCalendar.isSet(Calendar.MILLISECOND))) {
        	return dateFormatter.get().format(sourceCalendar.getTime());
        } else if (!(sourceCalendar.isSet(Calendar.YEAR) || sourceCalendar.isSet(Calendar.MONTH) || sourceCalendar.isSet(Calendar.DATE))) {
            String string = timeFormatter.get().format(sourceCalendar.getTime());
            int ms = (int)(sourceCalendar.getTimeInMillis() % 1000);
            if (0 == ms) {
                string += ".0";
            } else {
                string += ('.' + Helper.buildZeroPrefixAndTruncTrailZeros(ms, 3));
            }
            return appendTimeZone(string);
        } else {
            return stringFromDate(sourceCalendar.getTime());
        }
    }

    private String stringFromDate(java.util.Date sourceDate) {
    	String string = dateTimeFormatter.get().format(sourceDate);
		int ms = (int) (sourceDate.getTime() % 1000);
		if(0 == ms) {
			string += ".0";
		} else {
			string += ('.' + Helper.buildZeroPrefixAndTruncTrailZeros(ms, 3));
		}
        return appendTimeZone(string);
    }

    public java.util.Date convertStringToDate(String sourceString, QName schemaType) {
    	Date date;
    	if(XMLConstants.DATE_QNAME.equals(schemaType)) {
    		try {
    			date = dateFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw ConversionException.incorrectDateFormat(sourceString);
    		}
    	} else if(XMLConstants.TIME_QNAME.equals(schemaType)) {
    		try {
    			return convertStringToDate(sourceString, timeFormatter.get());
    		} catch(ParseException e) {
    			throw ConversionException.incorrectTimeFormat(sourceString);
    		} catch(NumberFormatException e) {
    			throw ConversionException.incorrectTimeFormat(sourceString);
    		}
    	} else if(XMLConstants.G_DAY_QNAME.equals(schemaType)) {
    		try {
    			date = gDayFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGDayFormat(sourceString);
    		}
    	} else if(XMLConstants.G_MONTH_QNAME.equals(schemaType)) {
    		try {
    			date = gMonthFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGMonthFormat(sourceString);
    		}
    	} else if(XMLConstants.G_MONTH_DAY_QNAME.equals(schemaType)) {
    		try {
    			date = gMonthDayFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGMonthDayFormat(sourceString);
    		}
    	} else if(XMLConstants.G_YEAR_QNAME.equals(schemaType)) {
    		try {
    			date = gYearFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGYearFormat(sourceString);
    		}
    	} else if(XMLConstants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
    		try {
    			date = gYearMonthFormatter.get().parse(sourceString);
    		} catch(ParseException e) {
    			throw XMLConversionException.incorrectGYearMonthFormat(sourceString);
    		}
    	} else if(XMLConstants.DURATION_QNAME.equals(schemaType)) {
    		throw new IllegalArgumentException();
    	} else {
    		try {
    			return convertStringToDate(sourceString, dateTimeFormatter.get());
    		} catch(ParseException e) {
    			throw ConversionException.incorrectDateTimeFormat(sourceString);
    		} catch(NumberFormatException e) {
    			throw ConversionException.incorrectDateTimeFormat(sourceString);
    		}
    	}
		applyTimeZone(date, sourceString);
		return date;
    }

    private java.util.Date convertStringToDate(String string, DateFormat dateFormat) throws ParseException {
    	Date date;
    	int decimalIndex = string.lastIndexOf('.');
    	if(-1 == decimalIndex) {
    		date = dateFormat.parse(string);
    		applyTimeZone(date, string);
    		return date;
    	}
    	date = dateFormat.parse(string.substring(0, decimalIndex));

    	int timeZoneIndex = string.lastIndexOf(GMT_SUFFIX);
    	if(-1 == timeZoneIndex) {
    		timeZoneIndex = string.lastIndexOf('-');
    		if(timeZoneIndex < decimalIndex) {
    			timeZoneIndex = -1;
    		}
    		if(-1 == timeZoneIndex) {
        		timeZoneIndex = string.lastIndexOf('+');
    		}
    	}
    	String msString;
    	if(-1 == timeZoneIndex) {
    		msString = string.substring(decimalIndex + 1);
    	} else {
    		msString = string.substring((decimalIndex + 1), timeZoneIndex);
    	}
     	double ms = Long.valueOf(msString).doubleValue();
    	ms = ms * Math.pow(10, 3 - msString.length());
    	date.setTime(date.getTime() + (long) ms);
		applyTimeZone(date, string);
    	return date;
    }

    public String stringFromDate(java.util.Date sourceDate, QName schemaType) {
    	if(XMLConstants.DATE_QNAME.equals(schemaType)) {
    		return dateFormatter.get().format(sourceDate);
    	} else if(XMLConstants.TIME_QNAME.equals(schemaType)) {
    		String string = timeFormatter.get().format(sourceDate);
    		int ms = (int) (sourceDate.getTime() % 1000);
    		if(0 == ms) {
    			string += ".0";
    		} else {
    			string += ('.' + Helper.buildZeroPrefixAndTruncTrailZeros(ms, 3));
    		}
            return appendTimeZone(string);
    	} else if(XMLConstants.G_DAY_QNAME.equals(schemaType)) {
    		return gDayFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_MONTH_QNAME.equals(schemaType)) {
    		return gMonthFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_MONTH_DAY_QNAME.equals(schemaType)) {
    		return gMonthDayFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_YEAR_QNAME.equals(schemaType)) {
    		return gYearFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
    		return gYearMonthFormatter.get().format(sourceDate);
    	} else if(XMLConstants.DURATION_QNAME.equals(schemaType)) {
    		throw new IllegalArgumentException();
    	} else {
    		return stringFromDate(sourceDate);
    	}
    }

    private String stringFromSQLDate(java.sql.Date sourceDate) {
         return dateFormatter.get().format(sourceDate);
    }

    private String stringFromSQLDate(java.sql.Date sourceDate, QName schemaType) {
    	if(XMLConstants.DATE_TIME_QNAME.equals(schemaType)) {
    		return dateTimeFormatter.get().format(sourceDate);
    	} else if(XMLConstants.TIME_QNAME.equals(schemaType)) {
    		String string = timeFormatter.get().format(sourceDate);
            return appendTimeZone(string);
    	} else if(XMLConstants.G_DAY_QNAME.equals(schemaType)) {
    		return gDayFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_MONTH_QNAME.equals(schemaType)) {
    		return gMonthFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_MONTH_DAY_QNAME.equals(schemaType)) {
    		return gMonthDayFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_YEAR_QNAME.equals(schemaType)) {
    		return gYearFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
    		return gYearMonthFormatter.get().format(sourceDate);
    	} else if(XMLConstants.DURATION_QNAME.equals(schemaType)) {
    		throw new IllegalArgumentException();
    	} else {
    		return stringFromSQLDate(sourceDate);
    	}
    }

    private String stringFromSQLTime(Time sourceTime) {
        String string = timeFormatter.get().format(sourceTime);
        return appendTimeZone(string);
    }

    private String stringFromSQLTime(Time sourceTime, QName schemaType) {
    	if(XMLConstants.DATE_TIME_QNAME.equals(schemaType)) {
    		String string = dateTimeFormatter.get().format(sourceTime);
            return appendTimeZone(string);
    	} else if(XMLConstants.DATE_QNAME.equals(schemaType)) {
    		return dateFormatter.get().format(sourceTime);
    	} else if(XMLConstants.G_DAY_QNAME.equals(schemaType)) {
    		return gDayFormatter.get().format(sourceTime);
    	} else if(XMLConstants.G_MONTH_QNAME.equals(schemaType)) {
    		return gMonthFormatter.get().format(sourceTime);
    	} else if(XMLConstants.G_MONTH_DAY_QNAME.equals(schemaType)) {
    		return gMonthDayFormatter.get().format(sourceTime);
    	} else if(XMLConstants.G_YEAR_QNAME.equals(schemaType)) {
    		return gYearFormatter.get().format(sourceTime);
    	} else if(XMLConstants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
    		return gYearMonthFormatter.get().format(sourceTime);
    	} else if(XMLConstants.DURATION_QNAME.equals(schemaType)) {
    		throw new IllegalArgumentException();
    	} else {
    		return stringFromSQLTime(sourceTime);
    	}
     }

    private String stringFromTimestamp(Timestamp sourceDate) {
        String string = dateTimeFormatter.get().format(sourceDate);
		int ns = sourceDate.getNanos();
		if(0 == ns) {
			string += ".0";
		} else {
			string += ('.' + Helper.buildZeroPrefixAndTruncTrailZeros(ns, 9));
		}
        return appendTimeZone(string);
    }

    private String stringFromTimestamp(Timestamp sourceDate, QName schemaType) {
        if (XMLConstants.DATE_TIME_QNAME.equals(schemaType)) {
            String string = dateTimeFormatter.get().format(sourceDate);
            int ns = sourceDate.getNanos();
            if(0 == ns) {
                string += ".0";
            } else {
                string += ('.' + Helper.buildZeroPrefixAndTruncTrailZeros(ns, 9));
            }
            return appendTimeZone(string);
        } else if(XMLConstants.DATE_QNAME.equals(schemaType)) {
    		return dateFormatter.get().format(sourceDate);
    	} else if(XMLConstants.TIME_QNAME.equals(schemaType)) {
    		String string = timeFormatter.get().format(sourceDate);
    		int ns = sourceDate.getNanos();
    		if(0 == ns) {
    			string += ".0";
    		} else {
    			string += ('.' + Helper.buildZeroPrefixAndTruncTrailZeros(ns, 9));
    		}
            return appendTimeZone(string);
    	} else if(XMLConstants.G_DAY_QNAME.equals(schemaType)) {
    		return gDayFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_MONTH_QNAME.equals(schemaType)) {
    		return gMonthFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_MONTH_DAY_QNAME.equals(schemaType)) {
    		return gMonthDayFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_YEAR_QNAME.equals(schemaType)) {
    		return gYearFormatter.get().format(sourceDate);
    	} else if(XMLConstants.G_YEAR_MONTH_QNAME.equals(schemaType)) {
    		return gYearMonthFormatter.get().format(sourceDate);
    	} else if(XMLConstants.DURATION_QNAME.equals(schemaType)) {
    		throw new IllegalArgumentException();
    	} else {
    		return stringFromTimestamp(sourceDate);
    	}
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
        if (!(sourceObject instanceof String)) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.APBYTE);
        }
        byte[] bytes = Base64.base64Decode(((String)sourceObject).getBytes());
        return bytes;
    }

    protected Byte[] convertSchemaBase64ToByteObjectArray(Object sourceObject) throws ConversionException {
        byte[] bytes = convertSchemaBase64ToByteArray(sourceObject);
        Byte[] objectBytes = new Byte[bytes.length];
        for (int index = 0; index < bytes.length; index++) {
            objectBytes[index] = new Byte(bytes[index]);
        }
        return objectBytes;
    }

    public String buildBase64StringFromBytes(byte[] bytes) {
        byte[] convertedBytes = Base64.base64Encode(bytes);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < convertedBytes.length; i++) {
            buffer.append((char)convertedBytes[i]);
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
            StringTokenizer tokenizer = new StringTokenizer((String)sourceObject, " ");
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                list.add(token);
            }
        }
        return list;
    }

    protected String convertListToString(Object sourceObject) throws ConversionException {
        String returnString = new String();
        if (sourceObject instanceof List) {
            List list = (List)sourceObject;
            for (int i = 0; i < list.size(); i++) {
                Object next = list.get(i);
                if (next instanceof String) {
                    if (i > 0) {
                        returnString += " ";
                    }
                    returnString = returnString + next;
                }
            }
        }
        return returnString;
    }

    private TimeZone convertStringToTimeZone(String string) {
    	if(GMT_SUFFIX.equals(string)) {
    		return TimeZone.getTimeZone(GMT_ID);
    	} else{
    		return TimeZone.getTimeZone(GMT_ID + string);
    	}
    }

    public HashMap getDefaultXMLTypes() {
        if (defaultXMLTypes == null) {
            defaultXMLTypes = buildXMLTypes();
        }
        return defaultXMLTypes;
    }

    public HashMap getDefaultJavaTypes() {
        if (defaultJavaTypes == null) {
            defaultJavaTypes = buildJavaTypes();
        }
        return defaultJavaTypes;
    }

    /**
    * Build and return a Hashtable containing the default XML to Java conversion pairs
    */
    private HashMap buildXMLTypes() {
        HashMap XMLTypes = new HashMap();

        //jaxb 1.0 spec pairs
        XMLTypes.put(XMLConstants.BASE_64_BINARY_QNAME, ClassConstants.APBYTE);
        XMLTypes.put(XMLConstants.BOOLEAN_QNAME, ClassConstants.PBOOLEAN);
        XMLTypes.put(XMLConstants.BYTE_QNAME, ClassConstants.PBYTE);
        XMLTypes.put(XMLConstants.DATE_QNAME, ClassConstants.CALENDAR);
        XMLTypes.put(XMLConstants.DATE_TIME_QNAME, ClassConstants.CALENDAR);
        XMLTypes.put(XMLConstants.DECIMAL_QNAME, ClassConstants.BIGDECIMAL);
        XMLTypes.put(XMLConstants.DOUBLE_QNAME, ClassConstants.PDOUBLE);
        XMLTypes.put(XMLConstants.FLOAT_QNAME, ClassConstants.PFLOAT);
        XMLTypes.put(XMLConstants.HEX_BINARY_QNAME, ClassConstants.APBYTE);
        XMLTypes.put(XMLConstants.INT_QNAME, ClassConstants.PINT);
        XMLTypes.put(XMLConstants.INTEGER_QNAME, ClassConstants.BIGINTEGER);
        XMLTypes.put(XMLConstants.LONG_QNAME, ClassConstants.PLONG);
        XMLTypes.put(XMLConstants.QNAME_QNAME, XMLConstants.QNAME_CLASS);
        XMLTypes.put(XMLConstants.SHORT_QNAME, ClassConstants.PSHORT);
        XMLTypes.put(XMLConstants.STRING_QNAME, ClassConstants.STRING);
        XMLTypes.put(XMLConstants.TIME_QNAME, ClassConstants.CALENDAR);
        XMLTypes.put(XMLConstants.UNSIGNED_BYTE_QNAME, ClassConstants.PSHORT);
        XMLTypes.put(XMLConstants.UNSIGNED_INT_QNAME, ClassConstants.PLONG);
        XMLTypes.put(XMLConstants.UNSIGNED_SHORT_QNAME, ClassConstants.PINT);
        XMLTypes.put(XMLConstants.ANY_SIMPLE_TYPE_QNAME, ClassConstants.STRING);

        return XMLTypes;
    }

    /**
     * Build and return a Hashtable containing the default Java to XML conversion pairs
     */
    private HashMap buildJavaTypes() {
        HashMap javaTypes = new HashMap();

        //jaxb 1.0 spec pairs
        javaTypes.put(ClassConstants.APBYTE, XMLConstants.HEX_BINARY_QNAME);
        javaTypes.put(ClassConstants.BIGDECIMAL, XMLConstants.DECIMAL_QNAME);
        javaTypes.put(ClassConstants.BIGINTEGER, XMLConstants.INTEGER_QNAME);
        javaTypes.put(ClassConstants.PBOOLEAN, XMLConstants.BOOLEAN_QNAME);
        javaTypes.put(ClassConstants.PBYTE, XMLConstants.BYTE_QNAME);
        javaTypes.put(ClassConstants.CALENDAR, XMLConstants.DATE_TIME_QNAME);
        javaTypes.put(ClassConstants.PDOUBLE, XMLConstants.DOUBLE_QNAME);
        javaTypes.put(ClassConstants.PFLOAT, XMLConstants.FLOAT_QNAME);
        javaTypes.put(ClassConstants.PINT, XMLConstants.INT_QNAME);
        javaTypes.put(ClassConstants.PLONG, XMLConstants.LONG_QNAME);
        javaTypes.put(ClassConstants.PSHORT, XMLConstants.SHORT_QNAME);
        javaTypes.put(XMLConstants.QNAME_CLASS, XMLConstants.QNAME_QNAME);
        javaTypes.put(ClassConstants.STRING, XMLConstants.STRING_QNAME);

        //other pairs
        javaTypes.put(ClassConstants.ABYTE, XMLConstants.HEX_BINARY_QNAME);
        javaTypes.put(ClassConstants.BOOLEAN, XMLConstants.BOOLEAN_QNAME);
        javaTypes.put(ClassConstants.BYTE, XMLConstants.BYTE_QNAME);
        javaTypes.put(ClassConstants.GREGORIAN_CALENDAR, XMLConstants.DATE_TIME_QNAME);
        javaTypes.put(ClassConstants.DOUBLE, XMLConstants.DOUBLE_QNAME);
        javaTypes.put(ClassConstants.FLOAT, XMLConstants.FLOAT_QNAME);
        javaTypes.put(ClassConstants.INTEGER, XMLConstants.INT_QNAME);
        javaTypes.put(ClassConstants.LONG, XMLConstants.LONG_QNAME);
        javaTypes.put(ClassConstants.SHORT, XMLConstants.SHORT_QNAME);

        return javaTypes;
    }

    private String appendTimeZone(String string) {
    	if(!timeZoneQualified){
    		return string;
    	}

    	StringBuilder stringBuilder = new StringBuilder(string);

    	// GMT Time Zone
    	int rawMinuteOffset = getTimeZone().getRawOffset() / 60000;
    	if(0 == rawMinuteOffset) {
    		stringBuilder.append(GMT_SUFFIX);
    		return stringBuilder.toString();
    	}

    	// +HH:MM
    	if(rawMinuteOffset < 0) {
    		stringBuilder.append('-');
    		rawMinuteOffset = Math.abs(rawMinuteOffset);
    	} else {
    		stringBuilder.append('+');
    	}
    	int hourOffset = rawMinuteOffset / 60;
    	if(hourOffset < 10) {
    		stringBuilder.append('0');
    	}
    	stringBuilder.append(hourOffset);
    	stringBuilder.append(':');
    	int minuteOffset = rawMinuteOffset % 60;
    	if(minuteOffset < 10) {
    		stringBuilder.append('0');
    	}
    	stringBuilder.append(minuteOffset);
    	return stringBuilder.toString();
    }

    private void applyTimeZone(java.util.Date date, String string) {
    	if(null == string) {
    		return;
    	}

    	int stringTimeZoneRawOffset;
    	if(string.charAt(string.length() -1) == 'Z') {
    		stringTimeZoneRawOffset = 0;
    	} else {
    		int timeZoneIndex = string.lastIndexOf('+');
    		if(-1 == timeZoneIndex) {
    			timeZoneIndex = string.lastIndexOf('-');
    			if(-1 == timeZoneIndex) {
    				return;
    			} else {
    	    		int lastColonIndex = string.lastIndexOf(':');
    	    		if((lastColonIndex - timeZoneIndex)  != 3) {
    	    			return;
    	    		}
    			}
    		}
        	TimeZone stringTimeZone = convertStringToTimeZone(string.substring(timeZoneIndex));
        	stringTimeZoneRawOffset = stringTimeZone.getRawOffset();
    	}

    	int formatTimeZoneRawOffset = getTimeZone().getRawOffset();
    	int timeZoneAdjustment = formatTimeZoneRawOffset - stringTimeZoneRawOffset;
    	date.setTime(date.getTime() + timeZoneAdjustment);
    }

    /**
     * INTERNAL:
     */
    public Object clone() {
      	XMLConversionManager clone = (XMLConversionManager)super.clone();
        buildFormatters(clone);
        return clone;
    }

}
