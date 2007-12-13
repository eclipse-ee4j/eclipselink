// Copyright (c) 2007 Oracle. All rights reserved.

package org.eclipse.persistence.platform.database.jdbc;

// javse imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseTypeWrapper;

/**
 * <b>INTERNAL</b>: a helper class that holds DatabaseType's. Used to support marshalling
 * PLSQLStoredProcedureCall's 
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class JDBCTypeWrapper extends DatabaseTypeWrapper {

    public JDBCTypeWrapper() {
        super();
    }

    public JDBCTypeWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    public JDBCType getWrappedType() {
        return (JDBCType)wrappedDatabaseType;
    }
}
