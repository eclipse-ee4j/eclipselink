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

package org.eclipse.persistence.platform.database.oracle.plsql;

// javase imports
import static java.lang.Integer.MIN_VALUE;

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
    public boolean isNonAssociative = false;  // assume collections are associative 

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
     * Set the 'isNonAssociative' collection flag.  Should be false (default) for
     * associative/indexed collections, and true for non-associative collections.
     * 
     * @param isNonAsscociative
     */
    public void setIsNonAssociativeCollection(boolean isNonAsscociative) {
    	this.isNonAssociative = isNonAsscociative;
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
