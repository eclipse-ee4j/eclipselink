/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.sessions.factories;

// javse imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLType;

/**
 * <b>INTERNAL</b>: a helper class that holds DatabaseType's. Used to support marshalling
 * PLSQLStoredProcedureCall's
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class SimplePLSQLTypeWrapper extends DatabaseTypeWrapper {

    public SimplePLSQLTypeWrapper() {
        super();
    }

    public SimplePLSQLTypeWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    @Override
    public OraclePLSQLType getWrappedType() {
        return (OraclePLSQLType)wrappedDatabaseType;
    }
}
