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

import java.io.*;
import java.util.*;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * <p>
 * <b>Purpose</b>: Define the abstract definition of a record for internal use.
 * Public API should reference the Map or Record interface.
 * Subclasses are DatabaseRecord and XMLRecord.
 * <p>
 * <b>Responsibilities</b>: <ul>
 *      <li> Implement the Record and Map interfaces.
 * </ul>
 * @see DatabaseField
 */
public abstract class AbstractRecord extends CoreAbstractRecord implements Record, Cloneable, Serializable, Map {

    /** Use vector to store the fields/values for optimal performance.*/
    protected Vector<DatabaseField> fields;

    /** Use vector to store the fields/values for optimal performance.*/
    protected Vector values;

    /** Optimize field creation for field name lookup. */
    protected DatabaseField lookupField;
    
    /** PERF: Cache the row size. */
    protected int size;

    /** INTERNAL: indicator showing that no entry exists for a given key. */
    public static final AbstractRecord.NoEntry noEntry = new AbstractRecord.NoEntry();
    
    /** INTERNAL: flag for any database field containing a null value */
    protected boolean nullValueInFields;

    /**
     * INTERNAL:
     * NoEntry: This is used to differentiate between the two kinds
     * of nulls: no entry exists, and the field is actually mapped
     * to null.
     */
    public static class NoEntry {
        private NoEntry() {
        }
    }

    /**
     * INTERNAL:
     *  converts JDBC results to collections of rows.
     */
    public AbstractRecord() {
        this.fields = new NonSynchronizedVector();
        this.values = new NonSynchronizedVector();
        this.size = 0;
        this.nullValueInFields = false;
    }

    /**
     * INTERNAL:
     *  converts JDBC results to collections of rows.
     */
    public AbstractRecord(int initialCapacity) {
        this.fields = new NonSynchronizedVector(initialCapacity);
        this.values = new NonSynchronizedVector(initialCapacity);
        this.size = 0;
        this.nullValueInFields = false;
    }

    /**
     * INTERNAL:
     *  converts JDBC results to collections of rows.
     */
    public AbstractRecord(Vector fields, Vector values) {
        this.fields = fields;
        this.values = values;
        this.nullValueInFields = false;
        resetSize();
    }

    /**
     * INTERNAL:
     *  converts JDBC results to collections of rows.
     */
    public AbstractRecord(Vector fields, Vector values, int size) {
        this.fields = fields;
        this.values = values;
        this.nullValueInFields = false;
        this.size = size;
    }
    
    /**
     * Reset the row size.
     * This must be reset after any change to the row.
     */
    protected void resetSize() {
        if (this.fields == null) {
            this.size = 0;
        } else {
            this.size = this.fields.size();
        }
    }

    /**
     * INTERNAL:
     * Add the field-value pair to the row.  Will not check,
     * will simply add to the end of the row
     */
    public void add(DatabaseField key, Object value) {
        this.fields.add(key);
        this.values.add(value);
        this.size++;
    }

    /**
     * PUBLIC:
     * Clear the contents of the row.
     */
    public void clear() {
        this.fields = new Vector();
        this.values = new Vector();
        resetSize();
    }

    /**
     * INTERNAL:
     * Clone the row and its values.
     */
    public AbstractRecord clone() {
        try {
            AbstractRecord clone = (AbstractRecord)super.clone();
            clone.fields = (Vector)this.fields.clone();
            clone.values = (Vector)this.values.clone();
            return clone;
        } catch (CloneNotSupportedException exception) {
            throw new InternalError();
        }
    }

    /**
     * PUBLIC:
     * Check if the value is contained in the row.
     */
    public boolean contains(Object value) {
        return containsValue(value);
    }

    /**
     * PUBLIC:
     * Check if the field is contained in the row.
     * Conform to hashtable interface.
     */
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return containsKey((String)key);
        }
        if (key instanceof DatabaseField) {
            return containsKey((DatabaseField)key);
        }

        return false;
    }

    /**
     * PUBLIC:
     * Check if the field is contained in the row.
     */
    public boolean containsKey(String fieldName) {
        return containsKey(getLookupField(fieldName));
    }

    /**
     * INTERNAL:
     * Check if the field is contained in the row.
     */
    public boolean containsKey(DatabaseField key) {
        // Optimize check.
        int index = key.index;
        if ((index >= 0) && (index < this.size)) {
            DatabaseField field = this.fields.get(index);
            if ((field == key) || field.equals(key)) {
                return true;
            }
        }
        return this.fields.contains(key);
    }

    /**
     * PUBLIC:
     * Check if the value is contained in the row.
     */
    public boolean containsValue(Object value) {
        return getValues().contains(value);
    }

    /**
     * PUBLIC:
     * Returns an Enumeration of the values.
     */
    public Enumeration elements() {
        return getValues().elements();
    }

    /**
     * PUBLIC:
     * Returns a set of the keys.
     */
    public Set entrySet() {
        return new EntrySet();
    }

    /**
     * PUBLIC:
     * Retrieve the value for the field name.
     * A field is constructed on the name to check the hash table.
     * If missing null is returned.
     */
    public Object get(Object key) {
        if (key instanceof String) {
            return get((String)key);
        } else if (key instanceof DatabaseField) {
            return get((DatabaseField)key);
        }
        return null;
    }

    /**
     * PUBLIC:
     * Retrieve the value for the field name.
     * A field is constructed on the name to check the hash table.
     * If missing null is returned.
     */
    public Object get(String fieldName) {
        return get(getLookupField(fieldName));
    }
    
    /**
     * Internal: factored out of getIndicatingNoEntry(String) to reduce complexity and have 
     * get(string) use get(DatabaseField) instead of getIndicatingNoEntry and then doing an extra check
     * @param fieldName
     * @return
     */
    protected DatabaseField getLookupField(String fieldName){
        if (this.lookupField == null) {
            this.lookupField = new DatabaseField(fieldName);
        } else {
            this.lookupField.resetQualifiedName(fieldName);
        }
        return this.lookupField;
    }

    /**
     * PUBLIC:
     * Retrieve the value for the field name.
     * A field is constructed on the name to check the hash table.
     * If missing DatabaseRow.noEntry is returned.
     */
    public Object getIndicatingNoEntry(String fieldName) {
        // Optimized the field creation.
        return getIndicatingNoEntry(getLookupField(fieldName));
    }

    /**
     * INTERNAL:
     * Retrieve the value for the field. If missing null is returned.
     */
    public Object get(DatabaseField key) {
        // PERF: Direct variable access.
        // ** Code duplicated in getIndicatingNoEntry, ensure kept in synch **
        // Optimize check.
        int index = key.index;
        if ((index >= 0) && (index < this.size)) {
            DatabaseField field = this.fields.get(index);
            if ((field == key) || field.equals(key)) {
                return this.values.get(index);
            }
        }
        int fieldsIndex = this.fields.indexOf(key);
        if (fieldsIndex >= 0) {
            // PERF: If the fields index was not set, then set it.
            if (index == -1) {
                key.setIndex(fieldsIndex);
            }
            return this.values.get(fieldsIndex);
        } else {
            return null;
        }
    }

    //----------------------------------------------------------------------------//
    public Object getValues(DatabaseField key) {
        return get(key);
    }

    public Object getValues(String key) {
        return get(key);
    }

    //----------------------------------------------------------------------------//

    /**
     * INTERNAL:
     * Retrieve the value for the field. If missing DatabaseRow.noEntry is returned.
     */
    public Object getIndicatingNoEntry(DatabaseField key) {
        // PERF: Direct variable access.
        // ** Code duplicated in get, ensure kept in synch **
        // Optimize check.
        int index = key.index;
        if ((index >= 0) && (index < this.size)) {
            DatabaseField field = this.fields.get(index);
            if ((field == key) || field.equals(key)) {
                return this.values.get(index);
            }
        }
        int fieldsIndex = this.fields.indexOf(key);
        if (fieldsIndex >= 0) {
            // PERF: If the fields index was not set, then set it.
            if (index == -1) {
                key.setIndex(fieldsIndex);
            }
            return this.values.get(fieldsIndex);
        } else {
            return AbstractRecord.noEntry;
        }
    }

    /**
     * INTERNAL:
     * Returns the row's field with the same name.
     */
    public DatabaseField getField(DatabaseField key) {
        // Optimize check.
        int index = key.index;
        if ((index >= 0) && (index < getFields().size())) {
            DatabaseField field = getFields().elementAt(index);
            if ((field == key) || field.equals(key)) {
                return field;
            }
        }
        for (index = 0; index < getFields().size(); index++) {
            DatabaseField field = getFields().elementAt(index);
            if ((field == key) || field.equals(key)) {
                return field;
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     */
    public Vector<DatabaseField> getFields() {
        return fields;
    }

    /**
     * INTERNAL:
     */
    public Vector getValues() {
        return values;
    }

    /**
     * PUBLIC:
     * Return if the row is empty.
     */
    public boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * INTERNAL:
     * Return true if the AbstractRecord has been marked as valid 
     * to check the update call cache with, false otherwise.
     */
    public boolean hasNullValueInFields() {
        return this.nullValueInFields;
    }

    /**
     * PUBLIC:
     * Returns an Enumeration of the DatabaseField objects.
     */
    public Enumeration keys() {
        return getFields().elements();
    }
    
    /**
     * PUBLIC:
     * Returns a set of the keys.
     */
    public Set keySet() {
        return new KeySet();
    }
    
    /**
     * Defines the virtual keySet.
     */
    protected class KeySet extends EntrySet {
        public Iterator iterator() {
            return new RecordKeyIterator();
        }
        public boolean contains(Object object) {
            return AbstractRecord.this.containsKey(object);
        }
        public boolean remove(Object object) {
            return AbstractRecord.this.remove(object) != null;
        }
    }
    
    /**
     * Defines the virtual valuesSet.
     */
    protected class ValuesSet extends EntrySet {
        public Iterator iterator() {
            return new RecordValuesIterator();
        }
        public boolean contains(Object object) {
            return AbstractRecord.this.contains(object);
        }
        public boolean remove(Object object) {
            int index = getValues().indexOf(object);
            if (index == -1) {
                return false;
            }
            AbstractRecord.this.remove(getFields().get(index));
            return true;
        }
    }
    
    /**
     * Defines the virtual entrySet.
     */
    protected class EntrySet extends AbstractSet {
        public Iterator iterator() {
            return new RecordEntryIterator();
        }
        public int size() {
            return AbstractRecord.this.size();
        }
        public boolean contains(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            }
            return AbstractRecord.this.containsKey(((Entry)object).getKey());
        }
        public boolean remove(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            }
            AbstractRecord.this.remove(((Entry)object).getKey());
            return true;
        }
        public void clear() {
            AbstractRecord.this.clear();
        }
    }
    
    /**
     * Defines the virtual entrySet iterator.
     */
    protected class RecordEntryIterator implements Iterator {
        int index;

        RecordEntryIterator() {
            this.index = 0;
        }

        public boolean hasNext() {
            return this.index < AbstractRecord.this.size();
        }

        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.index++;
            return new RecordEntry(getFields().get(this.index - 1), getValues().get(this.index - 1));
        }

        public void remove() {
            if (this.index >= AbstractRecord.this.size()) {
                throw new IllegalStateException();
            }
            AbstractRecord.this.remove(getFields().get(this.index));
        }
    }
    
    /**
     * Defines the virtual keySet iterator.
     */
    protected class RecordKeyIterator extends RecordEntryIterator {
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.index++;
            return getFields().get(this.index - 1);
        }
    }
    
    /**
     * Defines the virtual valuesSet iterator.
     */
    protected class RecordValuesIterator extends RecordEntryIterator {
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.index++;
            return getValues().get(this.index - 1);
        }
    }
    
    /**
     * Entry class for implementing Map interface.
     */
    protected static class RecordEntry implements Entry {
	Object key;
	Object value;

	public RecordEntry(Object key, Object value) {
	    this.key = key;
            this.value = value;
	}

	public Object getKey() {
	    return key;
	}

	public Object getValue() {
	    return value;
	}

	public Object setValue(Object value) {
	    Object oldValue = this.value;
	    this.value = value;
	    return oldValue;
	}

	public boolean equals(Object object) {
	    if (!(object instanceof Map.Entry)) {
		return false;
            }
	    Map.Entry entry = (Map.Entry)object;
	    return compare(key, entry.getKey()) && compare(value, entry.getValue());
	}

	public int hashCode() {
	    return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
	}

	public String toString() {
	    return key + "=" + value;
	}

        private boolean compare(Object object1, Object object2) {
            return (object1 == null ? object2 == null : object1.equals(object2));
        }
    }

    /**
     * INTERNAL:
     * Merge the provided row into this row.  Existing field values in this row will
     * be replaced with values from the provided row. Fields not in this row will be 
     * added from provided row.  Values not in provided row will remain in this row.
     */
    
    public void mergeFrom(AbstractRecord row){
        for (int index = 0; index < row.size(); ++index){
            this.put(row.getFields().get(index), row.getValues().get(index));
        }
    }
    
    /**
     * PUBLIC:
     * Add the field-value pair to the row.
     */
    public Object put(Object key, Object value) throws ValidationException {
        if (key instanceof String) {
            return put((String)key, value);
        } else if (key instanceof DatabaseField) {
            return put((DatabaseField)key, value);
        } else {
            throw ValidationException.onlyFieldsAreValidKeysForDatabaseRows();
        }
    }

    /**
     * PUBLIC:
     * Add the field-value pair to the row.
     */
    public Object put(String key, Object value) {
        return put(new DatabaseField(key), value);
    }

    /**
     * INTERNAL:
     * Add the field-value pair to the row.
     */
    public Object put(DatabaseField key, Object value) {
        int index = this.fields.indexOf(key);
        if (index >= 0) {
            return this.values.set(index, value);
        } else {
            add(key, value);
        }

        return null;
    }

    /**
     * PUBLIC:
     * Add all of the elements.
     */
    public void putAll(Map map) {
        Iterator entriesIterator = map.entrySet().iterator();
        while (entriesIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)entriesIterator.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * INTERNAL:
     * Remove the field key from the row.
     */
    public Object remove(Object key) {
        if (key instanceof String) {
            return remove((String)key);
        } else if (key instanceof DatabaseField) {
            return remove((DatabaseField)key);
        }
        return null;
    }

    /**
     * INTERNAL:
     * Remove the field key from the row.
     */
    public Object remove(String fieldName) {
        return remove(new DatabaseField(fieldName));
    }

    /**
     * INTERNAL:
     * Remove the field key from the row.
     */
    public Object remove(DatabaseField key) {
        int index = getFields().indexOf(key);
        if (index >= 0) {
            getFields().removeElementAt(index);
            Object value = getValues().elementAt(index);
            getValues().removeElementAt(index);
            resetSize();
            return value;
        }
        return null;
    }

    /**
     * INTERNAL:
     * replaces the value at index with value
     */
    public void replaceAt(Object value, int index) {
        this.values.set(index, value);
    }

    protected void setFields(Vector fields) {
        this.fields = fields;
        resetSize();
    }

    /**
     * INTERNAL:
     * Set the validForUpdateCallCacheCheck attribute to true if the row
     * does not contain nulls, false otherwise
     */
    public void setNullValueInFields(boolean nullValueInFields) {
        this.nullValueInFields = nullValueInFields;
    }
    
    protected void setValues(Vector values) {
        this.values = values;
    }

    /**
     * PUBLIC:
     * Return the number of field/value pairs in the row.
     */
    public int size() {
        return this.fields.size();
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(Helper.getShortClassName(getClass()));
        writer.write("(");

        for (int index = 0; index < getFields().size(); index++) {
            writer.write(Helper.cr());
            writer.write("\t");
            writer.write(String.valueOf((getFields().elementAt(index))));
            writer.write(" => ");
            writer.write(String.valueOf((getValues().elementAt(index))));
        }
        writer.write(")");

        return writer.toString();
    }

    /**
     * PUBLIC:
     * Returns an collection of the values.
     */
    public Collection values() {
        return new ValuesSet();
    }
}
