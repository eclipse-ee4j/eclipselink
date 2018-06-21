/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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
