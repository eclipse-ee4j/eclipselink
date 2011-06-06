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
 
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import static java.lang.Integer.MIN_VALUE;

public class PLSQLargument{

    public static final Integer IN = Integer.valueOf(1);
    public static final Integer OUT = Integer.valueOf(2);
    public static final Integer INOUT = Integer.valueOf(3);
    
    public String name;
    public int index = MIN_VALUE;
    public int direction = IN;
    public int length = 255;          //default from the EJB 3.0 spec.
    public int precision = MIN_VALUE;
    public int scale = MIN_VALUE;
    public boolean cursorOutput = false;
    public DatabaseTypeWrapper databaseTypeWrapper;

    public PLSQLargument() {
    }

    public PLSQLargument(String name, DatabaseType databaseType) {
        this(name, -1, IN, databaseType);
    }
    
    public PLSQLargument(String name, int index, int direction, DatabaseType databaseType) {
        this.name = name;
        this.index = index;
        this.direction = direction;
        if (!databaseType.isComplexDatabaseType() && databaseType.isJDBCType()) {
            databaseTypeWrapper = new JDBCTypeWrapper(databaseType);
        }
        else if (!databaseType.isComplexDatabaseType() && !databaseType.isJDBCType()) {
            databaseTypeWrapper = new SimplePLSQLTypeWrapper(databaseType);
        }
        else if (databaseType.isComplexDatabaseType()) {
            databaseTypeWrapper = new ComplexPLSQLTypeWrapper(databaseType);
        }
    }
    
    public PLSQLargument(String name, int index, int direction, DatabaseType databaseType,
        int length) {
        this(name, index, direction, databaseType);
        this.length = length;
    }
    
    public PLSQLargument(String name, int index, int direction, DatabaseType databaseType,
        int precision, int scale) {
        this(name, index, direction, databaseType);
        this.precision = precision;
        this.scale = scale;
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
            sb.append("INOUT");
        }
        else if (direction == OUT) {
            sb.append("OUT");
        }
        sb.append(',');
        sb.append(index);
        sb.append('}');
        return sb.toString();
    } 

    public boolean equals(Object obj) {
        PLSQLargument pArg = null;
        try {
            pArg = (PLSQLargument) obj;
        } catch (ClassCastException ccex) {
            return false;
        }
        if (!pArg.name.equals(this.name)) {
            return false;
        }
        if (pArg.direction != this.direction) {
            return false;
        }
        if (pArg.index != this.index) {
            return false;
        }
        if (!pArg.databaseTypeWrapper.equals(this.databaseTypeWrapper)) {
            return false;
        }
        return true;
    }
}
