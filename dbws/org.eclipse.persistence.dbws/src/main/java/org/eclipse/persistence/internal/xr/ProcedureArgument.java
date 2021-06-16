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

package org.eclipse.persistence.internal.xr;

/**
 * <p><b>INTERNAL</b>:
 *
 * @author Merrick Schincarol - merrick.schincariol@oracle.com
 * @since EclipseLink 1.x
 */
public class ProcedureArgument {

    protected String parameterName;
    protected String name;
    protected String complexTypeName;
    protected Integer jdbcType = null;

    public String getParameterName() {
        return parameterName;
    }
    public void setParameterName(String argumentName) {
        this.parameterName = argumentName;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getComplexTypeName() {
        return complexTypeName;
    }
    public void setComplexTypeName(String complexTypeName) {
        this.complexTypeName = complexTypeName;
    }

    /**
     * Indicates if the JDBC type should be set on the call.
     */
    public boolean isJdbcTypeSet() {
        return jdbcType != null;
    }

    /**
     * Indicates the JDBC type code to be set on the call.
     *
     */
    public int getJdbcType() {
        return jdbcType;
    }

    /**
     * Set the JDBC type code to be set on the call.
     */
    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }
}
