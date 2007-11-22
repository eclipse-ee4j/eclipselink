/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.platform.database.oracle;

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
 * helper class - tracks argument's original position as well as re-ordered position
 * Used by PLSQLrecord and PLSQLStoredProcedureCall
 */
public class PLSQLargument {

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
    
    public PLSQLargument(String name, int originalIndex, int direction,
        DatabaseType databaseType) {
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