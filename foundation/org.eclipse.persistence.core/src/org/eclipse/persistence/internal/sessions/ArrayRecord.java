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
package org.eclipse.persistence.internal.sessions;

import java.io.StringWriter;
import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * PERF: Optimized record implementation using arrays instead of Vector.
 * Currently only used when fetch rows from the database.
 */
public class ArrayRecord extends DatabaseRecord {
    protected DatabaseField[] fieldsArray;
    protected Object[] valuesArray;

    protected ArrayRecord() {
        super();
    }
    
    public ArrayRecord(Vector fields, DatabaseField[] fieldsArray, Object[] valuesArray) {
        super(fields, null, fieldsArray.length);
        this.fieldsArray = fieldsArray;
        this.valuesArray = valuesArray;
    }
    
    /**
     * Reset the fields and values from the arrays.
     * This removes the optimization if a non-optimized method is called.
     */
    protected void checkValues() {
        if (this.values == null) {
            this.values = new NonSynchronizedVector(this.valuesArray.length);
            for (Object value : this.valuesArray) {
                this.values.add(value);
            }
        }
    }

    /**
     * INTERNAL:
     * Add the field-value pair to the row.  Will not check,
     * will simply add to the end of the row
     */
    public void add(DatabaseField key, Object value) {
        checkValues();
        this.fieldsArray = null;
        this.valuesArray = null;
        super.add(key, value);
    }

    /**
     * PUBLIC:
     * Clear the contents of the row.
     */
    public void clear() {
        this.fieldsArray = null;
        this.valuesArray = null;
        super.clear();
    }

    /**
     * INTERNAL:
     * Clone the row and its values.
     */
    public AbstractRecord clone() {
        checkValues();
        return super.clone();
    }

    /**
     * INTERNAL:
     * Check if the field is contained in the row.
     */
    public boolean containsKey(DatabaseField key) {
        if (this.fieldsArray != null) {
            // Optimize check.
            int index = key.index;
            if ((index >= 0) && (index < this.size)) {
                DatabaseField field = this.fieldsArray[index];
                if ((field == key) || field.equals(key)) {
                    return true;
                }
            }
            for (DatabaseField field : this.fieldsArray) {
                if ((field == key) || field.equals(key)) {
                    return true;
                } 
            }
            return false;
        } else {
            return super.containsKey(key);
        }
    }

    /**
     * PUBLIC:
     * Check if the value is contained in the row.
     */
    public boolean containsValue(Object value) {
        if (this.valuesArray != null) {
            for (Object rowValue : this.valuesArray) {
                if ((value == rowValue) || rowValue.equals(value)) {
                    return true;
                } 
            }
            return false;
        } else {
            return super.containsValue(value);
        }
    }

    /**
     * INTERNAL:
     * Retrieve the value for the field. If missing null is returned.
     */
    public Object get(DatabaseField key) {
        if (this.fieldsArray != null) {
            // Optimize check.
            int index = key.index;
            if ((index >= 0) && (index < this.size)) {
                DatabaseField field = this.fieldsArray[index];
                if ((field == key) || field.equals(key)) {
                    return this.valuesArray[index];
                }
            }
            for (int fieldIndex = 0; fieldIndex < this.size; fieldIndex++) {
                DatabaseField field = this.fieldsArray[fieldIndex];
                if ((field == key) || field.equals(key)) {
                    // PERF: If the fields index was not set, then set it.
                    if (index == -1) {
                        key.setIndex(fieldIndex);
                    }
                    return this.valuesArray[fieldIndex];
                }
            }
            return null;
        } else {
            return super.get(key);
        }
    }
    
    /**
     * INTERNAL:
     * Retrieve the value for the field. If missing DatabaseRow.noEntry is returned.
     */
    public Object getIndicatingNoEntry(DatabaseField key) {
        if (this.fieldsArray != null) {
            // Optimize check.
            int index = key.index;
            if ((index >= 0) && (index < this.size)) {
                DatabaseField field = this.fieldsArray[index];
                if ((field == key) || field.equals(key)) {
                    return this.valuesArray[index];
                }
            }
            for (int fieldIndex = 0; fieldIndex < this.size; fieldIndex++) {
                DatabaseField field = this.fieldsArray[fieldIndex];
                if ((field == key) || field.equals(key)) {
                    // PERF: If the fields index was not set, then set it.
                    if (index == -1) {
                        key.setIndex(fieldIndex);
                    }
                    return this.valuesArray[fieldIndex];
                }
            }
            return AbstractRecord.noEntry;
        } else {
            return super.get(key);
        }
    }

    /**
     * INTERNAL:
     * Returns the row's field with the same name.
     */
    public DatabaseField getField(DatabaseField key) {
        if (this.fieldsArray != null) {
            // Optimize check.
            int index = key.index;
            if ((index >= 0) && (index < this.size)) {
                DatabaseField field = this.fieldsArray[index];
                if ((field == key) || field.equals(key)) {
                    return field;
                }
            }
            for (int fieldIndex = 0; fieldIndex < this.size; fieldIndex++) {
                DatabaseField field = this.fieldsArray[fieldIndex];
                if ((field == key) || field.equals(key)) {
                    return field;
                }
            }
            return null;
        } else {
            return super.getField(key);
        }
    }

    /**
     * INTERNAL:
     */
    public Vector getFields() {
        checkValues();
        return super.getFields();
    }

    /**
     * INTERNAL:
     */
    public Vector getValues() {
        checkValues();
        return super.getValues();
    }

    /**
     * INTERNAL:
     * Add the field-value pair to the row.
     */
    public Object put(DatabaseField key, Object value) {
        checkValues();
        this.fieldsArray = null;
        this.valuesArray = null;
        return super.put(key, value);
    }

    /**
     * INTERNAL:
     * Remove the field key from the row.
     */
    public Object remove(DatabaseField key) {
        checkValues();
        this.fieldsArray = null;
        this.valuesArray = null;
        return super.remove(key);
    }

    /**
     * INTERNAL:
     * replaces the value at index with value
     */
    public void replaceAt(Object value, int index) {
        if (this.valuesArray != null) {
            this.valuesArray[index] = value;
        } else {
            super.replaceAt(value, index);
        }
    }

    protected void setFields(Vector fields) {
        checkValues();
        this.fieldsArray = null;
        this.valuesArray = null;
        super.setFields(fields);
    }
    
    protected void setValues(Vector values) {
        checkValues();
        this.fieldsArray = null;
        this.valuesArray = null;
        super.setValues(values);
    }

    /**
     * PUBLIC:
     * Return the number of field/value pairs in the row.
     */
    public int size() {
        if (this.fieldsArray == null) {
            return this.fields.size();
        } else {
            return this.fieldsArray.length;            
        }
    }

    @Override
    public String toString() {
        if (this.valuesArray != null) {
            StringWriter writer = new StringWriter();
            writer.write(Helper.getShortClassName(getClass()));
            writer.write("(");
    
            for (int index = 0; index < this.fieldsArray.length; index++) {
                writer.write(Helper.cr());
                writer.write("\t");
                writer.write(String.valueOf(this.fieldsArray[index]));
                writer.write(" => ");
                writer.write(String.valueOf(this.valuesArray[index]));
            }
            writer.write(")");
    
            return writer.toString();
        } else {
            return super.toString();
        }
    }
}
