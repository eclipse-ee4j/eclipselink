/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;

/**
 * PERF: Record used by ObjectLevelReadQuery ResultSet optimization.
 * The record corresponds to a single position (resultSet.next() is never called).
 * In case the cached object used instead of creating a new one from the record,
 * the not needed fields' values are never obtained from resultSet
 * (that's especially important for expensive LOBs).
 * If alternatively the record is used to populate an object then all
 * the values obtained from resultSet and resultSet is nullified.
 * In this case the fields' values not required after object population
 * (those not involved in indirection) are nullified to save space in the record.
 */
public class ResultSetRecord extends ArrayRecord {
    transient protected ResultSet resultSet;
    transient protected ResultSetMetaData metaData;
    transient protected DatabaseAccessor accessor;
    transient protected DatabasePlatform platform;
    transient protected boolean optimizeData;
    transient protected AbstractSession session;

    protected ResultSetRecord() {
        super();
    }

    public ResultSetRecord(Vector fields, DatabaseField[] fieldsArray, ResultSet resultSet, ResultSetMetaData metaData, DatabaseAccessor accessor, AbstractSession session, DatabasePlatform platform, boolean optimizeData) {
        super(fields, fieldsArray, new Object[fieldsArray.length]);
        this.resultSet = resultSet;
        this.metaData = metaData;
        this.accessor = accessor;
        this.platform = platform;
        this.optimizeData = optimizeData;
        this.session = session;
    }

    /**
     * Obtains all the value from resultSet and removes it.
     * resultSet must be non null.
     */
    public void loadAllValuesFromResultSet() {
        int size = this.valuesArray.length;
        for (int index = 0; index < size; index++) {
            if (this.valuesArray[index] == null) {
                DatabaseField field = this.fieldsArray[index];
                // Field can be null for fetch groups.
                if (field != null) {
                    this.valuesArray[index] = this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
                }
            }
        }
        this.resultSet = null;
        this.metaData = null;
        this.accessor = null;
        this.platform = null;
        this.session = null;
    }

    /**
     * Remove values corresponding to all fields not related to indirection.
     */
    public void removeNonIndirectionValues() {
        if (this.fieldsArray != null) {
            int size = this.valuesArray.length;
            for (int index = 0; index < size; index++) {
                DatabaseField field = this.fieldsArray[index];
                // Field can be null for fetch groups.
                if (field != null) {
                    if (!field.keepInRow) {
                        this.valuesArray[index] = null;
                    }
                }
            }
        }
    }

    public void removAllValue() {
        if (this.valuesArray != null) {
            int size = this.valuesArray.length;
            for (int index = 0; index < size; index++) {
                this.valuesArray[index] = null;
            }
        }
    }

    /**
     * Indicates whether resultSet is still here.
     * @return
     */
    public boolean hasResultSet() {
        return this.resultSet != null;
    }

    public void removeResultSet() {
        this.resultSet = null;
        this.metaData = null;
        this.accessor = null;
        this.platform = null;
        this.session = null;
    }

    /**
     * PUBLIC:
     * Clear the contents of the row.
     */
    @Override
    public void clear() {
        removeResultSet();
        this.fieldsArray = null;
        this.valuesArray = null;
        super.clear();
    }

    /**
     * Reset the fields and values from the arrays.
     * This removes the optimization if a non-optimized method is called.
     */
    @Override
    protected void checkValues() {
        if (this.resultSet != null) {
            loadAllValuesFromResultSet();
        }
        super.checkValues();
    }

    /**
     * PUBLIC:
     * Check if the value is contained in the row.
     */
    @Override
    public boolean containsValue(Object value) {
        if (this.resultSet != null) {
            loadAllValuesFromResultSet();
        }
        return super.containsValue(value);
    }

    /**
     * INTERNAL:
     * Retrieve the value for the field. If missing null is returned.
     */
    @Override
    public Object get(DatabaseField key) {
        if (this.fieldsArray != null) {
            // Optimize check.
            int index = key.index;
            if ((index < 0) || (index >= this.size)) {
                index = 0;
            }
            DatabaseField field = this.fieldsArray[index];
            if ((field != key) && !field.equals(key)) {
                index = -1;
                for (int fieldIndex = 0; fieldIndex < this.size; fieldIndex++) {
                    field = this.fieldsArray[fieldIndex];
                    if ((field == key) || field.equals(key)) {
                        // PERF: If the fields index was not set, then set it.
                        if (key.index == -1) {
                            key.setIndex(fieldIndex);
                        }
                        index = fieldIndex;
                        break;
                    }
                }
                if (index < 0) {
                    return null;
                }
            }
            if (this.resultSet != null) {
                Object value = this.valuesArray[index];
                if (value == null) {
                    value = this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
                    this.valuesArray[index] = value;
                } else {
                    // field's value has been already extracted earlier - the row is used to populate object
                    loadAllValuesFromResultSet();
                }
                return value;
            } else {
                return this.valuesArray[index];
            }
        } else {
            return super.get(key);
        }
    }

    /**
     * INTERNAL:
     * Retrieve the value for the field. If missing DatabaseRow.noEntry is returned.
     * PERF: This method is a clone of get() for performance.
     */
    @Override
    public Object getIndicatingNoEntry(DatabaseField key) {
        if (this.fieldsArray != null) {
            // Optimize check.
            int index = key.index;
            if ((index < 0) || (index >= this.size)) {
                index = 0;
            }
            DatabaseField field = this.fieldsArray[index];
            if ((field != key) && !field.equals(key)) {
                index = -1;
                for (int fieldIndex = 0; fieldIndex < this.size; fieldIndex++) {
                    field = this.fieldsArray[fieldIndex];
                    if ((field == key) || field.equals(key)) {
                        // PERF: If the fields index was not set, then set it.
                        if (key.index == -1) {
                            key.setIndex(fieldIndex);
                        }
                        index = fieldIndex;
                        break;
                    }
                }
                if (index < 0) {
                    return null;
                }
            }
            if (this.resultSet != null) {
                Object value = this.valuesArray[index];
                if (value == null) {
                    value = this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
                    this.valuesArray[index] = value;
                } else {
                    // field's value has been already extracted earlier - the row is used to populate object
                    loadAllValuesFromResultSet();
                }
                return value;
            } else {
                return this.valuesArray[index];
            }
        } else {
            return super.get(key);
        }
    }

    @Override
    protected String toStringAditional() {
        return (this.resultSet != null ? " hasResultSet" : "");
    }

    @Override
    public void setSopObject(Object sopObject) {
        super.setSopObject(sopObject);
        // sopObject is set - the row is used to populate object
        if (this.resultSet != null) {
            loadAllValuesFromResultSet();
        }
    }
}
