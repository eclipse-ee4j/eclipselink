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
package org.eclipse.persistence.sessions;

import java.util.*;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <p>
 * <b>Purpose</b>: Define a representation of a database row as field=>value pairs.
 * This is the database row implementation class, the Record or java.util.Map interfaces
 * should be used to access this class instead of the implementation class.
 * <p>
 * <b>Responsibilities</b>: <ul>
 *        <li> Implement the common hashtable collection protocol.
 *        <li> Allow get and put on the field or field name.
 * </ul>
 * @see DatabaseField
 * @see Record
 * @see java.util.Map
 */
public class DatabaseRecord extends AbstractRecord {

    /**
     * INTERNAL:
     * Returns a record (of default size).
     */
    public DatabaseRecord() {
        super();
    }

    /**
     * INTERNAL:
     * Returns a record of the given initial capacity.
     * @param initialCapacity 
     */
    public DatabaseRecord(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * INTERNAL:
     * Builds row from database result fields and values.
     * Note: the entire database result will share the same fields vector.
     * @param fields Vector of fields
     * @param values Vector of values
     */
    public DatabaseRecord(Vector fields, Vector values) {
        super(fields, values);
    }

    /**
     * PUBLIC:
     * Clears the contents of the database row, both fields and values.
     */
    public void clear() {
        super.clear();
    }

    /**
     * PUBLIC:
     * Checks if the given Object value is contained in the values held 
     * in the database row.
     * @param value the Object to be considered
     * @return boolean - true if the Object value is in the row.
     */
    public boolean contains(Object value) {
        return super.containsValue(value);
    }

    /**
     * PUBLIC:
     * Checks if a key (ie. the field) is contained in the database row.
     * Conforms to a hashtable interface.
     * @param key an Object, either String or DatabaseField
     * @return boolean - true if the row with the corresponding key is in the row.
     */
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    /**
     * PUBLIC:
     * Checks if a given field is contained in the database row.
     * @param key String, the DatabaseField name
     * @return boolean - true if the row contains the key with the corresponding fieldName.
     */
    public boolean containsKey(String fieldName) {
        return super.containsKey(fieldName);
    }

    /**
     * PUBLIC:
     * Checks if the given Object value is contained in the values held 
     * in the database row.
     * @param value the Object under consideration
     * @return boolean - true if the row contains the Object as a value
     */
    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    /**
     * PUBLIC:
     * Returns an Enumeration of the values in the database row.
     * @return Enumeration 
     */
    public Enumeration elements() {
        return super.elements();
    }

    /**
     * PUBLIC:
     * Returns a set of map entries (ie. field-value pairs)in the database row
     * with the DatabaseFields as keys and the value Objects as values.
     * @see java.util.Map#entrySet()
     * @return Set - the set of all the field-value entries (see java.util.Map.Entry)
     */
    public Set entrySet() {
        return super.entrySet();
    }

    /**
     * PUBLIC:
     * Retrieves the value for the given key.
     * A field is constructed with the key to check the hash table.
     * If missing, null is returned.
     * @param key Object, either String or DatabaseField
     * @return Object
     */
    public Object get(Object key) {
    	return super.get(key);
    }

    /**
     * PUBLIC:
     * Retrieves the value with the given name of the DatabaseField.
     * A field is constructed on the name to check the hash table.
     * If missing, null is returned.
     * @param fieldName String, the DatabaseField name
     * @return Object - the value
     */
    public Object get(String fieldName) {
       return super.get(fieldName);
    }

    /**
     * PUBLIC:
     * Retrieves the value with the given field name.
     * A field is constructed on the name to check the hash table.
     * If missing, DatabaseRow.noEntry is returned.
     * @param fieldName String, the DatabaseField name
     * @return Object - the value
     */
    public Object getIndicatingNoEntry(String fieldName) {
       return super.getIndicatingNoEntry(fieldName);
    }
    
    /**
     * PUBLIC:
     * Returns the Object associated with the given key 
     * (null if the key does not map to an Object.)
     * @param key DatabaseField
     * @return Object - the value associated with the key
     */
    public Object getValues(DatabaseField key) {
        return super.get(key);
    }

    /**
     * PUBLIC:
     * Returns the Object associated with the given key 
     * (null if the key does not map to an Object.)
     * @param key String
     * @return Object - the value associated with the key
     */
    public Object getValues(String key) {
        return super.get(key);
    }

    /**
     * PUBLIC:
     * Checks if the database row is empty (ie. there are no field-value pairs.)
     * @return boolean - true if the database row is empty
     */
    public boolean isEmpty() {
        return super.isEmpty();
    }

    /**
     * PUBLIC:
     * Returns an Enumeration of the DatabaseField Objects.
     * @return Enumeration
     */
    public Enumeration keys() {
        return super.keys();
    }

    /**
     * PUBLIC:
     * Returns a set of the keys, the DatabaseField Objects, for the database row.
     * @return Set of the keys
     */
    public Set keySet() {
        return super.keySet();
    }
    
    /**
     * PUBLIC:
     * Adds a field-value pair to the row.
     * @param key Object, either String or DatabaseField
     * @param value Object
     * @return Object - the previous Object with that key, could be null
     * @throws ValidationException if inappropriate key is used
     */
    public Object put(Object key, Object value) throws ValidationException {
    	return super.put(key, value);
    }

    /**
     * PUBLIC:
     * Adds a field-value pair to the row.
     * @param key String
     * @param value Object
     * @return Object - the previous Object with that key, could be null
     */
    public Object put(String key, Object value) {
        return super.put(key, value);
    }

    /**
     * PUBLIC:
     * Adds all of the elements in the given map to the database row.
     * @param map Map of all the field-value elements to be added
     */
    public void putAll(Map map){
    	super.putAll(map);
    }

    /**
     * PUBLIC:
     * Returns the number of field-value pairs in the database row.
     * @return int
     */
    public int size() {
        return super.size();
    }

    /**
     * PUBLIC:
     * Returns a collection of the values held in the database row.
     * @return Collection of value Objects
     */
    public Collection values() {
        return super.values();
    }
}
