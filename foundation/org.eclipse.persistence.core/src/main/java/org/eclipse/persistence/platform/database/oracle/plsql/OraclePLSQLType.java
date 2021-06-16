/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    String PLSQLBoolean_IN_CONV = "SYS.SQLJUTL.INT2BOOL";
    String PLSQLBoolean_OUT_CONV = "SYS.SQLJUTL.BOOL2INT";
}
