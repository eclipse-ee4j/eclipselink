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
package org.eclipse.persistence.internal.helper;

import java.math.*;
import java.net.URL;
import java.util.*;
import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.sql.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.core.helper.CoreConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.security.PrivilegedGetContextClassLoader;

/**
 * <p>
 * <b>Purpose</b>: Contains the conversion routines for some common classes in the system.
 * Primarily used to convert objects from a given database type to a different type in Java.
 * Uses a singleton instance, this is also used from the platform.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Execute the appropriate conversion routine.
 *    </ul>
 */
public class ConversionManager extends CoreConversionManager implements Serializable, Cloneable {
    protected Map defaultNullValues;

    /**
     * This flag is here if the Conversion Manager should use the class loader on the
     * thread when loading classes.
     */
    protected boolean shouldUseClassLoaderFromCurrentThread = false;
    protected static ConversionManager defaultManager;

    /** Allows the setting of a global default if no instance-level loader is set. */
    private static ClassLoader defaultLoader;
    protected ClassLoader loader;

    /** Store the list of Classes that can be converted to from the key. */
    protected Hashtable dataTypesConvertedFromAClass;

    /** Store the list of Classes that can be converted from to the key. */
    protected Hashtable dataTypesConvertedToAClass;

    public ConversionManager() {
        this.dataTypesConvertedFromAClass = new Hashtable();
        this.dataTypesConvertedToAClass = new Hashtable();
    }

    /**
     * INTERNAL:
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            return null;
        }
    }

    /**
     * Convert the object to the appropriate type by invoking the appropriate
     * ConversionManager method
     * @param object - the object that must be converted
     * @param javaClass - the class that the object must be converted to
     * @exception - ConversionException, all exceptions will be thrown as this type.
     * @return - the newly converted object
     */
    @Override
    public Object convertObject(Object sourceObject, Class javaClass) throws ConversionException {
        if (sourceObject == null) {
            // Check for default null conversion.
            // i.e. allow for null to be defaulted to "", or 0 etc.
            if (javaClass != null ) {
                return getDefaultNullValue(javaClass);
            } else {
                return null;
            }
        }

        if ((sourceObject.getClass() == javaClass) || (javaClass == null) || (javaClass == ClassConstants.OBJECT) || (javaClass == ClassConstants.BLOB) || (javaClass == ClassConstants.CLOB)) {
            return sourceObject;
        }

        try {
            if (javaClass == ClassConstants.STRING) {
                return convertObjectToString(sourceObject);
            } else if (javaClass == ClassConstants.UTILDATE) {
                return convertObjectToUtilDate(sourceObject);
            } else if (javaClass == ClassConstants.SQLDATE) {
                return convertObjectToDate(sourceObject);
            } else if (javaClass == ClassConstants.TIME) {
                return convertObjectToTime(sourceObject);
            } else if (javaClass == ClassConstants.TIMESTAMP) {
                return convertObjectToTimestamp(sourceObject);
            } else if ((javaClass == ClassConstants.CALENDAR) || (javaClass == ClassConstants.GREGORIAN_CALENDAR)) {
                return convertObjectToCalendar(sourceObject);
            } else if ((javaClass == ClassConstants.CHAR) || (javaClass == ClassConstants.PCHAR && !(sourceObject instanceof Character))) {
                return convertObjectToChar(sourceObject);
            } else if ((javaClass == ClassConstants.INTEGER) || (javaClass == ClassConstants.PINT && !(sourceObject instanceof Integer))) {
                return convertObjectToInteger(sourceObject);
            } else if ((javaClass == ClassConstants.DOUBLE) || (javaClass == ClassConstants.PDOUBLE && !(sourceObject instanceof Double))) {
                return convertObjectToDouble(sourceObject);
            } else if ((javaClass == ClassConstants.FLOAT) || (javaClass == ClassConstants.PFLOAT && !(sourceObject instanceof Float))) {
                return convertObjectToFloat(sourceObject);
        	} else if ((javaClass == ClassConstants.LONG) || (javaClass == ClassConstants.PLONG && !(sourceObject instanceof Long))) {
                return convertObjectToLong(sourceObject);
            } else if ((javaClass == ClassConstants.SHORT) || (javaClass == ClassConstants.PSHORT && !(sourceObject instanceof Short))) {
                return convertObjectToShort(sourceObject);
            } else if ((javaClass == ClassConstants.BYTE) || (javaClass == ClassConstants.PBYTE && !(sourceObject instanceof Byte))) {
                return convertObjectToByte(sourceObject);
            } else if (javaClass == ClassConstants.BIGINTEGER) {
                return convertObjectToBigInteger(sourceObject);
            } else if (javaClass == ClassConstants.BIGDECIMAL) {
                return convertObjectToBigDecimal(sourceObject);
            } else if (javaClass == ClassConstants.NUMBER) {
                return convertObjectToNumber(sourceObject);
            } else if ((javaClass == ClassConstants.BOOLEAN) || (javaClass == ClassConstants.PBOOLEAN  && !(sourceObject instanceof Boolean))) {
                return convertObjectToBoolean(sourceObject);
            } else if (javaClass == ClassConstants.APBYTE) {
                return convertObjectToByteArray(sourceObject);
            } else if (javaClass == ClassConstants.ABYTE) {
                return convertObjectToByteObjectArray(sourceObject);
            } else if (javaClass == ClassConstants.APCHAR) {
                return convertObjectToCharArray(sourceObject);
            } else if (javaClass == ClassConstants.ACHAR) {
                return convertObjectToCharacterArray(sourceObject);
            } else if ((sourceObject.getClass() == ClassConstants.STRING) && (javaClass == ClassConstants.CLASS)) {
                return convertObjectToClass(sourceObject);
            } else if(javaClass == ClassConstants.URL_Class) {
                return convertObjectToUrl(sourceObject);
            }
        } catch (ConversionException ce) {
            throw ce;
        } catch (Exception e) {
            throw ConversionException.couldNotBeConverted(sourceObject, javaClass, e);
        }
        
        // Check if object is instance of the real class for the primitive class.
        if ((((javaClass == ClassConstants.PBOOLEAN) && (sourceObject instanceof Boolean)  ) || 
            ((javaClass == ClassConstants.PLONG) && (sourceObject instanceof Long) ) || 
            ((javaClass == ClassConstants.PINT) && (sourceObject instanceof Integer)  ) || 
            ((javaClass == ClassConstants.PFLOAT) && (sourceObject instanceof Float)) || 
            ((javaClass == ClassConstants.PDOUBLE) &&  (sourceObject instanceof Double) ) || 
            ((javaClass == ClassConstants.PBYTE) &&  (sourceObject instanceof Byte)) || 
            ((javaClass == ClassConstants.PCHAR) &&  (sourceObject instanceof Character)) || 
            ((javaClass == ClassConstants.PSHORT) &&  (sourceObject instanceof Short)))) {
            return sourceObject;
        }

        // Delay this check as poor performance.
        if (javaClass.isInstance(sourceObject)) {
            return sourceObject;
        }
        if (ClassConstants.NOCONVERSION.isAssignableFrom(javaClass)) {
            return sourceObject;
        }

        throw ConversionException.couldNotBeConverted(sourceObject, javaClass);
    }

    /**
     * Build a valid instance of BigDecimal from the given sourceObject
     *    @param sourceObject    Valid instance of String, BigInteger, any Number
     */
    protected BigDecimal convertObjectToBigDecimal(Object sourceObject) throws ConversionException {
        BigDecimal bigDecimal = null;

        try {
            if (sourceObject instanceof String) {
                bigDecimal = new BigDecimal((String)sourceObject);
            } else if (sourceObject instanceof BigInteger) {
                bigDecimal = new BigDecimal((BigInteger)sourceObject);
            } else if (sourceObject instanceof Number) {
                // Doubles do not maintain scale, because of this it is 
                // impossible to distinguish between 1 and 1.0.  In order to
                // maintain backwards compatibility both 1 and 1.0 will be 
                // treated as BigDecimal(1).
                String numberString = String.valueOf(sourceObject);
                if(numberString.endsWith(".0") || numberString.contains(".0E+")) {
                    bigDecimal = new BigDecimal(((Number)sourceObject).doubleValue());
                } else {
                    bigDecimal = new BigDecimal(numberString);
                }
            } else {
                throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.BIGDECIMAL);
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.BIGDECIMAL, exception);
        }
        return bigDecimal;
    }

    /**
     * Build a valid instance of BigInteger from the provided sourceObject.
     *    @param sourceObject    Valid instance of String, BigDecimal, or any Number
     */
    protected BigInteger convertObjectToBigInteger(Object sourceObject) throws ConversionException {
        BigInteger bigInteger = null;

        try {
            if (sourceObject instanceof BigInteger) {
                bigInteger = (BigInteger)sourceObject;
            } else if (sourceObject instanceof String) {
                bigInteger = new BigInteger((String)sourceObject);
            } else if (sourceObject instanceof BigDecimal) {
                bigInteger = ((BigDecimal)sourceObject).toBigInteger();
            } else if (sourceObject instanceof Number) {
                bigInteger = new BigInteger(String.valueOf(((Number)sourceObject).longValue()));
            } else if (sourceObject instanceof Byte[]) {
                Byte[] objectBytes = (Byte[])sourceObject;
                byte[] bytes = new byte[objectBytes.length];
                for (int index = 0; index < objectBytes.length; index++) {
                    bytes[index] = objectBytes[index].byteValue();
                }
                bigInteger = new BigInteger(bytes);
            } else if (sourceObject instanceof byte[]) {
                bigInteger = new BigInteger((byte[]) sourceObject);
            } else {
                throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.BIGINTEGER);
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.BIGINTEGER, exception);
        }

        return bigInteger;
    }

    /**
     *    Build a valid instance of Boolean from the source object.
     *    't', 'T', "true", "TRUE", 1,'1'             -> Boolean(true)
     *    'f', 'F', "false", "FALSE", 0 ,'0'        -> Boolean(false)
     */
    protected Boolean convertObjectToBoolean(Object sourceObject) {
        if (sourceObject instanceof Character) {
            switch (Character.toLowerCase(((Character)sourceObject).charValue())) {
            case '1':
            case 't':
                return Boolean.TRUE;
            case '0':
            case 'f':
                return Boolean.FALSE;
            }
        }
        if (sourceObject instanceof String) {
            String stringValue = ((String)sourceObject).toLowerCase();
            if (stringValue.equals("t") || stringValue.equals("true") || stringValue.equals("1")) {
                return Boolean.TRUE;
            } else if (stringValue.equals("f") || stringValue.equals("false") || stringValue.equals("0")) {
                return Boolean.FALSE;
            }
        }
        if (sourceObject instanceof Number) {
            int intValue = ((Number)sourceObject).intValue();
            if (intValue != 0) {
                return Boolean.TRUE;
            } else if (intValue == 0) {
                return Boolean.FALSE;
            }
        }
        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.BOOLEAN);
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
        try {
            if (sourceObject instanceof String) {
                return Byte.valueOf((String)sourceObject);
            }
            if (sourceObject instanceof Number) {
                return Byte.valueOf(((Number)sourceObject).byteValue());
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.BYTE, exception);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.BYTE);
    }

    /**
      * Build a valid instance of a byte array from the given object.
      * This method does hex conversion of the string values.  Some
      * databases have problems with storing blobs unless the blob
      * is stored as a hex string.
      */
    protected byte[] convertObjectToByteArray(Object sourceObject) throws ConversionException {
        //Bug#3128838 Used when converted to Byte[]
        if (sourceObject instanceof byte[]) {
            return (byte[])sourceObject;
            //Related to Bug#3128838.  Add support to convert to Byte[]
        } else if (sourceObject instanceof Byte[]) {
            Byte[] objectBytes = (Byte[])sourceObject;
            byte[] bytes = new byte[objectBytes.length];
            for (int index = 0; index < objectBytes.length; index++) {
                bytes[index] = objectBytes[index].byteValue();
            }
            return bytes;
        } else if (sourceObject instanceof String) {
            return Helper.buildBytesFromHexString((String)sourceObject);
        } else if (sourceObject instanceof Blob) {
            Blob blob = (Blob)sourceObject;
            try {
                return blob.getBytes(1L, (int)blob.length());
            } catch (SQLException exception) {
                throw DatabaseException.sqlException(exception);
            }
        } else if (sourceObject instanceof InputStream) {
            InputStream inputStream = (InputStream)sourceObject;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                int tempInt = inputStream.read();
                while (tempInt != -1) {
                    outputStream.write(tempInt);
                    tempInt = inputStream.read();
                }
                return outputStream.toByteArray();
            } catch (IOException ioException) {
                throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.APBYTE, ioException);
            }
        } else if (sourceObject instanceof BigInteger) {
            return ((BigInteger)sourceObject).toByteArray();
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.APBYTE);
    }

    /**
      * Build a valid instance of a Byte array from the given object.
      * This method does hex conversion of the string values.  Some
      * databases have problems with storing blobs unless the blob
      * is stored as a hex string.
      */
    protected Byte[] convertObjectToByteObjectArray(Object sourceObject) throws ConversionException {
        byte[] bytes = convertObjectToByteArray(sourceObject);
        Byte[] objectBytes = new Byte[bytes.length];
        for (int index = 0; index < bytes.length; index++) {
            objectBytes[index] = Byte.valueOf(bytes[index]);
        }
        return objectBytes;
    }

    /**
     * Build a valid instance of java.util.Calendar from the given source object.
     *    @param sourceObject    Valid instance of java.util.Date, String, java.sql.Timestamp, or Long
     */
    protected Calendar convertObjectToCalendar(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof Calendar) {
            return (Calendar)sourceObject;
        } else if (sourceObject instanceof java.util.Date) {
            // PERF: Avoid double conversion for date subclasses.
            return Helper.calendarFromUtilDate((java.util.Date)sourceObject);
        }
        return Helper.calendarFromUtilDate(convertObjectToUtilDate(sourceObject));
    }

    /**
     * Build a valid instance of Character from the provided sourceObject.
     *    @param sourceObject    Valid instance of String or any Number
     */
    protected Character convertObjectToChar(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof String) {
            if (((String)sourceObject).length() < 1) {
                // ELBug336192 - Return default null value of char instead of returning null.
                return (Character)getDefaultNullValue(ClassConstants.PCHAR);
            }
            return Character.valueOf(((String)sourceObject).charAt(0));
        }

        if (sourceObject instanceof Number) {
            return Character.valueOf((char)((Number)sourceObject).byteValue());
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.CHAR);
    }

    /**
      * Build a valid instance of a Character array from the given object.
      */
    protected Character[] convertObjectToCharacterArray(Object sourceObject) throws ConversionException {
        String stringValue = convertObjectToString(sourceObject);
        Character[] chars = new Character[stringValue.length()];
        for (int index = 0; index < stringValue.length(); index++) {
            chars[index] = Character.valueOf(stringValue.charAt(index));
        }
        return chars;
    }

    /**
      * Build a valid instance of a char array from the given object.
      */
    protected char[] convertObjectToCharArray(Object sourceObject) throws ConversionException {
        if (sourceObject instanceof Character[]) {
            Character[] objectChars = (Character[])sourceObject;
            char[] chars = new char[objectChars.length];
            for (int index = 0; index < objectChars.length; index++) {
                chars[index] = objectChars[index].charValue();
            }
            return chars;
        }
        String stringValue = convertObjectToString(sourceObject);
        char[] chars = new char[stringValue.length()];
        for (int index = 0; index < stringValue.length(); index++) {
            chars[index] = stringValue.charAt(index);
        }
        return chars;
    }

    /**
     * Build a valid Class from the string that is passed in
     *    @param sourceObject    Valid instance of String
     */
    protected Class convertObjectToClass(Object sourceObject) throws ConversionException {
        Class theClass = null;
        if (!(sourceObject instanceof String)) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.CLASS);
        }
        try {
            // bug # 2799318
            theClass = getPrimitiveClass((String)sourceObject);
            if (theClass == null) {
                theClass = Class.forName((String)sourceObject, true, getLoader());
            }
        } catch (Exception exception) {
            throw ConversionException.couldNotBeConvertedToClass(sourceObject, ClassConstants.CLASS, exception);
        }
        return theClass;
    }

    /**
      * Convert the object to an instance of java.sql.Date.
      *    @param    sourceObject Object of type java.sql.Timestamp, java.util.Date, String or Long
      */
    protected java.sql.Date convertObjectToDate(Object sourceObject) throws ConversionException {
        java.sql.Date date = null;

        if (sourceObject instanceof java.sql.Date) {
            date = (java.sql.Date)sourceObject;//Helper date is not caught on class check.
        } else if (sourceObject instanceof java.sql.Timestamp) {
            date = Helper.dateFromTimestamp((java.sql.Timestamp)sourceObject);
        } else if (sourceObject.getClass() == ClassConstants.UTILDATE) {
            date = Helper.sqlDateFromUtilDate((java.util.Date)sourceObject);
        } else if (sourceObject instanceof Calendar) {
            return Helper.dateFromCalendar((Calendar)sourceObject);
        } else if (sourceObject instanceof String) {
            date = Helper.dateFromString((String)sourceObject);
        } else if (sourceObject instanceof Long) {
            date = Helper.dateFromLong((Long)sourceObject);
        } else {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.SQLDATE);
        }
        return date;
    }

    /**
      * Convert the object to an instance of Double.
      * @param                    sourceObject Object of type String or Number.
      * @caught exception    The Double(String) constructor throws a
      *         NumberFormatException if the String does not contain a
      *        parsable double.
      */
    protected Double convertObjectToDouble(Object sourceObject) throws ConversionException {
        try {
            if (sourceObject instanceof String) {
                return Double.valueOf((String)sourceObject);
            }
            if (sourceObject instanceof Number) {
                return Double.valueOf(((Number)sourceObject).doubleValue());
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.DOUBLE, exception);
        }
        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.DOUBLE);
    }

    /**
     * Build a valid Float instance from a String or another Number instance.
     * @caught exception    The Float(String) constructor throws a
     *         NumberFormatException if the String does not contain a
     *        parsable Float.
     */
    protected Float convertObjectToFloat(Object sourceObject) throws ConversionException {
        try {
            if (sourceObject instanceof String) {
                return Float.valueOf((String)sourceObject);
            }
            if (sourceObject instanceof Number) {
                return Float.valueOf(((Number)sourceObject).floatValue());
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.FLOAT, exception);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.FLOAT);
    }

    /**
     * Build a valid Integer instance from a String or another Number instance.
     * @caught exception    The Integer(String) constructor throws a
     *         NumberFormatException if the String does not contain a
     *        parsable integer.
     */
    protected Integer convertObjectToInteger(Object sourceObject) throws ConversionException {
        try {
            if (sourceObject instanceof String) {
                return Integer.valueOf((String)sourceObject);
            }

            if (sourceObject instanceof Number) {
                return Integer.valueOf(((Number)sourceObject).intValue());
            }

            if (sourceObject instanceof Boolean) {
                if (((Boolean)sourceObject).booleanValue()) {
                    return Integer.valueOf(1);
                } else {
                    return Integer.valueOf(0);
                }
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.INTEGER, exception);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.INTEGER);
    }

    /**
      * Build a valid Long instance from a String or another Number instance.
      * @caught exception    The Long(String) constructor throws a
      *         NumberFormatException if the String does not contain a
      *        parsable long.
      *
      */
    protected Long convertObjectToLong(Object sourceObject) throws ConversionException {
        try {
            if (sourceObject instanceof String) {
                return Long.valueOf((String)sourceObject);
            }
            if (sourceObject instanceof Number) {
                return Long.valueOf(((Number)sourceObject).longValue());
            }
            if (sourceObject instanceof java.util.Date) {
                return Long.valueOf(((java.util.Date)sourceObject).getTime());
            }
            if (sourceObject instanceof java.util.Calendar) {
                return Long.valueOf(((java.util.Calendar)sourceObject).getTimeInMillis());
            }

            if (sourceObject instanceof Boolean) {
                if (((Boolean)sourceObject).booleanValue()) {
                    return Long.valueOf(1);
                } else {
                    return Long.valueOf(0);
                }
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.LONG, exception);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.LONG);
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
        try {
            if (sourceObject instanceof String) {
                return new BigDecimal((String)sourceObject);
            }

            if (sourceObject instanceof Number) {
                return new BigDecimal(((Number)sourceObject).doubleValue());
            }

            if (sourceObject instanceof Boolean) {
                if (((Boolean)sourceObject).booleanValue()) {
                    return BigDecimal.valueOf(1);
                } else {
                    return BigDecimal.valueOf(0);
                }
            }
        } catch (NumberFormatException exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.NUMBER, exception);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.NUMBER);
    }

    /**
     * INTERNAL:
     * Build a valid Short instance from a String or another Number instance.
     * @caught exception    The Short(String) constructor throws a
     *     NumberFormatException if the String does not contain a
     *    parsable short.
     */
    protected Short convertObjectToShort(Object sourceObject) throws ConversionException {
        try {
            if (sourceObject instanceof String) {
                return Short.valueOf((String)sourceObject);
            }

            if (sourceObject instanceof Number) {
                return Short.valueOf(((Number)sourceObject).shortValue());
            }

            if (sourceObject instanceof Boolean) {
                if (((Boolean)sourceObject).booleanValue()) {
                    return Short.valueOf((short)1);
                } else {
                    return Short.valueOf((short)0);
                }
            }
        } catch (Exception exception) {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.SHORT, exception);
        }

        throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.SHORT);
    }

    /**
     * INTERNAL:
     * Converts objects to their string representations.  java.util.Date
     * is converted to a timestamp first and then to a string.  An array
     * of bytes is converted to a hex string.
     */
    protected String convertObjectToString(Object sourceObject) throws ConversionException {
        if (sourceObject.getClass() == ClassConstants.UTILDATE) {
            return Helper.printTimestamp(Helper.timestampFromDate((java.util.Date)sourceObject));
        } else if (sourceObject instanceof Calendar) {
            return Helper.printCalendar((Calendar)sourceObject);
        } else if (sourceObject instanceof java.sql.Timestamp) {
            return Helper.printTimestamp((java.sql.Timestamp)sourceObject);
        } else if (sourceObject instanceof java.sql.Date) {
            return Helper.printDate((java.sql.Date)sourceObject);
        } else if (sourceObject instanceof java.sql.Time) {
            return Helper.printTime((java.sql.Time)sourceObject);
        } else if (sourceObject instanceof byte[]) {
            return Helper.buildHexStringFromBytes((byte[])sourceObject);
            //Bug#3854296 Added support to convert Byte[], char[] and Character[] to String correctly
        } else if (sourceObject instanceof Byte[]) {
            return Helper.buildHexStringFromBytes(convertObjectToByteArray(sourceObject));
        } else if (sourceObject instanceof char[]) {
            return new String((char[])sourceObject);
        } else if (sourceObject instanceof Character[]) {
            return new String(convertObjectToCharArray(sourceObject));
        } else if (sourceObject instanceof Class) {
            return ((Class)sourceObject).getName();
        } else if (sourceObject instanceof Character) {
            return sourceObject.toString();
        } else if (sourceObject instanceof Clob) {
            Clob clob = (Clob)sourceObject;
            try {
                return clob.getSubString(1L, (int)clob.length());
            } catch (SQLException exception) {
                throw DatabaseException.sqlException(exception);
            }
        }

        return sourceObject.toString();
    }

    /**
     * INTERNAL:
     * Build a valid instance of java.sql.Time from the given source object.
     * @param    sourceObject    Valid instance of java.sql.Time, String, java.util.Date, java.sql.Timestamp, or Long
     */
    protected java.sql.Time convertObjectToTime(Object sourceObject) throws ConversionException {
        java.sql.Time time = null;

        if (sourceObject instanceof java.sql.Time) {
            return (java.sql.Time)sourceObject;//Helper timestamp is not caught on class check.
        }

        if (sourceObject instanceof String) {
            time = Helper.timeFromString((String)sourceObject);
        } else if (sourceObject.getClass() == ClassConstants.UTILDATE) {
            time = Helper.timeFromDate((java.util.Date)sourceObject);
        } else if (sourceObject instanceof java.sql.Timestamp) {
            time = Helper.timeFromTimestamp((java.sql.Timestamp)sourceObject);
        } else if (sourceObject instanceof Calendar) {
            return Helper.timeFromCalendar((Calendar)sourceObject);
        } else if (sourceObject instanceof Long) {
            time = Helper.timeFromLong((Long)sourceObject);
        } else {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.TIME);
        }
        return time;
    }

    /**
     * INTERNAL:
     * Build a valid instance of java.sql.Timestamp from the given source object.
     * @param sourceObject    Valid obejct of class java.sql.Timestamp, String, java.util.Date, or Long
     */
    protected java.sql.Timestamp convertObjectToTimestamp(Object sourceObject) throws ConversionException {
        java.sql.Timestamp timestamp = null;

        if (sourceObject instanceof java.sql.Timestamp) {
            return (java.sql.Timestamp)sourceObject;// Helper timestamp is not caught on class check.
        }

        if (sourceObject instanceof String) {
            timestamp = Helper.timestampFromString((String)sourceObject);
        } else if (sourceObject instanceof java.util.Date) {// This handles all date and subclasses, sql.Date, sql.Time conversions.
            timestamp = Helper.timestampFromDate((java.util.Date)sourceObject);
        } else if (sourceObject instanceof Calendar) {
            return Helper.timestampFromCalendar((Calendar)sourceObject);
        } else if (sourceObject instanceof Long) {
            timestamp = Helper.timestampFromLong((Long)sourceObject);
        } else {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.TIMESTAMP);
        }
        return timestamp;
    }

    /**
     * INTERNAL:
     * Build a valid instance of java.net.URL from the given source object.
     * @param sourceObject    Valid instance of java.net.URL, or String
     */
    protected URL convertObjectToUrl(Object sourceObject) throws ConversionException {
        if(sourceObject.getClass() == ClassConstants.URL_Class) {
            return (URL) sourceObject;
        } else if (sourceObject.getClass() == ClassConstants.STRING) {
            try {
                return new URL((String) sourceObject);
            } catch(Exception e) {
                throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.URL_Class, e);
            }
        } else {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.URL_Class);
        }
    }

    /**
     * INTERNAL:
     * Build a valid instance of java.util.Date from the given source object.
     * @param sourceObject    Valid instance of java.util.Date, String, java.sql.Timestamp, or Long
     */
    protected java.util.Date convertObjectToUtilDate(Object sourceObject) throws ConversionException {
        java.util.Date date = null;

        if (sourceObject.getClass() == java.util.Date.class) {
            date = (java.util.Date)sourceObject;//used when converting util.Date to Calendar
        } else if (sourceObject instanceof java.sql.Date) {
            date = Helper.utilDateFromSQLDate((java.sql.Date)sourceObject);
        } else if (sourceObject instanceof java.sql.Time) {
            date = Helper.utilDateFromTime((java.sql.Time)sourceObject);
        } else if (sourceObject instanceof String) {
            date = Helper.utilDateFromTimestamp(Helper.timestampFromString((String)sourceObject));
        } else if (sourceObject instanceof java.sql.Timestamp) {
            date = Helper.utilDateFromTimestamp((java.sql.Timestamp)sourceObject);
        } else if (sourceObject instanceof Calendar) {
            return ((Calendar)sourceObject).getTime();
        } else if (sourceObject instanceof Long) {
            date = Helper.utilDateFromLong((Long)sourceObject);
        } else if (sourceObject instanceof java.util.Date) {
            date = new java.util.Date(((java.util.Date) sourceObject).getTime());
        } else {
            throw ConversionException.couldNotBeConverted(sourceObject, ClassConstants.UTILDATE);
        }
        return date;
    }

    /**
     * PUBLIC:
     * Resolve the given String className into a class using this
     * ConversionManager's classloader.
     */
    public Class convertClassNameToClass(String className) throws ConversionException {
        return convertObjectToClass(className);
    }

    /**
     * A singleton conversion manager is used to handle generic conversions.
     * This should not be used for conversion under the session context, these must go through the platform.
     * This allows for the singleton to be customized through setting the default to a user defined subclass.
     */
    public static ConversionManager getDefaultManager() {
        if (defaultManager == null) {
            setDefaultManager(new ConversionManager());
            defaultManager.setShouldUseClassLoaderFromCurrentThread(true);
        }
        return defaultManager;
    }

    /**
     * INTERNAL:
     * Allow for the null values for classes to be defaulted in one place.
     * Any nulls read from the database to be converted to the class will be given the specified null value.
     */
    public Object getDefaultNullValue(Class theClass) {
        if (this.defaultNullValues == null) return null;
        return getDefaultNullValues().get(theClass);
    }

    /**
     * INTERNAL:
     * Allow for the null values for classes to be defaulted in one place.
     * Any nulls read from the database to be converted to the class will be given the specified null value.
     */
    public Map getDefaultNullValues() {
        return defaultNullValues;
    }

    /**
     * INTERNAL:
     */
    public ClassLoader getLoader() {
        if (shouldUseClassLoaderFromCurrentThread()) {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (ClassLoader)AccessController.doPrivileged(new PrivilegedGetContextClassLoader(Thread.currentThread()));
                } catch (PrivilegedActionException exception) {
                    // should not be thrown
                }
            } else {
                return PrivilegedAccessHelper.getContextClassLoader(Thread.currentThread());
            }
        }
        if (loader == null) {
            if (defaultLoader == null) {
                //CR 2621
                ClassLoader loader = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        loader = (ClassLoader)AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(ClassConstants.ConversionManager_Class));
                    } catch (PrivilegedActionException exc){
                        // will not be thrown
                    }
                } else {
                    loader = PrivilegedAccessHelper.getClassLoaderForClass(ClassConstants.ConversionManager_Class);
                }
                setLoader(loader);
            } else {
                setLoader(getDefaultLoader());
            }
        }
        return loader;
    }

    /**
     * INTERNAL
     */
    public boolean hasDefaultNullValues(){
        return this.defaultNullValues != null;
    }
    
    /**
     * INTERNAL:
     * Load the class using the default managers class loader.
     * This is a thread based class loader by default.
     * This should be used to load all classes as Class.forName can only
     * see classes on the same classpath as the eclipselink.jar.
     */
    public static Class loadClass(String className) {
        return (Class)getDefaultManager().convertObject(className, ClassConstants.CLASS);
    }

    /**
     * INTERNAL:
     * This is used to determine the wrapper class for a primitive.
     */
    public static Class getObjectClass(Class javaClass) {
        // Null means unknown always for classifications.
        if (javaClass == null) {
            return null;
        }

        if (javaClass.isPrimitive()) {
            if (javaClass == ClassConstants.PCHAR) {
                return ClassConstants.CHAR;
            }
            if (javaClass == ClassConstants.PINT) {
                return ClassConstants.INTEGER;
            }
            if (javaClass == ClassConstants.PDOUBLE) {
                return ClassConstants.DOUBLE;
            }
            if (javaClass == ClassConstants.PFLOAT) {
                return ClassConstants.FLOAT;
            }
            if (javaClass == ClassConstants.PLONG) {
                return ClassConstants.LONG;
            }
            if (javaClass == ClassConstants.PSHORT) {
                return ClassConstants.SHORT;
            }
            if (javaClass == ClassConstants.PBYTE) {
                return ClassConstants.BYTE;
            }
            if (javaClass == ClassConstants.PBOOLEAN) {
                return ClassConstants.BOOLEAN;
            }
            } else if (javaClass == ClassConstants.APBYTE) {
                return ClassConstants.APBYTE;
            } else if (javaClass == ClassConstants.APCHAR) {
                return ClassConstants.APCHAR;
            } else {
                return javaClass;
            }

        return javaClass;
    }

    /**
     * INTERNAL:
     * Returns a class based on the passed in string.
     */
    public static Class getPrimitiveClass(String classType) {
        if (classType.equals("int")) {
            return Integer.TYPE;
        } else if (classType.equals("boolean")) {
            return Boolean.TYPE;
        } else if (classType.equals("char")) {
            return Character.TYPE;
        } else if (classType.equals("short")) {
            return Short.TYPE;
        } else if (classType.equals("byte")) {
            return Byte.TYPE;
        } else if (classType.equals("float")) {
            return Float.TYPE;
        } else if (classType.equals("double")) {
            return Double.TYPE;
        } else if (classType.equals("long")) {
            return Long.TYPE;
        }

        return null;
    }

    /**
     * A singleton conversion manager is used to handle generic conversions.
     * This should not be used for conversion under the session context, these must go through the platform.
     * This allows for the singleton to be customized through setting the default to a user defined subclass.
     */
    public static void setDefaultManager(ConversionManager theManager) {
        defaultManager = theManager;
    }

    /**
     * INTERNAL:
     * Allow for the null values for classes to be defaulted in one place.
     * Any nulls read from the database to be converted to the class will be given the specified null value.
     * Primitive null values should be set to the wrapper class.
     */
    public void setDefaultNullValue(Class theClass, Object theValue) {
        if (this.defaultNullValues == null){
            this.defaultNullValues = new HashMap(5); 
        }
        getDefaultNullValues().put(theClass, theValue);
    }

    /**
     * INTERNAL:
     * Allow for the null values for classes to be defaulted in one place.
     * Any nulls read from the database to be converted to the class will be given the specified null value.
     */
    public void setDefaultNullValues(Map defaultNullValues) {
        this.defaultNullValues = defaultNullValues;
    }

    /**
     * INTERNAL:
     * @parameter java.lang.ClassLoader
     */
    public void setLoader(ClassLoader classLoader) {
        shouldUseClassLoaderFromCurrentThread = false;
        loader = classLoader;
    }

    /**
     * INTERNAL:
     * Set the default class loader to use if no instance-level loader is set
     * @parameter java.lang.ClassLoader
     */
    public static void setDefaultLoader(ClassLoader classLoader) {
        defaultLoader = classLoader;
    }

    /**
     * INTERNAL:
     * Get the default class loader to use if no instance-level loader is set
     * @return java.lang.ClassLoader
     */
    public static ClassLoader getDefaultLoader() {
        return defaultLoader;
    }

    /**
     * ADVANCED:
     * This flag should be set if the current thread classLoader should be used.
     * This is the case in certain Application Servers were the class loader must be
     * retrieved from the current Thread.  If classNotFoundExceptions are being thrown then set
     * this flag.  In certain cases it will resolve the problem
     */
    public void setShouldUseClassLoaderFromCurrentThread(boolean useCurrentThread) {
        this.shouldUseClassLoaderFromCurrentThread = useCurrentThread;
    }

    /**
     * ADVANCED:
     *  This flag should be set if the current thread classLoader should be used.
     * This is the case in certain Application Servers were the class loader must be
     * retrieved from the current Thread.  If classNotFoundExceptions are being thrown then set
     * this flag.  In certain cases it will resolve the problem
     */
    public boolean shouldUseClassLoaderFromCurrentThread() {
        return this.shouldUseClassLoaderFromCurrentThread;
    }

    /**
     * PUBLIC:
     * Return the list of Classes that can be converted to from the passed in javaClass.
     * @param javaClass - the class that is converted from
     * @return - a vector of classes
     */
    public Vector getDataTypesConvertedFrom(Class javaClass) {
        if (dataTypesConvertedFromAClass.isEmpty()) {
            buildDataTypesConvertedFromAClass();
        }
        return (Vector)dataTypesConvertedFromAClass.get(javaClass);
    }

    /**
     * PUBLIC:
     * Return the list of Classes that can be converted from to the passed in javaClass.
     * @param javaClass - the class that is converted to
     * @return - a vector of classes
     */
    public Vector getDataTypesConvertedTo(Class javaClass) {
        if (dataTypesConvertedToAClass.isEmpty()) {
            buildDataTypesConvertedToAClass();
        }
        return (Vector)dataTypesConvertedToAClass.get(javaClass);
    }

    protected Vector buildNumberVec() {
        Vector vec = new Vector();
        vec.addElement(BigInteger.class);
        vec.addElement(BigDecimal.class);
        vec.addElement(Byte.class);
        vec.addElement(Double.class);
        vec.addElement(Float.class);
        vec.addElement(Integer.class);
        vec.addElement(Long.class);
        vec.addElement(Short.class);
        vec.addElement(Number.class);
        return vec;
    }

    protected Vector buildDateTimeVec() {
        Vector vec = new Vector();
        vec.addElement(java.util.Date.class);
        vec.addElement(Timestamp.class);
        vec.addElement(Calendar.class);
        return vec;
    }

    protected void buildDataTypesConvertedFromAClass() {
        dataTypesConvertedFromAClass.put(BigDecimal.class, buildFromBigDecimalVec());
        dataTypesConvertedFromAClass.put(BigInteger.class, buildFromBigIntegerVec());
        dataTypesConvertedFromAClass.put(Blob.class, buildFromBlobVec());
        dataTypesConvertedFromAClass.put(Boolean.class, buildFromBooleanVec());
        dataTypesConvertedFromAClass.put(byte[].class, buildFromByteArrayVec());
        dataTypesConvertedFromAClass.put(Byte.class, buildFromByteVec());
        dataTypesConvertedFromAClass.put(Calendar.class, buildFromCalendarVec());
        dataTypesConvertedFromAClass.put(Character.class, buildFromCharacterVec());
        dataTypesConvertedFromAClass.put(Clob.class, buildFromClobVec());
        dataTypesConvertedFromAClass.put(java.sql.Date.class, buildFromDateVec());
        dataTypesConvertedFromAClass.put(Double.class, buildFromDoubleVec());
        dataTypesConvertedFromAClass.put(Float.class, buildFromFloatVec());
        dataTypesConvertedFromAClass.put(Integer.class, buildFromIntegerVec());
        dataTypesConvertedFromAClass.put(Long.class, buildFromLongVec());
        dataTypesConvertedFromAClass.put(Number.class, buildFromNumberVec());
        dataTypesConvertedFromAClass.put(Short.class, buildFromShortVec());
        dataTypesConvertedFromAClass.put(String.class, buildFromStringVec());
        dataTypesConvertedFromAClass.put(Timestamp.class, buildFromTimestampVec());
        dataTypesConvertedFromAClass.put(Time.class, buildFromTimeVec());
        dataTypesConvertedFromAClass.put(java.util.Date.class, buildFromUtilDateVec());
        dataTypesConvertedFromAClass.put(Byte[].class, buildFromByteObjectArraryVec());
        dataTypesConvertedFromAClass.put(char[].class, buildFromCharArrayVec());
        dataTypesConvertedFromAClass.put(Character[].class, buildFromCharacterArrayVec());
    }

    protected Vector buildFromBooleanVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Boolean.class);
        vec.addElement(Integer.class);
        vec.addElement(Long.class);
        vec.addElement(Short.class);
        vec.addElement(Number.class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        vec.addElement(boolean.class);
        vec.addElement(int.class);
        vec.addElement(long.class);
        vec.addElement(short.class);
        return vec;
    }

    protected Vector buildFromNumberVec() {
        Vector vec = buildNumberVec();
        vec.addElement(String.class);
        vec.addElement(Character.class);
        vec.addElement(Boolean.class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        vec.addElement(char.class);
        vec.addElement(int.class);
        vec.addElement(double.class);
        vec.addElement(float.class);
        vec.addElement(long.class);
        vec.addElement(short.class);
        vec.addElement(byte.class);
        vec.addElement(boolean.class);
        return vec;
    }

    protected Vector buildFromBigDecimalVec() {
        return buildFromNumberVec();
    }

    protected Vector buildFromBigIntegerVec() {
        return buildFromNumberVec();
    }

    protected Vector buildFromIntegerVec() {
        return buildFromNumberVec();
    }

    protected Vector buildFromFloatVec() {
        return buildFromNumberVec();
    }

    protected Vector buildFromDoubleVec() {
        return buildFromNumberVec();
    }

    protected Vector buildFromShortVec() {
        return buildFromNumberVec();
    }

    protected Vector buildFromByteVec() {
        return buildFromNumberVec();
    }

    protected Vector buildFromLongVec() {
        Vector vec = buildFromNumberVec();
        vec.addAll(buildDateTimeVec());
        vec.addElement(java.sql.Date.class);
        vec.addElement(Time.class);
        return vec;
    }

    protected Vector buildFromStringVec() {
        Vector vec = buildFromLongVec();
        vec.addElement(Byte[].class);
        vec.addElement(byte[].class);
        vec.addElement(Clob.class);
        return vec;
    }

    protected Vector buildFromCharacterVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Boolean.class);
        vec.addElement(Character[].class);
        vec.addElement(Character.class);
        vec.addElement(char[].class);
        vec.addElement(char.class);
        vec.addElement(boolean.class);
        return vec;
    }

    protected Vector buildFromByteArrayVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(byte[].class);
        vec.addElement(Byte[].class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        return vec;
    }

    protected Vector buildFromClobVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        return vec;
    }

    protected Vector buildFromBlobVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Byte[].class);
        vec.addElement(byte[].class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        return vec;
    }

    protected Vector buildFromUtilDateVec() {
        Vector vec = buildDateTimeVec();
        vec.addElement(String.class);
        vec.addElement(Long.class);
        vec.addElement(java.sql.Date.class);
        vec.addElement(Time.class);
        vec.addElement(long.class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        return vec;
    }

    protected Vector buildFromTimestampVec() {
        return buildFromUtilDateVec();
    }

    protected Vector buildFromCalendarVec() {
        return buildFromUtilDateVec();
    }

    protected Vector buildFromDateVec() {
        Vector vec = buildDateTimeVec();
        vec.addElement(String.class);
        vec.addElement(Long.class);
        vec.addElement(java.sql.Date.class);
        vec.addElement(long.class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        return vec;
    }

    protected Vector buildFromTimeVec() {
        Vector vec = buildDateTimeVec();
        vec.addElement(String.class);
        vec.addElement(Long.class);
        vec.addElement(Time.class);
        vec.addElement(long.class);
        vec.addElement(Character[].class);
        vec.addElement(char[].class);
        return vec;
    }

    protected Vector buildFromByteObjectArraryVec() {
        Vector vec = new Vector();
        vec.addElement(Blob.class);
        vec.addElement(byte[].class);
        return vec;
    }

    protected Vector buildFromCharArrayVec() {
        Vector vec = new Vector();
        vec.addElement(Clob.class);
        return vec;
    }

    protected Vector buildFromCharacterArrayVec() {
        Vector vec = new Vector();
        vec.addElement(Clob.class);
        return vec;
    }

    protected void buildDataTypesConvertedToAClass() {
        dataTypesConvertedToAClass.put(BigDecimal.class, buildToBigDecimalVec());
        dataTypesConvertedToAClass.put(BigInteger.class, buildToBigIntegerVec());
        dataTypesConvertedToAClass.put(Boolean.class, buildToBooleanVec());
        dataTypesConvertedToAClass.put(Byte.class, buildToByteVec());
        dataTypesConvertedToAClass.put(byte[].class, buildToByteArrayVec());
        dataTypesConvertedToAClass.put(Byte[].class, buildToByteObjectArrayVec());
        dataTypesConvertedToAClass.put(Calendar.class, buildToCalendarVec());
        dataTypesConvertedToAClass.put(Character.class, buildToCharacterVec());
        dataTypesConvertedToAClass.put(Character[].class, buildToCharacterArrayVec());
        dataTypesConvertedToAClass.put(char[].class, buildToCharArrayVec());
        dataTypesConvertedToAClass.put(java.sql.Date.class, buildToDateVec());
        dataTypesConvertedToAClass.put(Double.class, buildToDoubleVec());
        dataTypesConvertedToAClass.put(Float.class, buildToFloatVec());
        dataTypesConvertedToAClass.put(Integer.class, buildToIntegerVec());
        dataTypesConvertedToAClass.put(Long.class, buildToLongVec());
        dataTypesConvertedToAClass.put(Number.class, buildToNumberVec());
        dataTypesConvertedToAClass.put(Short.class, buildToShortVec());
        dataTypesConvertedToAClass.put(String.class, buildToStringVec());
        dataTypesConvertedToAClass.put(Timestamp.class, buildToTimestampVec());
        dataTypesConvertedToAClass.put(Time.class, buildToTimeVec());
        dataTypesConvertedToAClass.put(java.util.Date.class, buildToUtilDateVec());
        dataTypesConvertedToAClass.put(Clob.class, buildToClobVec());
        dataTypesConvertedToAClass.put(Blob.class, buildToBlobVec());
    }

    protected Vector buildAllTypesToAClassVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Integer.class);
        vec.addElement(java.util.Date.class);
        vec.addElement(java.sql.Date.class);
        vec.addElement(Time.class);
        vec.addElement(Timestamp.class);
        vec.addElement(Calendar.class);
        vec.addElement(Character.class);
        vec.addElement(Double.class);
        vec.addElement(Float.class);
        vec.addElement(Long.class);
        vec.addElement(Short.class);
        vec.addElement(Byte.class);
        vec.addElement(BigInteger.class);
        vec.addElement(BigDecimal.class);
        vec.addElement(Number.class);
        vec.addElement(Boolean.class);
        vec.addElement(Character[].class);
        vec.addElement(Blob.class);
        vec.addElement(Clob.class);
        return vec;
    }

    protected Vector buildToBigDecimalVec() {
        Vector vec = buildNumberVec();
        vec.addElement(String.class);
        return vec;
    }

    protected Vector buildToBigIntegerVec() {
        return buildToBigDecimalVec();
    }

    protected Vector buildToBooleanVec() {
        Vector vec = buildToBigDecimalVec();
        vec.addElement(Character.class);
        vec.addElement(Boolean.class);
        return vec;
    }

    protected Vector buildToByteVec() {
        return buildToBigDecimalVec();
    }

    protected Vector buildToDoubleVec() {
        return buildToBigDecimalVec();
    }

    protected Vector buildToFloatVec() {
        return buildToBigDecimalVec();
    }

    protected Vector buildToIntegerVec() {
        Vector vec = buildToBigDecimalVec();
        vec.addElement(Boolean.class);
        return vec;
    }

    protected Vector buildToLongVec() {
        Vector vec = buildToIntegerVec();
        vec.addElement(Calendar.class);
        vec.addElement(java.util.Date.class);
        return vec;
    }

    protected Vector buildToNumberVec() {
        return buildToIntegerVec();
    }

    protected Vector buildToShortVec() {
        return buildToIntegerVec();
    }

    protected Vector buildToByteArrayVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(Blob.class);
        vec.addElement(byte[].class);
        vec.addElement(Byte[].class);
        return vec;
    }

    protected Vector buildToByteObjectArrayVec() {
        Vector vec = buildToByteArrayVec();
        vec.addElement(Byte[].class);
        return vec;
    }

    protected Vector buildToCharacterVec() {
        Vector vec = buildToBigDecimalVec();
        vec.addElement(Character.class);
        return vec;
    }

    protected Vector buildToCharacterArrayVec() {
        return buildAllTypesToAClassVec();
    }

    protected Vector buildToCharArrayVec() {
        return buildAllTypesToAClassVec();
    }

    protected Vector buildToStringVec() {
        return buildAllTypesToAClassVec();
    }

    protected Vector buildToCalendarVec() {
        Vector vec = buildDateTimeVec();
        vec.addElement(String.class);
        vec.addElement(Long.class);
        vec.addElement(java.sql.Date.class);
        vec.addElement(Time.class);
        return vec;
    }

    protected Vector buildToTimestampVec() {
        return buildToCalendarVec();
    }

    protected Vector buildToUtilDateVec() {
        return buildToCalendarVec();
    }

    protected Vector buildToDateVec() {
        Vector vec = buildDateTimeVec();
        vec.addElement(String.class);
        vec.addElement(Long.class);
        vec.addElement(java.sql.Date.class);
        return vec;
    }

    protected Vector buildToTimeVec() {
        Vector vec = buildDateTimeVec();
        vec.addElement(String.class);
        vec.addElement(Long.class);
        vec.addElement(Time.class);
        return vec;
    }

    protected Vector buildToBlobVec() {
        Vector vec = new Vector();
        vec.addElement(Byte[].class);
        vec.addElement(byte[].class);
        return vec;
    }

    protected Vector buildToClobVec() {
        Vector vec = new Vector();
        vec.addElement(String.class);
        vec.addElement(char[].class);
        vec.addElement(Character[].class);
        return vec;
    }
}
