/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle.jdbc;

import static java.sql.Types.ARRAY;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;

public class OracleArrayType extends ComplexDatabaseType implements Cloneable {

    /**
     * Defines the database type of the value contained in the collection type.
     * <p>i.e. the OF type.
     * <p>This could be a JDBC type, PLSQL type, or a PLSQL RECORD type.
     */
    protected DatabaseType nestedType;
    
    /**
     * Return the database type of the value contained in the collection type.
     */
    public DatabaseType getNestedType() {
        return nestedType;
    }
    /**
     * Set the database type of the value contained in the collection type.
     * <p>i.e. the OF type.
     * <p>This could be a JDBC type, PLSQL type, or a PLSQL RECORD type.
     */
    public void setNestedType(DatabaseType nestedType) {
        this.nestedType = nestedType;
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
    public boolean isArray() {
        return true;
    }

    public int getSqlCode() {
        return ARRAY;
    }
    
    /**
     * Oracle ARRAY types don't have a compatible type like PL/SQL
     * types do, so we will use the type name
     */
    @Override
    public String getCompatibleType() {
        return typeName;
    }
    
    /**
     * Oracle ARRAY types don't have a compatible type like PL/SQL
     * types do, so we will use the type name
     */
    @Override
    public void setCompatibleType(String compatibleType) {
        this.typeName = compatibleType;
    }

    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
    	// no-op
    }

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