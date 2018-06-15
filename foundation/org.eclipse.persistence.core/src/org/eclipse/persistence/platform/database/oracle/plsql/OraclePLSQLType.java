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

package org.eclipse.persistence.platform.database.oracle.plsql;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.SimpleDatabaseType;

/**
 * <b>PUBLIC</b>: Marker interface for Oracle PL/SQL types
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public interface OraclePLSQLType extends SimpleDatabaseType {

    public static final String PLSQLBoolean_IN_CONV = "SYS.SQLJUTL.INT2BOOL";
    public static final String PLSQLBoolean_OUT_CONV = "SYS.SQLJUTL.BOOL2INT";
}
