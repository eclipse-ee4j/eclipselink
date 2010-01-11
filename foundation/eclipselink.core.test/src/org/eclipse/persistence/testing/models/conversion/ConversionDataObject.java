/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.conversion;

import java.util.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.tools.schemaframework.*;

public class ConversionDataObject {
    public int id;

    // ====================================
    // Direct Mapping Variables
    // ====================================
    // Primitive types
    public char aPChar;
    public char[] aPCharArray;
    public int aPInt;
    public float aPFloat;
    public boolean aPBoolean;
    public long aPLong;
    public double aPDouble;
    public byte aPByte;
    public byte[] aPByteArray;
    public short aPShort;

    // Primitive wrapper types
    public Character aCharacter;
    public Integer anInteger;
    public Float aFloat;
    public Boolean aBoolean;
    public Long aLong;
    public Double aDouble;
    public Byte aByte;
    public Short aShort;

    // Misc. number types
    public Number aNumber;
    public java.math.BigDecimal aBigDecimal;
    public java.math.BigInteger aBigInteger;

    // Date/Time types
    public java.sql.Date anSQLDate;
    public java.util.Date aJavaDate;
    public java.sql.Time aTime;
    public java.sql.Timestamp aTimestamp;
    public java.util.Calendar aCalendar;

    // Other
    public String aString;

    // ====================================
    // Type Conversion Mapping Variables
    // ====================================
    //	public int intToChar;
    public long timestamp2Long;
    public Date dateToTimestamp;// Only mapped with DB2.  Used for nano-second test
    public int intToFloat;
    public int intToLong;
    public int intToDouble;
    public int intToShort;
    public int intToString;
    public int intToByte;

    //	public float floatToChar;
    public float floatToInt;
    public float floatToLong;
    public float floatToDouble;
    public float floatToShort;
    public float floatToString;
    public float floatToByte;
    public String stringToInt;

    public static ConversionDataObject example1() {
        ConversionDataObject example = new ConversionDataObject();

        example.aPChar = 'a';
        example.setAPCharArray(new char[] { 'a', 'b', 'c' });
        example.aPInt = 1;
        example.aPFloat = 1.0F;
        example.aPBoolean = true;
        example.aPLong = 1L;
        example.aPDouble = 1.0;
        example.aPByte = 1;
        example.setAPByteArray(new byte[] { 1, 2, 3 });
        example.aPShort = 1;
        example.aCharacter = new Character('a');
        example.anInteger = new Integer(1);
        example.aFloat = new Float(1.0);
        example.aBoolean = new Boolean(false);
        example.aLong = new Long(1L);
        example.aDouble = new Double(1.0);
        example.aByte = new Byte((byte)1);
        example.aShort = new Short((short)1);
        example.aBigDecimal = new java.math.BigDecimal(1.0);
        example.aBigInteger = new java.math.BigInteger("1");
        example.aNumber = example.aBigDecimal;
        example.anSQLDate = Helper.dateFromYearMonthDate(1903, 3, 3);
        Calendar c = Calendar.getInstance();
        c.set(1903, 3, 3);
        c.set(Calendar.MILLISECOND, 0);
        example.aJavaDate = c.getTime();
        example.aCalendar = Calendar.getInstance();
        example.aCalendar.set(1999, 06, 06, 0, 0, 0);
        example.aCalendar.set(Calendar.MILLISECOND, 0);
        example.aTime = Helper.timeFromHourMinuteSecond(3, 3, 3);
        example.aTimestamp = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1903, 3, 3, 3, 3, 3, 0);
        example.aString = new String("Conversion Managaer Test Example 1");

        //	example.intToChar = 111;
        example.timestamp2Long = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1990, 1, 1, 0, 0, 0, 0).getTime();
        example.dateToTimestamp = new java.util.Date(System.currentTimeMillis());
        example.intToFloat = 111;
        example.intToLong = 111;
        example.intToDouble = 111;
        example.intToShort = 111;
        example.intToString = 111;
        example.intToByte = 11;

        //	example.floatToChar = 111;
        example.floatToInt = 111;
        example.floatToLong = 111;
        example.floatToDouble = 111;
        example.floatToShort = 111;
        example.floatToString = 111;
        example.floatToByte = 11;

        example.stringToInt = new String("111");

        return example;
    }

    public static ConversionDataObject example2() {
        ConversionDataObject example = new ConversionDataObject();

        example.aPChar = 'b';
        example.setAPCharArray(new char[] { 'd', 'e', 'f' });
        example.aPInt = 2;
        example.aPFloat = 2.0F;
        example.aPBoolean = false;
        example.aPLong = 2L;
        example.aPDouble = 2.0;
        example.aPByte = 2;
        example.setAPByteArray(new byte[] { 4, 5, 6 });
        example.aPShort = 2;
        example.aCharacter = new Character('b');
        example.anInteger = new Integer(2);
        example.aFloat = new Float(2.0);
        example.aBoolean = new Boolean(true);
        example.aLong = new Long(2L);
        example.aDouble = new Double(2.0);
        example.aByte = new Byte((byte)2);
        example.aShort = new Short((short)2);
        example.aBigDecimal = new java.math.BigDecimal(2.0);
        example.aBigInteger = new java.math.BigInteger("2");
        example.aNumber = example.aBigDecimal;
        example.anSQLDate = Helper.dateFromYearMonthDate(1906, 6, 6);
        Calendar c = Calendar.getInstance();
        c.set(1906, 6, 6);
        c.set(Calendar.MILLISECOND, 0);
        example.aJavaDate = c.getTime();
        example.aCalendar = Calendar.getInstance();
        example.aCalendar.set(1991, 11, 10, 0, 0, 0);
        example.aCalendar.set(java.util.Calendar.MILLISECOND, 0);
        example.aTime = Helper.timeFromHourMinuteSecond(6, 6, 6);
        example.aTimestamp = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1906, 6, 6, 6, 6, 6, 0);
        example.aString = new String("Conversion Managaer Test Example 2");

        //	example.intToChar = 222;
        example.timestamp2Long = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1999, 9, 9, 0, 0, 0, 0).getTime();
        example.intToFloat = 222;
        example.intToLong = 222;
        example.intToDouble = 222;
        example.intToShort = 222;
        example.intToString = 222;
        example.intToByte = 22;

        //	example.floatToChar = 222;
        example.floatToInt = 222;
        example.floatToLong = 222;
        example.floatToDouble = 222;
        example.floatToShort = 222;
        example.floatToString = 222;
        example.floatToByte = 22;

        example.stringToInt = new String("222");

        return example;
    }

    public static ConversionDataObject example3() {
        ConversionDataObject example = new ConversionDataObject();

        example.aPChar = 'c';
        example.setAPCharArray(new char[] { 'g', 'h', 'i' });
        example.aPInt = 3;
        example.aPFloat = 2.0F;
        example.aPBoolean = false;
        example.aPLong = 3L;
        example.aPDouble = 3.0;
        example.aPByte = 3;
        example.setAPByteArray(new byte[] { 7, 8, 9 });
        example.aPShort = 3;
        example.aCharacter = new Character('c');
        example.anInteger = new Integer(3);
        example.aFloat = new Float(3.0);
        example.aBoolean = new Boolean(true);
        example.aLong = new Long(3L);
        example.aDouble = new Double(3.0);
        example.aByte = new Byte((byte)3);
        example.aShort = new Short((short)3);
        example.aBigDecimal = new java.math.BigDecimal(3.0);
        example.aBigInteger = new java.math.BigInteger("3");
        example.aNumber = example.aBigDecimal;
        example.anSQLDate = Helper.dateFromYearMonthDate(1909, 9, 9);
        Calendar c = Calendar.getInstance();
        c.set(1909, 9, 9);
        c.set(Calendar.MILLISECOND, 0);
        example.aJavaDate = c.getTime();
        example.aCalendar = Calendar.getInstance();
        example.aCalendar.set(2001, 03, 8, 0, 0, 0);
        example.aCalendar.set(Calendar.MILLISECOND, 0);
        example.aTime = Helper.timeFromHourMinuteSecond(9, 9, 9);
        example.aTimestamp = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1909, 9, 9, 9, 9, 9, 0);
        example.aString = new String("Conversion Managaer Test Example 3");

        //	example.intToChar = 333;
        example.timestamp2Long = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1990, 1, 1, 0, 0, 0, 0).getTime();
        example.intToFloat = 333;
        example.intToLong = 333;
        example.intToDouble = 333;
        example.intToShort = 333;
        example.intToString = 333;
        example.intToByte = 33;

        //	example.floatToChar = 333;
        example.floatToInt = 333;
        example.floatToLong = 333;
        example.floatToDouble = 333;
        example.floatToShort = 333;
        example.floatToString = 333;
        example.floatToByte = 33;

        example.stringToInt = new String("333");

        return example;
    }

    public byte[] getAPByteArray() {
        return aPByteArray;
    }

    public char[] getAPCharArray() {
        return aPCharArray;
    }

    public void setAPByteArray(byte[] aPByteArray) {
        this.aPByteArray = aPByteArray;
    }

    public void setAPCharArray(char[] aPCharArray) {
        this.aPCharArray = aPCharArray;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CM_OBJ");

        definition.addIdentityField("ID", Integer.class);

        definition.addField("A_PCHAR_A", Character[].class);

        definition.addField("A_PCHAR", Character.class);
        definition.addField("A_PINT", Integer.class);
        definition.addField("A_PFLOAT", Float.class);

        FieldDefinition field = new FieldDefinition("A_PBOOLEAN", Boolean.class);
        field.setShouldAllowNull(false);
        definition.addField(field);

        definition.addField("A_PLONG", Long.class);
        definition.addField("A_PDOUBLE", Double.class);
        definition.addField("A_PBYTE", Byte.class);

        // The A_PBYTE_A field will be added after a plaftorm check in 
        // ConversionManagerSystem.createTables()
        definition.addField("A_PSHORT", Short.class);

        definition.addField("A_CHAR", Character.class);
        definition.addField("AN_INTEGER", Integer.class);
        definition.addField("A_FLOAT", Float.class);

        FieldDefinition field2 = new FieldDefinition("A_BOOLEAN", Boolean.class);
        field2.setShouldAllowNull(false);
        definition.addField(field2);

        definition.addField("A_LONG", Long.class);
        definition.addField("A_DOUBLE", Double.class);
        definition.addField("A_BYTE", Byte.class);
        definition.addField("A_SHORT", Short.class);

        definition.addField("A_NUMBER", java.math.BigDecimal.class);
        definition.addField("A_BIGDEC", java.math.BigDecimal.class);
        definition.addField("A_BIGINT", java.math.BigInteger.class);

        definition.addField("AN_SQLDATE", java.sql.Date.class);
        definition.addField("A_JAVADATE", java.sql.Timestamp.class);
        definition.addField("A_TIME", java.sql.Time.class);
        definition.addField("A_TIMESTMP", java.sql.Timestamp.class);
        definition.addField("A_CALNDR", java.sql.Timestamp.class);

        definition.addField("A_STRING", String.class, 50);

        //	definition.addField("INT2CHR", Character.class);
        definition.addField("TIMESP2LNG", java.sql.Timestamp.class);
        definition.addField("DATE2TIMESTAMP", java.sql.Timestamp.class);
        definition.addField("INT2FLT", Float.class);
        definition.addField("INT2LNG", Long.class);
        definition.addField("INT2DBL", Double.class);
        definition.addField("INT2SHORT", Short.class);
        definition.addField("INT2STR", String.class);
        definition.addField("INT2BYTE", Byte.class);

        //	definition.addField("FLT2CHR", Character.class);
        definition.addField("FLT2INT", Integer.class);
        definition.addField("FLT2LNG", Long.class);
        definition.addField("FLT2DBL", Double.class);
        definition.addField("FLT2SHORT", Short.class);
        definition.addField("FLT2STR", String.class);
        definition.addField("FLT2BYTE", Byte.class);

        definition.addField("STR2INT", Integer.class);

        return definition;
    }

    public String toString() {
        return "ConversionDataObject(" + aPChar + ")";
    }
}
