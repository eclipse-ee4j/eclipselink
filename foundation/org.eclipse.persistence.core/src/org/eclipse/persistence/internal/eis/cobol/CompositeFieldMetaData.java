/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

/**
* <b>Purpose</b>: This class represents metadata for composite fields.  It exetends
* <code>ElementaryFieldMetaData</code> and adds teh attribute <code>myCompositeFields</code>
* which is a collection of subordinate fields.
*/
public class CompositeFieldMetaData extends ElementaryFieldMetaData implements CompositeObject {

    /** collection containing this fields subordinate fields */
    protected Vector myCompositeFields;

    public CompositeFieldMetaData() {
        super.initialize();
        initialize();
    }

    public CompositeFieldMetaData(String fieldName, String recordName) {
        super.initialize(fieldName, recordName);
        initialize();
    }

    public CompositeFieldMetaData(String fieldName, RecordMetaData record) {
        super.initialize(fieldName, record);
        initialize();
    }

    public CompositeFieldMetaData(String fieldName, RecordMetaData record, Vector fields) {
        super.initialize(fieldName, record);
        initialize(fields);
    }

    protected void initialize() {
        myCompositeFields = new Vector();
    }

    protected void initialize(Vector fields) {
        myCompositeFields = fields;
    }

    /**
    * performs a deep copy of all subordiate fields in the <code>myCompositeFields</code> attribute
    */
    public FieldMetaData deepCopy() {
        CompositeFieldMetaData fieldCopy = new CompositeFieldMetaData(new String(myName), myRecord.getName());
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
        Enumeration fieldsEnum = myCompositeFields.elements();
        while (fieldsEnum.hasMoreElements()) {
            FieldMetaData field = (FieldMetaData)fieldsEnum.nextElement();
            fieldCopy.addField(field.deepCopy());
        }
        return fieldCopy;
    }

    /**
    * overides <code>ElementaryFieldMetaData</code> to calculate the fields size from all the sizes
    * of its subordinate fields.
    */
    public int getSize() {
        Enumeration fieldsEnum = myCompositeFields.elements();
        int size = 0;
        while (fieldsEnum.hasMoreElements()) {
            FieldMetaData field = (FieldMetaData)fieldsEnum.nextElement();
            if (!field.isFieldRedefine()) {
                size += field.getSize();
            }
        }

        /*if(isArray())
            return size * myArraySize;
        else*/
        return size;
    }

    /**
    * we don't want to set the size for a composite field because its size is determined from its
    * subordinate fields.
    */
    public void setSize(int size) {
        //do nothing 
    }

    /**
    * a composite field can't have a decimal position
    */
    public boolean hasDecimal() {
        return false;
    }

    /**
    * a composite field can't have a decimal position
    */
    public int getDecimalPosition() {
        return -1;
    }

    /**
    * a composite field can't have a decimal position
    */
    public void setDecimalPosition() {
        //do nothing
    }

    /**
    * a composite field is by definition going to be composite
    */
    public boolean isComposite() {
        return true;
    }

    /**
    * a composite field is by definition going to be composite
    */
    public int getType() {
        return FieldMetaData.COMPOSITE;
    }

    /**
    * a composite field is by definition going to be composite, so this cannot be changed
    */
    public void setType(int type) {
        //do nothing
    }

    /**
    * returns a collection of subordinate fields
    */
    public Vector getFields() {
        return myCompositeFields;
    }

    /**
    * sets the composite field attribute to the new collection
    */
    public void setFields(Vector newCompositeFields) {
        myCompositeFields = newCompositeFields;
    }

    /**
    * adds a field to the collection
    */
    public void addField(FieldMetaData newField) {
        myCompositeFields.addElement(newField);
    }

    /**
    * returns the first subordinate field with a name matching the string in <code>fieldName</code>
    */
    public FieldMetaData getFieldNamed(String fieldName) {
        Enumeration fieldsEnum = getFields().elements();
        while (fieldsEnum.hasMoreElements()) {
            FieldMetaData field = (FieldMetaData)fieldsEnum.nextElement();
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
    * loops through the subordinate fields extracting the value from each.
    */
    public Object extractValueFromArray(byte[] recordData) {
        ArrayList fieldValue = new ArrayList(getFields().size());
        if (this.isArray()) {
            int offset = this.getOffset();
            for (int i = this.getArraySize(); i > 0; i--) {
                //must change offset to read the appropriate section of the byte array
                CompositeFieldMetaData fieldCopy = (CompositeFieldMetaData)this.deepCopy();
                fieldCopy.setOffset(offset);
                fieldCopy.resetChildOffsets();
                CobolRow compositeRow = new CobolRow();
                fieldCopy.writeCompositeOnRow(compositeRow, recordData);
                fieldValue.add(compositeRow);
                offset += this.getSize();
            }
        } else {
            CobolRow compositeRow = new CobolRow();
            writeCompositeOnRow(compositeRow, recordData);
            fieldValue.add(compositeRow);
        }
        return fieldValue;
    }

    /**
    * writes individual fields on given row
    */
    public void writeCompositeOnRow(CobolRow row, byte[] recordData) {
        Enumeration fields = getFields().elements();
        while (fields.hasMoreElements()) {
            FieldMetaData currentField = (FieldMetaData)fields.nextElement();
            currentField.writeOnRow(row, recordData);
        }
    }

    /**
    * extracts the value from the record data for the field and writes it to the row.
    */
    public void writeOnRow(CobolRow row, byte[] recordData) {
        Object value;

        //check for array first adjust size if necessary
        if (this.isArray() && this.dependsOn()) {
            adjustArraySize(row);
        }
        DatabaseField field = new DatabaseField(getName(), getRecord().getName());
        if (this.isFieldRedefine()) {
            value = new CobolRedefinedFieldValue(this, recordData);
        } else {
            value = extractValueFromArray(recordData);
        }
        row.add(field, value);
    }

    /**
    * takes the value from the row for this field and writes it to the byte array
    */
    public void writeOnArray(CobolRow row, byte[] recordData) {
        Object obj = row.get(this.getName());
        List fieldValue = (List)obj;
        if (this.isArray()) {
            //check for array first adjust size if necessary
            if (this.dependsOn()) {
                adjustArraySize(row);
            }
            int offset = this.getOffset();
            Iterator elements = fieldValue.iterator();
            for (int i = this.getArraySize(); i > 0; i--) {
                //must change offset to write to the appropriate section of the byte array
                CompositeFieldMetaData fieldCopy = (CompositeFieldMetaData)this.deepCopy();
                fieldCopy.setOffset(offset);
                fieldCopy.resetChildOffsets();
                CobolRow compositeRow = (CobolRow)elements.next();
                fieldCopy.writeCompositeOnArray(compositeRow, recordData);
                offset += this.getSize();
            }
        } else {
            CobolRow compositeRow = (CobolRow)fieldValue.get(0);
            this.writeCompositeOnArray(compositeRow, recordData);
        }
    }

    /**
    * This method is used by fields that are array values to write themselves to arrays
    */
    protected void writeCompositeOnArray(CobolRow row, byte[] recordData) {
        Enumeration fields = getFields().elements();
        while (fields.hasMoreElements()) {
            FieldMetaData currentField = (FieldMetaData)fields.nextElement();
            currentField.writeOnArray(row, recordData);
        }
    }

    /**
    * method resets offsets of subordinate fields, should be called when the offset of the parent
    * is changed.
    */
    protected void resetChildOffsets() {
        Enumeration childFieldsEnum = myCompositeFields.elements();
        int offset = this.getOffset();
        while (childFieldsEnum.hasMoreElements()) {
            FieldMetaData field = (FieldMetaData)childFieldsEnum.nextElement();
            field.setOffset(offset);
            if (field.isComposite()) {
                ((CompositeFieldMetaData)field).resetChildOffsets();
            }
            offset += field.getSize();
        }
    }
}
