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
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;

/**
 * <b>PUBLIC</b>: describe an Oracle PL/SQL Record type
 * 
 * <b>This version is 'stubbed' out until the solution to the 'order-of-out-args' issues is checked in
 * 
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
public class PLSQLrecord implements ComplexDatabaseType, OraclePLSQLType, Cloneable {

    protected String typeName;
    boolean hasCompatibleType = false;
    
    public boolean isComplexDatabaseType() {
        return true;
    }

    public int getSqlCode() {
        if (hasCompatibleType) {
            return STRUCT;
        }
        else {
            return OTHER;
        }
    }

    public int getConversionCode() {
        return getSqlCode();
    }

    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}