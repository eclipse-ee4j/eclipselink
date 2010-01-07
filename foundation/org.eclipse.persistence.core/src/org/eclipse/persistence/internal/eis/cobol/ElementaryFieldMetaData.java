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
package org.eclipse.persistence.internal.eis.cobol;

import java.util.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.eis.cobol.helper.*;

/**
* <b>Purpose</b>: This class defines the <code>FieldMetaData</code> interface it defines
* various behavior associated with a field and stores meta-information about the field.
* <code>ElementaryFieldMetaData</code> represents a field that contains a direct value and
* no subordinate fields, a root in the hierarchy.
*/
public class ElementaryFieldMetaData implements FieldMetaData {

    /** This is the record that contains this field */
    protected RecordMetaData myRecord;

    /** This is the name of the field */
    protected String myName;

    /** This is the size of the field */
    protected int mySize;

    /** This is the offset of the field within the record byte array */
    protected int myOffset;

    /** This is the type of data the field contains */
    protected int myType;

    /** This is true if this field is redefined */
    protected boolean isRedefine;

    /** This is the offset of the decimal position within the digits of a numeric field */
    protected int decimalPosition;

    /** This is the field that this field redefines */
    protected FieldMetaData myFieldRedefined;

    /** This is the number of elements in the array if this field is an array type */
    protected int myArraySize;

    /** This is populated if the arraySize is dependent on another field */
    protected String myDependentFieldName;

    /** This is true if the field is a numeric field that is signed */
    protected boolean isSigned;

    public ElementaryFieldMetaData() {
        initialize();
    }

    public ElementaryFieldMetaData(String fieldName, String recordName) {
        initialize(fieldName, recordName);
    }

    public ElementaryFieldMetaData(String fieldName, RecordMetaData record) {
        initialize(fieldName, record);
    }

    protected void initialize() {
        myRecord = new RecordMetaData();
        isRedefine = false;
        decimalPosition = -1;
        myArraySize = -1;
        isSigned = false;
        myDependentFieldName = "";
    }

    protected void initialize(String fieldName, String recordName) {
        myName = fieldName;
        myRecord = new RecordMetaData(recordName);
        isRedefine = false;
        decimalPosition = -1;
        myArraySize = -1;
        isSigned = false;
        myDependentFieldName = "";
    }

    protected void initialize(String fieldName, RecordMetaData record) {
        myName = fieldName;
        myRecord = record;
        isRedefine = false;
        decimalPosition = -1;
        myArraySize = -1;
        isSigned = false;
        myDependentFieldName = "";
    }

    /**
    * performs a deep copy of all the attributes of the field
    */
    public FieldMetaData deepCopy() {
        FieldMetaData fieldCopy = new ElementaryFieldMetaData(new String(myName), myRecord.getName());
        fieldCopy.setIsFieldRedefine(isRedefine);
        fieldCopy.setDecimalPosition(decimalPosition);
        fieldCopy.setArraySize(myArraySize);
        fieldCopy.setSize(mySize);
        fieldCopy.setOffset(myOffset);
        fieldCopy.setType(myType);
        if (isFieldRedefine()) {
            fieldCopy.setFieldRedefined(myFieldRedefined.deepCopy());
        }
        fieldCopy.setDependentFieldName(new String(myDependentFieldName));
        fieldCopy.setIsSigned(isSigned);
        return fieldCopy;
    }

    /**
    * returns true if field is a signed numeric field
    */
    public boolean isSigned() {
        return isSigned;
    }

    /**
    * setter for isSigned
    */
    public void setIsSigned(boolean signed) {
        isSigned = signed;
    }

    /**
    * returns true if the field is a numeric field with a decimal marker
    */
    public boolean hasDecimal() {
        if (decimalPosition == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
    * getter for decimalPosition
    */
    public int getDecimalPosition() {
        return decimalPosition;
    }

    /**
    * setter for decimalPosition
    */
    public void setDecimalPosition(int newPosition) {
        decimalPosition = newPosition;
    }

    /**
    * getter for myName
    */
    public String getName() {
        return myName;
    }

    /**
    * setter for myName
    */
    public void setName(String newName) {
        myName = newName;
    }

    /**
    * getter for myRecord
    */
    public RecordMetaData getRecord() {
        return myRecord;
    }

    /**
    * setter for myRecord
    */
    public void setRecord(RecordMetaData newRecord) {
        myRecord = newRecord;
    }

    /**
    * getter for mySize, multiplies field size my array size if field is an array to get
    * total size
    */
    public int getSize() {

        /*if(this.isArray())
            return mySize * myArraySize;
        else*/
        return mySize;
    }

    /**
    * setter for mySize
    */
    public void setSize(int size) {
        mySize = size;
    }

    /**
    * returns true if field is composite, since this is elementary, it always returns false
    */
    public boolean isComposite() {
        return false;
    }

    /**
    * returns an empty vector since an elementary field should have no subordinate fields
    */
    public Vector getCompositeFields() {
        return new Vector();
    }

    /**
    * getter for myOffset
    */
    public int getOffset() {
        return myOffset;
    }

    /**
    * setter for myOffset
    */
    public void setOffset(int offset) {
        myOffset = offset;
    }

    /**
    * getter for myType
    */
    public int getType() {
        return myType;
    }

    /**
    * setter for myType
    */
    public void setType(int type) {
        myType = type;
    }

    /**
    * returns true if field is a redefine
    */
    public boolean isFieldRedefine() {
        return isRedefine;
    }

    /**
    * setter for isRedefine
    */
    public void setIsFieldRedefine(boolean status) {
        isRedefine = status;
    }

    /**
    * setter for myFieldRedefined
    */
    public void setFieldRedefined(FieldMetaData field) {
        myFieldRedefined = field;
        if (field != null) {
            isRedefine = true;
        } else {
            isRedefine = false;
        }
    }

    /**
    * getter for myFieldRedefined
    */
    public FieldMetaData getFieldRedefined() {
        return myFieldRedefined;
    }

    /**
    * returns true if field is an array
    */
    public boolean isArray() {
        return (myArraySize > 0);
    }

    /**
    * getter for array size
    */
    public int getArraySize() {
        return myArraySize;
    }

    /**
    * setter for myArraySize
    */
    public void setArraySize(int newSize) {
        myArraySize = newSize;
    }

    /**
    * returns true if this field has a dependent field
    */
    public boolean dependsOn() {
        return (myDependentFieldName != "");
    }

    /**
    * getter for myDependentFieldName
    */
    public String getDependentFieldName() {
        return myDependentFieldName;
    }

    /**
    * setter for myDependentFieldName
    */
    public void setDependentFieldName(String fieldName) {
        myDependentFieldName = fieldName;
    }

    /**
    * this method extracts and returns the value for the field, if the field is an
    * array, the value is an <code>Vector</code>
    */
    public Object extractValueFromArray(byte[] recordData) {
        Object value;
        if (this.isArray()) {
            int offset = this.getOffset();
            ArrayList fieldValue = new ArrayList(this.getArraySize());
            for (int i = this.getArraySize(); i > 0; i--) {
                FieldMetaData fieldCopy = this.deepCopy();
                fieldCopy.setOffset(offset);
                ByteConverter converter = new ByteConverter(fieldCopy, recordData);
                fieldValue.add(converter.getStringValue());
                offset += mySize;
            }
            value = fieldValue;
        } else {
            ByteConverter converter = new ByteConverter(this, recordData);
            value = converter.getStringValue();
        }
        return value;
    }

    /**
    * this method adjusts the array size to the value in its dependent field
    */
    protected void adjustArraySize(CobolRow row) {
        Integer intValue = Helper.integerFromString(row.get(this.getDependentFieldName()).toString());
        if (intValue != null) {
            this.setArraySize(intValue.intValue());
        }
    }

    /**
    * this method will write its value to the row provided
    */
    public void writeOnRow(CobolRow row, byte[] recordData) {
        DatabaseField field = new DatabaseField(this.getName(), this.getRecord().getName());
        Object value;

        //check for array first adjust size if necessary
        if (this.isArray() && this.dependsOn()) {
            adjustArraySize(row);
        }
        if (!this.isFieldRedefine()) {
            value = extractValueFromArray(recordData);
        }
        //redefined field handle accordingly
        else {
            value = new CobolRedefinedFieldValue(this, recordData);
        }
        row.add(field, value);

    }

    /**
    * this method will write its value from the row to the record data byte array
    */
    public void writeOnArray(CobolRow row, byte[] recordData) {
        Object value = row.get(this.getName());
        ByteConverter converter;
        if (this.isArray()) {
            //check for array first adjust size if necessary
            if (this.dependsOn()) {
                adjustArraySize(row);
            }
            int offset = this.getOffset();
            Iterator elements = ((List)value).iterator();
            for (int i = this.getArraySize(); i > 0; i--) {
                FieldMetaData fieldCopy = this.deepCopy();
                fieldCopy.setOffset(offset);
                converter = new ByteConverter(fieldCopy, recordData);
                String elementValue = (String)elements.next();
                converter.setBytesToValue(elementValue);
                offset += mySize;
            }
        } else {
            converter = new ByteConverter(this, recordData);
            converter.setBytesToValue((String)value);
        }
    }
}
