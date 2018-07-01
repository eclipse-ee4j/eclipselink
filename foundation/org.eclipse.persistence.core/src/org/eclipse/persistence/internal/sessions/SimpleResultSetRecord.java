/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2013, 2018 IBM Corporation. All rights reserved.
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
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
package org.eclipse.persistence.internal.sessions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * PERF: Record used by ObjectLevelReadQuery ResultSet optimization.
 * This is a subclass of ResultSetRecord that's used with simple descriptors
 * (descriptor.getObjectBuilder().isSimple()) - those having only DirectToField mappings.
 * In this case the record is not cached by any mapping therefore could be reused
 * in ReadAllQuery - (which calls resultSet.next()).
 * In case the cached object used instead of creating a new one from the record,
 * the not needed fields' values are never obtained from resultSet
 * (that's especially important for expensive LOBs).
 * If alternatively the record is used to populate an object then all
 * the values obtained from resultSet are nullified after use.
 */
public class SimpleResultSetRecord extends ResultSetRecord {

    /** Indicates whether the values requested while populating object (isPopulatingObject == true) should be saved in valuesArray */
    transient protected boolean shouldKeepValues;
    /** Indicates whether to use optimization while getting values from result set instead of calling accessor.getObject method. */
    transient protected boolean shouldUseOptimization;
    /**
     * Indicates that the  whole object is being populated.
     * At first the primary key is extracted from the row - to see
     * if the object with the same pk is already in the cache.
     * Then in case of refresh the locking field value is extracted, too.
     * Indication of the object being populated from the record (looping through all the mappings)
     * is request for a value, which has been already returned before (usually primary key, but may be version).
     * At this point the flag is set to true.
     */
    transient protected boolean isPopulatingObject;

    protected SimpleResultSetRecord() {
        super();
    }

    public SimpleResultSetRecord(Vector fields, DatabaseField[] fieldsArray, ResultSet resultSet, ResultSetMetaData metaData, DatabaseAccessor accessor, AbstractSession session, DatabasePlatform platform, boolean optimizeData) {
        super(fields, fieldsArray, resultSet, metaData, accessor, session, platform, optimizeData);
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
                    this.valuesArray[index] = getValueFromResultSet(index, field);
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
                if (value != null) {
                    if (!this.isPopulatingObject) {
                        this.isPopulatingObject = true;
                    }
                    if (!this.shouldKeepValues) {
                        this.valuesArray[index] = null;
                    }
                } else {
                    if (this.shouldUseOptimization) {
                        try {
                            Class fieldType = field.getType();
                            if (fieldType == ClassConstants.STRING) {
                                if(platform.shouldUseGetSetNString()){
                                    value = resultSet.getNString(index + 1);
                                }else {
                                    value = resultSet.getString(index + 1);
                                }

                            } else if (fieldType == ClassConstants.LONG) {
                                value = resultSet.getLong(index + 1);
                            } else if (fieldType == ClassConstants.INTEGER) {
                                value = resultSet.getInt(index + 1);
                            } else {
                                value = this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
                            }
                        } catch (SQLException exception) {
                            DatabaseException commException = this.accessor.processExceptionForCommError(session, exception, null);
                            if (commException != null) {
                                throw commException;
                            }
                            throw DatabaseException.sqlException(exception, accessor, session, false);
                        }            } else {
                        value = this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
                    }
                    if (!this.isPopulatingObject || this.shouldKeepValues) {
                        this.valuesArray[index] = value;
                    }
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
                if (value != null) {
                    if (!this.isPopulatingObject) {
                        this.isPopulatingObject = true;
                    }
                    if (!this.shouldKeepValues) {
                        this.valuesArray[index] = null;
                    }
                } else {
                    if (this.shouldUseOptimization) {
                        try {
                            Class fieldType = field.getType();
                            if (fieldType == ClassConstants.STRING) {
                                if(platform.shouldUseGetSetNString()){
                                    value = resultSet.getNString(index + 1);
                                }else {
                                    value = resultSet.getString(index + 1);
                                }
                            } else if (fieldType == ClassConstants.LONG) {
                                value = resultSet.getLong(index + 1);
                            } else if (fieldType == ClassConstants.INTEGER) {
                                value = resultSet.getInt(index + 1);
                            } else {
                                value = this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
                            }
                        } catch (SQLException exception) {
                            DatabaseException commException = this.accessor.processExceptionForCommError(session, exception, null);
                            if (commException != null) {
                                throw commException;
                            }
                            throw DatabaseException.sqlException(exception, accessor, session, false);
                        }            } else {
                        value = this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
                    }
                    if (!this.isPopulatingObject || this.shouldKeepValues) {
                        this.valuesArray[index] = value;
                    }
                }
                return value;
            } else {
                return this.valuesArray[index];
            }
        } else {
            return super.get(key);
        }
    }

    protected Object getValueFromResultSet(int index, DatabaseField field) {
        if (this.shouldUseOptimization) {
            try {
                Class fieldType = field.getType();
                if (fieldType == ClassConstants.STRING) {
                    if(platform.shouldUseGetSetNString()){
                        return resultSet.getNString(index + 1);
                    }else {
                        return resultSet.getString(index + 1);
                    }
                } else if (fieldType == ClassConstants.LONG) {
                    return resultSet.getLong(index + 1);
                } else if (fieldType == ClassConstants.INTEGER) {
                    return resultSet.getInt(index + 1);
                }
            } catch (SQLException exception) {
                DatabaseException commException = this.accessor.processExceptionForCommError(session, exception, null);
                if (commException != null) {
                    throw commException;
                }
                throw DatabaseException.sqlException(exception, accessor, session, false);
            }
        }
        return this.accessor.getObject(this.resultSet, field, this.metaData, index + 1, this.platform, this.optimizeData, this.session);
    }

    public void reset() {
        if (this.isPopulatingObject) {
            this.isPopulatingObject = false;
            if (this.shouldKeepValues) {
                int size = this.valuesArray.length;
                for (int index = 0; index < size; index++) {
                    this.valuesArray[index] = null;
                }
            }
        } else {
            // TODO: this row hasn't been used to populate object, so values for all fields are null except for primary key fields and, possibly, version field.
            int size = this.valuesArray.length;
            for (int index = 0; index < size; index++) {
                this.valuesArray[index] = null;
            }
        }
    }

    public boolean shouldKeepValues() {
        return this.shouldKeepValues;
    }

    public void setShouldKeepValues(boolean shouldKeepValues) {
        this.shouldKeepValues = shouldKeepValues;
    }

    public boolean shouldUseOptimization() {
        return this.shouldUseOptimization;
    }

    public void setShouldUseOptimization(boolean shouldUseOptimization) {
        this.shouldUseOptimization = shouldUseOptimization;
    }

    public boolean hasValues() {
        return this.valuesArray[0] != null;
    }

    public boolean isPopulatingObject() {
        return this.isPopulatingObject;
    }

    @Override
    protected String toStringAditional() {
        return (this.shouldKeepValues ? " shouldKeepValues" : "") + (shouldUseOptimization ? " shouldUseOptimization" : "") + (isPopulatingObject ? " isPopulatingObject" : "");
    }

    @Override
    public void setSopObject(Object sopObject) {
        this.sopObject = sopObject;
        // sopObject is set - the row is used to populate object
        this.isPopulatingObject = true;
    }
}
