/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.cobol;


/**
* <b>Purpose</b>:  This interface defines behavior for a field meta data be they elementary or
* composite.  This allows reference to either type field indiscriminatly.  It also defines some
* statics for use in the type attribute of field meta data.
*/
public interface FieldMetaData {
    //types
    int NUMERIC = 1;
    int ALPHA_NUMERIC = 2;
    int COMPOSITE = 3;
    int ALPHABETIC = 4;
    int BINARY = 5;
    int MANTISSA = 6;
    int PACKED_DECIMAL = 7;
    int VOID = 0;

    /** this method returns an new instance with the same values */
    FieldMetaData deepCopy();

    /** this method returns true if the field is signed */
    boolean isSigned();

    /** this method sets the signed attribute for the field */
    void setIsSigned(boolean signed);

    /** this method will return the name of the field */
    String getName();

    /** this method will set the name of the field */
    void setName(String newName);

    /** this method will return the associated record */
    RecordMetaData getRecord();

    /** this method will set the assoicated record */
    void setRecord(RecordMetaData newRecord);

    /** this method will return the size in bytes of the field */
    int getSize();

    /** this method will set the size of the field */
    void setSize(int size);

    /** this method returns true if the field is composite, false otherwise */
    boolean isComposite();

    /** this method returns the offset, in bytes, of the field in the record */
    int getOffset();

    /** this method sets the offset of the field */
    void setOffset(int offset);

    /** this method returns the type of the field */
    int getType();

    /** this method sets the type of the field */
    void setType(int type);

    /** this method sets whether this is a field redefine or not */
    boolean isFieldRedefine();

    /** this method sets the boolean for field redefine to true */
    void setIsFieldRedefine(boolean status);

    /** this method sets the redefined field */
    void setFieldRedefined(FieldMetaData field);

    /** this method returns the field that is redefined */
    FieldMetaData getFieldRedefined();

    /** this method returns true is the field has a preset decimal position */
    boolean hasDecimal();

    /** this method returns the decimal position index */
    int getDecimalPosition();

    /** this method sets the decimal position to the new index */
    void setDecimalPosition(int newPosition);

    /** this method returns true if this field is an array */
    boolean isArray();

    /** this method returns the array size */
    int getArraySize();

    /** this method sets the array size for the field */
    void setArraySize(int newSize);

    /** this method returns true if the array size depends on another field */
    boolean dependsOn();

    /** this method returns the name of the dependent field name */
    String getDependentFieldName();

    /** this method sets the dependent field name */
    void setDependentFieldName(String fieldName);

    /** this method will extract the field's value from a byte array */
    Object extractValueFromArray(byte[] recordData);

    /** this method will write itself on the give row extracting the data from the given
    byte array */
    void writeOnRow(CobolRow row, byte[] recordData);

    /** this method will write itself on the given byte array from the data in the row */
    void writeOnArray(CobolRow row, byte[] recordData);
}
