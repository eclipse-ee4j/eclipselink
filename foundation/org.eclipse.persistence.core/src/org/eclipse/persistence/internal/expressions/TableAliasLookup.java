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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.Map;

import org.eclipse.persistence.internal.helper.*;

/**
 * INTERNAL:
 * Represents the aliased tables an ObjectExpression will be translated to,
 * along with any of its derived TableExpressions.
 * For bug 2778339 / CR 2456, this Lookup also represents identity.  Two
 * expressions with the same Lookup will be translated to the same table(s).
 */
public class TableAliasLookup implements Serializable {// CR#3718, implements Serializable
    protected DatabaseTable[] keys;
    protected DatabaseTable[] values;
    protected int lastUsed;

    /* Have these aliases already been added to a FROM clause? */
    protected boolean haveBeenAddedToStatement;

    /**
     * TableAliasLookup constructor comment.
     */
    public TableAliasLookup() {
        super();
        keys = new DatabaseTable[5];
        values = new DatabaseTable[5];
        lastUsed = 0;
    }

    /**
     * TableAliasLookup constructor comment.
     */
    public TableAliasLookup(int initialSize) {
        super();
        keys = new DatabaseTable[initialSize];
        values = new DatabaseTable[initialSize];
        lastUsed = 0;
    }

    // Add all of our values to the map
    public void addToMap(Map<DatabaseTable, DatabaseTable> map) {
        for (int i = 0; i < lastUsed; i++) {
            map.put(keys[i], values[i]);
        }
    }

    public DatabaseTable get(DatabaseTable key) {
        int index = lookupIndexOf(key);
        if (index == -1) {
            return null;
        }
        return values[index];
    }

    private void grow() {
        DatabaseTable[] newKeys = new DatabaseTable[(lastUsed * 2)];
        DatabaseTable[] newValues = new DatabaseTable[(lastUsed * 2)];

        for (int i = 0; i < lastUsed; i++) {
            newKeys[i] = keys[i];
            newValues[i] = values[i];
        }
        keys = newKeys;
        values = newValues;
    }

    /**
     * INTERNAL:
     * Answers if the aliases have already been added to a statement.
     * This insures that a subselect will not re-add aliases already
     * in a parent FROM clause.
     * For CR#4223
     */
    public boolean haveBeenAddedToStatement() {
        return haveBeenAddedToStatement;
    }

    /**
     * isEmpty method comment.
     */
    public boolean isEmpty() {
        return keys[0] == null;
    }

    public DatabaseTable keyAtValue(DatabaseTable value) {
        int index = lookupValueIndexOf(value);
        if (index == -1) {
            return null;
        }
        return keys[index];
    }

    public DatabaseTable[] keys() {
        return keys;
    }

    private int lookupIndexOf(DatabaseTable table) {
        for (int i = 0; i < lastUsed; i++) {
            if (keys[i].equals(table)) {
                return i;
            }
        }
        return -1;
    }

    private int lookupValueIndexOf(DatabaseTable table) {
        for (int i = 0; i < lastUsed; i++) {
            if (values[i].equals(table)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * put method comment.
     */
    public DatabaseTable put(DatabaseTable key, DatabaseTable value) {
        int index = lookupIndexOf(key);
        if (index == -1) {
            keys[lastUsed] = key;
            values[lastUsed++] = value;
            if (lastUsed >= keys.length) {
                grow();
            }
        } else {
            values[index] = value;
        }
        return value;
    }

    /**
     * INTERNAL:
     * Called when aliases are added to a statement.
     * This insures that a subselect will not re-add aliases already
     * in a parent FROM clause.
     * For CR#4223
     */
    public void setHaveBeenAddedToStatement(boolean value) {
        haveBeenAddedToStatement = value;
    }

    /**
     * size method comment.
     */
    public int size() {
        return lastUsed;
    }

    public String toString() {
        int max = size() - 1;
        StringBuffer buf = new StringBuffer();
        buf.append("{");

        for (int i = 0; i <= max; i++) {
            String s1 = keys[i].toString();
            String s2 = values[i].toString();
            buf.append(s1 + "=" + s2);
            if (i < max) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    public DatabaseTable[] values() {
        return values;
    }
}
