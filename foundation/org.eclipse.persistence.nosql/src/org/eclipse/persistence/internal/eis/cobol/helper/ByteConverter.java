/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.eis.cobol.helper;

import java.io.*;
import org.eclipse.persistence.internal.eis.cobol.*;

/**
* <b>Purpose</b>: This class handles all the byte <-> string conversions.  It handles
* ascii/binary/packed-decimal conversions.  This class is used by and dependent on <code>
* FieldMetaData</code>
*/
public class ByteConverter {

    /** This is the byte array that represents the byte data for the entire record */
    private byte[] myRecordData;

    /** This is the field that this particular <code>ByteConverter</code> is associatedd */
    FieldMetaData myFieldMetaData;

    /** This is the word size for the system, api is provided to change this if necessary */
    public static int wordSize = 2;

    /** This represents whether the system is little endian or not, api is provided to change
    * this if necessary. */
    private boolean isLittleEndian;

    /**
    * Default constructor
    */
    public ByteConverter() {
        initialize();
    }

    /**
    * constructor that accepts FieldMetaData and record data, this is the preferred constructor
    */
    public ByteConverter(FieldMetaData metaData, byte[] recordData) {
        initialize(metaData, recordData);
    }

    /**
    * no-argument initializer
    */
    protected void initialize() {
        isLittleEndian = false;
    }

    /**
    * accepts FieldMetaData and record data.
    */
    protected void initialize(FieldMetaData metaData, byte[] recordData) {
        myFieldMetaData = metaData;
        myRecordData = recordData;
        isLittleEndian = false;
    }

    /**
    * method returns littleEndian attribute
    */
    public boolean isLittleEndian() {
        return isLittleEndian;
    }

    /**
    * method sets littleEndian attribute
    */
    public void setIsLittleEndian(boolean newValue) {
        isLittleEndian = newValue;
    }

    /**
    * method returns wordSize attribute
    */
    public int getWordSize() {
        return wordSize;
    }

    /**
    * method sets wordSize attribute
    */
    public void setWordSize(int newWordSize) {
        wordSize = newWordSize;
    }

    /**
    * This method checks the field to see if it should have a decimal point added and inserts it
    * if needed.
    */
    protected String insertDecimalInString(String string) {
        if (getFieldMetaData().hasDecimal()) {
            int decimalPosition = this.getFieldMetaData().getDecimalPosition();
            if (decimalPosition < string.length()) {
                StringWriter writer = new StringWriter();
                writer.write(string.substring(0, decimalPosition - 1));
                writer.write(".");
                writer.write(string.substring(decimalPosition - 1));
                return writer.toString();
            }
        }
        return string;
    }

    /**
    * This method removes the decimal if needed
    */
    protected String removeDecimalInString(String string) {
        if (getFieldMetaData().hasDecimal()) {
            int decimalPosition = this.getFieldMetaData().getDecimalPosition();
            if (decimalPosition < string.length()) {
                StringWriter writer = new StringWriter();
                writer.write(string.substring(0, decimalPosition - 1));
                writer.write(string.substring(decimalPosition));
                return writer.toString();
            }
        }
        return string;
    }

    /**
    * This method is the primary public access for the byte converter to extract a string value
    * associated with a field, from a byte array associated with a record.
    */
    public String getStringValue() {
        int offset = this.getFieldMetaData().getOffset();
        String stringValue = null;
        if ((this.getFieldMetaData().getType() != FieldMetaData.BINARY) && (this.getFieldMetaData().getType() != FieldMetaData.MANTISSA) && (this.getFieldMetaData().getType() != FieldMetaData.PACKED_DECIMAL)) {
            int length = 0;
            while (length < this.getFieldMetaData().getSize()) {
                if (this.getRecordData()[offset + length] == 0) {
                    break;
                }
                length++;
            }
            stringValue = new String(this.getRecordData(), offset, length);
        } else if (this.getFieldMetaData().getType() == FieldMetaData.BINARY) {
            if (isLittleEndian()) {
                this.swapEndians(offset, this.getFieldMetaData().getSize());
            }
            stringValue = getStringFromBinaryData(offset, this.getFieldMetaData().getSize());
        } else if (this.getFieldMetaData().getType() == FieldMetaData.PACKED_DECIMAL) {
            stringValue = this.getStringValueFromPackedDecimal();
        } else {
            //todo: handle other types of data
            stringValue = null;
        }
        return insertDecimalInString(stringValue);
    }

    /**
    * This is the primary public access for writing string values associated with a field to a
    * byte array associated with a record.
    */
    public void setBytesToValue(String value) {
        value = removeDecimalInString(value);
        if ((this.getFieldMetaData().getType() != FieldMetaData.BINARY) && (this.getFieldMetaData().getType() != FieldMetaData.MANTISSA) && (this.getFieldMetaData().getType() != FieldMetaData.PACKED_DECIMAL)) {
            byte[] byteValue = value.getBytes();
            int length = byteValue.length;

            //assure length is within boundaries of the field
            if (length > this.getFieldMetaData().getSize()) {
                length = this.getFieldMetaData().getSize();
            }
            int i;
            int j;
            for (i = 0, j = this.getFieldMetaData().getOffset(); i < length; i++, j++) {
                this.getRecordData()[j] = byteValue[i];
            }

            //terminate the string
            if (length < this.getFieldMetaData().getSize()) {
                this.getRecordData()[j] = 0;
            }
        } else if (this.getFieldMetaData().getType() == FieldMetaData.BINARY) {
            this.setBinaryDataToStringValue(value, this.getFieldMetaData().getOffset(), this.getFieldMetaData().getSize());
        } else if (this.getFieldMetaData().getType() == FieldMetaData.PACKED_DECIMAL) {
            this.setByteArrayToPackedDecimalValue(value);
        } else {
            //todo: handle other types of data
        }
    }

    /**
    * getter for <code>FieldMetaData</code>
    */
    public FieldMetaData getFieldMetaData() {
        return myFieldMetaData;
    }

    /**
    * setter for <code>FieldMetaData</code>
    */
    public void setFieldMetaData(FieldMetaData newFieldMetaData) {
        myFieldMetaData = newFieldMetaData;
    }

    /**
    * getter for record data
    */
    public byte[] getRecordData() {
        return myRecordData;
    }

    /**
    * setter for record data
    */
    public void setRecordData(byte[] newRecordData) {
        myRecordData = newRecordData;
    }

    /**
    * this method builds a string value from a byte array containing packed-decimal data
    */
    protected String getStringValueFromPackedDecimal() {
        int mask = 0xf0;
        boolean signed = this.getFieldMetaData().isSigned();
        String sign = new String();
        int offset = this.getFieldMetaData().getOffset();
        int size = this.getFieldMetaData().getSize();
        String value = new String();
        int position = 0;

        //determine the sign if there is one
        if (signed) {
            byte signBits = (byte)(myRecordData[(offset + size) - 1] | mask);
            if (signBits == 0x0d) {
                sign = "-";
            } else if (signBits == 0x0c) {
                sign = "+";
            }
        }

        String stringValue;

        //build hex string
        for (int i = offset; i < (offset + size); i++) {
            stringValue = Integer.toHexString(Helper.intFromByte(myRecordData[i]));
            //added to handle strange behavior of toHexString method with negative numbers
            if (stringValue.length() > 2) {
                stringValue = stringValue.substring(stringValue.length() - 2);
            }
            value += stringValue;
        }

        //count leading zeros
        while (value.charAt(position) == '0') {
            position++;
        }

        //remove sign bits and leading zeros
        value = value.substring(position, value.length() - 1);

        if (signed) {
            return sign + value;
        } else {
            return value;
        }
    }

    /**
    * this method sets byte array data to a packed-decimal value held in the value argument
    */
    protected void setByteArrayToPackedDecimalValue(String value) {
        int size = this.getFieldMetaData().getSize();
        int offset = this.getFieldMetaData().getOffset();
        boolean signed = this.getFieldMetaData().isSigned();

        //determine the sign if there is one add it
        if (signed) {
            if (value.startsWith("-")) {
                value += "d";
            } else {
                value += "c";
            }
            value = value.substring(1);
        }
        //add F if unsigned
        else {
            value += "f";
        }

        //add leading zeros
        while (value.length() < (size * 2)) {
            value = "0" + value;
        }

        //write one byte at a time to array
        for (int i = offset, j = 0; i < (offset + size); i++, j += 2) {
            myRecordData[i] = Helper.byteFromInt(Helper.intFromHexString(value.substring(j, j + 2)));
        }
    }

    /**
    * this method will swap the ends of a byte in a byte array starting at <code>offset</code>
    * and ending at <code>end</code>
    */
    protected void swapEndians(int offset, int end) {
        if ((wordSize > 0) && ((wordSize % 2) == 0)) {
            int length = end - offset;
            int stop = end;

            //assure length is divisible by wordSize
            if ((length % wordSize) != 0) {
                int remainder = length % wordSize;
                stop = end - remainder;
            }
            int halfWord = wordSize / 2;
            byte highByte;
            byte lowByte;
            for (int i = offset; i < stop; i += wordSize) {
                for (int j = 0; j < halfWord; j++) {
                    highByte = myRecordData[i + j];
                    lowByte = myRecordData[i + j + halfWord];
                    myRecordData[i + j] = lowByte;
                    myRecordData[i + j + halfWord] = highByte;
                }
            }
        } else {
            throw ByteArrayException.unrecognizedDataType();
        }
    }

    /**
    * this method performs a bitwise twos complement on a section of a byte array starting at
    * <code>offset</code> and ending at <code>offset + length</code>
    */
    protected void twosComplement(int offset, int length) {
        int i;

        //perform a twos complement on the bytes in record data
        for (i = offset; i < (offset + length); i++) {
            myRecordData[i] = Helper.byteFromInt(~(Helper.intFromByte(myRecordData[i])));
        }
        i--;
        //add one to value
        while ((i >= offset) && (Helper.intFromByte(myRecordData[i]) == -1)) {
            myRecordData[i] += 1;
            i--;
        }
        if (i >= offset) {
            myRecordData[i] += 1;
        }
    }

    /**
    * This method builds a string value from a byte array section containing binary data
    */
    protected String getStringFromBinaryData(int offset, int size) {
        boolean signed = this.getFieldMetaData().isSigned();
        String sign = new String();
        int total = 0;

        //check if it is signed and determine sign
        if (signed) {
            int mask = 0xff;
            byte highestByte = myRecordData[offset];
            if (((highestByte >> 3) & mask) == 0xff) {
                sign = "-";
                twosComplement(offset, size);
            } else {
                sign = "+";
            }
        }

        //loop through bytes accumulating total
        for (int i = (offset + size) - 1, j = 0; i >= offset; i--, j++) {
            if (Helper.intFromByte(myRecordData[i]) < 0) {
                myRecordData[i] &= 0x7f;
                total += Helper.power(2, ((j + 1) * 8) - 1);
            }
            total += (Helper.intFromByte(myRecordData[i]) * Helper.power(2, j * 8));
        }
        if (signed) {
            return sign + String.valueOf(total);
        } else {
            return String.valueOf(total);
        }
    }

    /**
    * this method sets a section of byte array to the value represented by the string <code> value
    * </code> using a binary representation.
    */
    protected void setBinaryDataToStringValue(String value, int offset, int size) {
        boolean isNegative = false;

        //remove +/-
        if (value.startsWith("-")) {
            isNegative = true;
            value = value.substring(1);
        } else if (value.startsWith("+")) {
            value = value.substring(1);
        }
        int total = Helper.integerFromString(value).intValue();

        //loop through total dividing down
        for (int i = offset, j = size - 1; i < (size + offset); i++, j--) {
            myRecordData[i] = Helper.byteFromInt(total / Helper.power(2, j * 8));
            total %= Helper.power(2, j * 8);
        }
        if (isNegative) {
            twosComplement(offset, size);
        }
    }
}
