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

package org.eclipse.persistence.platform.database.oracle.plsql;

// javase imports
import static java.lang.Integer.MIN_VALUE;

import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
// EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseType;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT_CURSOR;

/**
 * <p>
 * <b>INTERNAL:</b> 
 * Helper class - tracks argument's original position as well as re-ordered position
 * Used by PLSQLrecord and PLSQLStoredProcedureCall
 */
public class PLSQLargument implements Cloneable {

    public String name;
    public int direction = IN;
    public int originalIndex = MIN_VALUE;
    public int inIndex = MIN_VALUE;   // re-computed positional index for IN argument
    public int outIndex = MIN_VALUE;  // re-computed positional index for OUT argument
    public DatabaseType databaseType;
    public int length = 255;          //default from the EJB 3.0 spec.
    public int precision = MIN_VALUE;
    public int scale = MIN_VALUE;
    public boolean cursorOutput = false;

    public PLSQLargument() {
        super();
    }
    
    public PLSQLargument(String name, int originalIndex, int direction,
        DatabaseType databaseType) {
        this();
        this.name = name;
        this.databaseType = databaseType;
        this.originalIndex = originalIndex;
        this.direction = direction;
    }
    
    public PLSQLargument(String name, int originalIndex, int direction,
        DatabaseType databaseType, int length) {
        this(name, originalIndex, direction, databaseType);
        this.length = length;
    }
    
    public PLSQLargument(String name, int originalIndex, int direction,
        DatabaseType databaseType, int precision, int scale) {
        this(name, originalIndex, direction, databaseType);
        this.precision = precision;
        this.scale = scale;
    }
    
    @Override
    protected PLSQLargument clone() {
        try {
            return (PLSQLargument)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
           return null;
        }
    }

    public void useNamedCursorOutputAsResultSet() {
        cursorOutput = true;
    }

    /**
     * Sets flag on this argument's database type indicating that it represents a
     * non-associative collection, i.e. Nested Table (as opposed to a Varray).
     * 
     * The value should be false (default) for associative/indexed collections 
     * (Varrays), and true for non-associative collections (Nested Tables).
     * 
     * It is assumed that the database type has been determined to be a PLSQLCollection
     * prior to calling this method - if this argument's database type is not a 
     * PLSQLCollection, no operation is performed.
     * 
     * The preferred method of flagging a PLSQCollection as a Nested Table is to use the
     * setIsNestedTable(boolean) method directly on PLSQLCollection.
     * 
     * @param isNonAsscociative true indicates this argument's database type represents a Nested Table
     * @see org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection
     */
    public void setIsNonAssociativeCollection(boolean isNonAsscociative) {
        if (databaseType != null && databaseType.isComplexDatabaseType() && ((ComplexDatabaseType) databaseType).isCollection()) {
            ((PLSQLCollection) databaseType).setIsNestedTable(isNonAsscociative);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append('{');
        if (direction == IN) {
            sb.append("IN");
        }
        else if (direction == INOUT) {
            sb.append("IN");
        }
        else if (direction == OUT) {
            sb.append("OUT");
        }
        else if (direction == OUT_CURSOR) {
            sb.append("OUT CURSOR");
        }
        sb.append(',');
        sb.append(originalIndex);
        sb.append(',');
        if (inIndex != MIN_VALUE) {
            sb.append(inIndex);
        }
        sb.append(',');
        if (outIndex != MIN_VALUE) {
            sb.append(outIndex);
        }
        sb.append('}');
        return sb.toString();
    } 
}
