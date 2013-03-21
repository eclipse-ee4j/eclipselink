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
package org.eclipse.persistence.eis;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <p>An <code>EISMappedRecord</code> acts as a <code>Record</code> 
 * wrapper.  It allows database row field-value pair mappings to be used as a 
 * mapped record implementation.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISMappedRecord extends AbstractRecord {
    protected Map record;
    protected EISAccessor accessor;

    /**
     * Create a database row wrapper for the record.
     */
    public EISMappedRecord(Map record, EISAccessor accessor) {
        setRecord(record);
        setAccessor(accessor);
    }

    /**
     * INTERNAL:
     * getFields() is sued internally in a few places, so try to make that work for mapped records.
     */
    @Override
    public Vector getFields() {
        return new Vector(getRecord().keySet());
    }

    /**
     * INTERNAL:
     * getValues() is sued internally in a few places, so try to make that work for mapped records.
     */
    @Override
    public Vector getValues() {
        return new Vector(getRecord().values());
    }
    
    /**
     * Forward the request to the record.
     */
    public Set keySet() {
        Set keys = getRecord().keySet();

        // Handle bug in Attunity record.  It returns null for the keys.
        if (keys == null) {
            return new HashSet(1);
        }
        return keys;
    }

    /**
     * Forward the request to the record.
     */
    public Collection values() {
        return getRecord().values();
    }

    /**
     * Forward the request to the record.
     */
    public int size() {
        return getRecord().size();
    }
    
    /**
     * Return if the row is empty.
     * For some reason Attunity MappedRecords think they are empty when not,
     * so always return false.
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Forward the request to the record.
     */
    public void clear() {
        getRecord().clear();
    }

    /**
     * Forward the request to the record.
     */
    public Object get(String key) {
        return getRecord().get(key);
    }

    /**
     * Forward the request to the record.
     * Wrapped nested records and collections to SDKFieldValues.
     */
    public Object get(DatabaseField field) {
        Object value = get(field.getName());
        if (value instanceof Map) {
            Vector nestedRows = new Vector(1);
            nestedRows.add(new EISMappedRecord((Map)value, getAccessor()));
            value = nestedRows;
        } else if (value instanceof List) {
            List values = (List)value;
            Vector nestedValues = new Vector(values.size());
            for (int index = 0; index < values.size(); index++) {
                Object nestedValue = values.get(index);
                if (nestedValue instanceof Map) {
                    nestedValues.add(new EISMappedRecord((Map)values.get(index), getAccessor()));
                } else {
                    nestedValues.add(nestedValue);
                }
            }
            value = nestedValues;
        }
        return value;
    }
    
    /**
     * Check if the field is contained in the row.
     */
    public boolean containsKey(String fieldName) {
        return get(fieldName) != null;
    }

    /**
     * Check if the field is contained in the row.
     */
    public boolean containsKey(DatabaseField key) {
        return get(key) != null;
    }

    /**
     * Return the wrapped record.
     */
    public Map getRecord() {
        return record;
    }

    /**
     * Set the wrapped record.
     */
    public void setRecord(Map record) {
        this.record = record;
    }

    /**
     * Return the accessor.
     */
    public EISAccessor getAccessor() {
        return accessor;
    }

    /**
     * Set the accessor.
     */
    public void setAccessor(EISAccessor accessor) {
        this.accessor = accessor;
    }

    /**
     * Print all of the record keys and values.
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(Helper.getShortClassName(getClass()));
        writer.write("(");

        for (Iterator keysIterator = keySet().iterator(); keysIterator.hasNext();) {
            Object key = keysIterator.next();
            writer.write(Helper.cr());
            writer.write("\t");
            writer.write(String.valueOf(key));
            writer.write(" => ");
            writer.write(String.valueOf(get(key)));
        }
        writer.write(")");

        return writer.toString();
    }
}
