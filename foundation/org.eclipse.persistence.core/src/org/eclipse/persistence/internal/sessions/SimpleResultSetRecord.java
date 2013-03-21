/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
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
    
    public SimpleResultSetRecord(Vector fields, DatabaseField[] fieldsArray, ResultSet resultSet, ResultSetMetaData metaData, DatabaseAccessor accessor, AbstractSession session) {
        super(fields, fieldsArray, resultSet, metaData, accessor, session);
    }    
    
    /**
     * Obtains the value corresponding to the passed index from resultSet.
     * Should not be called if resultSet is not null.
     */
    @Override
    protected Object getValue(int index, DatabaseField field) {
        Object value = this.valuesArray[index];
        if (value != null) {
            if (!this.isPopulatingObject) {
                this.isPopulatingObject = true;
            }
            if (!this.shouldKeepValues) {
                this.valuesArray[index] = null;
            }
        } else {
            value = getValueFromResultSet(index, field);
            if (!this.isPopulatingObject || this.shouldKeepValues) {
                this.valuesArray[index] = value;
            }
        }
        return value;
    }
    
    protected Object getValueFromResultSet(int index, DatabaseField field) {
        if (this.shouldUseOptimization) {
            try {
                Class fieldType = field.getType(); 
                if (fieldType == ClassConstants.STRING) {
                    return resultSet.getString(index + 1);
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
        return super.getValueFromResultSet(index, field);
    }
    
    public void reset() {
        if (this.isPopulatingObject) {
            this.isPopulatingObject = false;
            if (this.shouldKeepValues) {
                for (int index = 0; index < this.valuesArray.length; index++) {
                    this.valuesArray[index] = null;
                }
            }
        } else {
            // TODO: this row hasn't been used to populate object, so values for all fields are null except for primary key fields and, possibly, version field.
            for (int index = 0; index < this.valuesArray.length; index++) {
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
}
