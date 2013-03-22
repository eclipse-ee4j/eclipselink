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
 *     James Sutherland - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.util.*;
import java.util.Map.Entry;

/**
 * Properties subclass that removes the synchronization.
 */
public class NonSynchronizedProperties extends Properties {

    protected Map<Object, Object> values;

    public NonSynchronizedProperties(int size) {
        super();
        this.values = new HashMap(size);
    }
    
    @Override
    public void clear() {
        this.values.clear();
    }

    @Override
    public Object clone() {
        NonSynchronizedProperties properties = new NonSynchronizedProperties(size());
        properties.putAll(this);
        return properties;
        
    }

    @Override
    public boolean contains(Object value) {
        return this.values.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.values.containsValue(value);
    }

    @Override
    public Enumeration<Object> elements() {
        return new Hashtable(this.values).elements();
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return this.values.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.values.equals(o);
    }

    @Override
    public Object get(Object key) {
        return this.values.get(key);
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public Enumeration<Object> keys() {
        return new Hashtable(this.values).keys();
    }

    @Override
    public Set<Object> keySet() {
        return this.values.keySet();
    }

    @Override
    public Object put(Object key, Object value) {
        // Properties cannot store null.
        if (value == null) {
            return this.values.remove(key);
        }
        return this.values.put(key, value);
    }

    @Override
    public void putAll(Map<? extends Object, ? extends Object> t) {
        this.values.putAll(t);
    }

    @Override
    public Object remove(Object key) {
        return this.values.remove(key);
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public String toString() {
        return this.values.toString();
    }

    @Override
    public Collection<Object> values() {
        return this.values.values();
    }

    @Override
    public Object setProperty(String key, String value) {
        return this.values.put(key, value);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }
    @Override
    public String getProperty(String key) {
        Object oval = get(key);
        String sval = (oval instanceof String) ? (String)oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}

