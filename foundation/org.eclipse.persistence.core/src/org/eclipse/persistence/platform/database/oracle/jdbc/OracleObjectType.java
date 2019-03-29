/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle.jdbc;

import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;

public class OracleObjectType extends ComplexDatabaseType implements Cloneable {

    public OracleObjectType() {
        super();
    }

    protected int lastFieldIdx;
    protected Map<String, DatabaseType> fields =  new LinkedHashMap<>();

    public int getLastFieldIndex() {
        return lastFieldIdx;
    }
    public void setLastFieldIndex(int lastFieldIdx) {
        this.lastFieldIdx = lastFieldIdx;
    }

    public Map<String, DatabaseType> getFields() {
        return fields;
    }

    public void setFields(Map<String, DatabaseType> fields) {
        this.fields = fields;
    }

    @Override
    public boolean isJDBCType() {
        return true;
    }

    @Override
    public boolean isComplexDatabaseType() {
        return true;
    }

    @Override
    public boolean isStruct() {
        return true;
    }

    @Override
    public int getSqlCode() {
        return Types.STRUCT;
    }

    /**
     * Oracle STRUCT types don't have a compatible type like PL/SQL
     * types do, so we will use the type name
     */
    @Override
    public String getCompatibleType() {
        return typeName;
    }

    /**
     * Oracle STRUCT types don't have a compatible type like PL/SQL
     * types do, so we will use the type name
     */
    @Override
    public void setCompatibleType(String compatibleType) {
        this.typeName = compatibleType;
    }

    @Override
    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
        // no-op
    }

    @Override
    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
        // Validate.
        if ((getTypeName() == null) || getTypeName().equals("")) {
            throw QueryException.typeNameNotSet(this);
        }
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(inArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(" := :");
        sb.append(inArg.inIndex);
        sb.append(";");
        sb.append(NL);
    }

    @Override
    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
        // Validate.
        if ((getTypeName() == null) || getTypeName().equals("")) {
            throw QueryException.typeNameNotSet(this);
        }
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(outArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(";");
        sb.append(NL);
    }

    @Override
    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
        String target = databaseTypeHelper.buildTarget(outArg);
        sb.append("  :");
        sb.append(outArg.outIndex);
        sb.append(" := ");
        sb.append(target);
        sb.append(";");
        sb.append(NL);
    }
}
