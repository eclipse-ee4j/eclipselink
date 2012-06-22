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
package org.eclipse.persistence.internal.eis.cobol;


/**
* <b>Purpose</b>:  This interface defines behavior for a field meta data be they elementary or
* composite.  This allows reference to either type field indiscriminatly.  It also defines some
* statics for use in the type attribute of field meta data.
*/
public interface FieldMetaData {
    //types
    public static final int NUMERIC = 1;
    public static final int ALPHA_NUMERIC = 2;
    public static final int COMPOSITE = 3;
    public static final int ALPHABETIC = 4;
    public static final int BINARY = 5;
    public static final int MANTISSA = 6;
    public static final int PACKED_DECIMAL = 7;
    public static final int VOID = 0;

    /** this method returns an new instance with the same values */
    public FieldMetaData deepCopy();

    /** this method returns true if the field is signed */
    public boolean isSigned();

    /** this method sets the signed attribute for the field */
    public void setIsSigned(boolean signed);

    /** this method will return the name of the field */
    public String getName();

    /** this method will set the name of the field */
    public void setName(String newName);

    /** this method will return the associated record */
    public RecordMetaData getRecord();

    /** this method will set the assoicated record */
    public void setRecord(RecordMetaData newRecord);

    /** this method will return the size in bytes of the field */
    public int getSize();

    /** this method will set the size of the field */
    public void setSize(int size);

    /** this method returns true if the field is composite, false otherwise */
    public boolean isComposite();

    /** this method returns the offset, in bytes, of the field in the record */
    public int getOffset();

    /** this method sets the offset of the field */
    public void setOffset(int offset);

    /** this method returns the type of the field */
    public int getType();

    /** this method sets the type of the field */
    public void setType(int type);

    /** this method sets whether this is a field redefine or not */
    public boolean isFieldRedefine();

    /** this method sets the boolean for field redefine to true */
    public void setIsFieldRedefine(boolean status);

    /** this method sets the redefined field */
    public void setFieldRedefined(FieldMetaData field);

    /** this method returns the field that is redefined */
    public FieldMetaData getFieldRedefined();

    /** this method returns true is the field has a preset decimal position */
    public boolean hasDecimal();

    /** this method returns the decimal position index */
    public int getDecimalPosition();

    /** this method sets the decimal position to the new index */
    public void setDecimalPosition(int newPosition);

    /** this method returns true if this field is an array */
    public boolean isArray();

    /** this method returns the array size */
    public int getArraySize();

    /** this method sets the array size for the field */
    public void setArraySize(int newSize);

    /** this method returns true if the array size depends on another field */
    public boolean dependsOn();

    /** this method returns the name of the dependent field name */
    public String getDependentFieldName();

    /** this method sets the dependent field name */
    public void setDependentFieldName(String fieldName);

    /** this method will extract the field's value from a byte array */
    public Object extractValueFromArray(byte[] recordData);

    /** this method will write itself on the give row extracting the data from the given
    byte array */
    public void writeOnRow(CobolRow row, byte[] recordData);

    /** this method will write itself on the given byte array from the data in the row */
    public void writeOnArray(CobolRow row, byte[] recordData);
}
