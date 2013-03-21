/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - May 15, 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.plsql;

import java.util.ListIterator;
import static java.sql.Types.OTHER;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;

/**
 * <b>PUBLIC</b>: describe an Oracle PL/SQL Cursor type
 */
@SuppressWarnings("unchecked")
public class PLSQLCursor extends ComplexDatabaseType implements OraclePLSQLType, Cloneable {

    public PLSQLCursor() {
        super();
        typeName = "CURSOR";
    }

    public PLSQLCursor(String cursorName) {
        super();
        typeName = cursorName;
    }

    @Override
    public PLSQLCursor clone()  {
        return (PLSQLCursor)super.clone();
    }

    @Override
    public boolean isCursor() {
        return true;
    }

    public int getSqlCode() {
        return OTHER;
    }

    @Override
    public int computeInIndex(PLSQLargument inArg, int newIndex, ListIterator<PLSQLargument> i) {
        inArg.inIndex = newIndex;
        return newIndex;
    }

    @Override
    public int computeOutIndex(PLSQLargument outArg, int newIndex, ListIterator<PLSQLargument> iterator) {
        outArg.outIndex = newIndex;
        return newIndex;
    }

    @Override
    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
        // nothing to do for CURSOR
    }
    
    @Override
    public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
        if ((getTypeName() == null) || getTypeName().equals("")) {
            throw QueryException.typeNameNotSet(this);        
        }
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(outArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(";\n");
    }

    @Override
    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(arg));
    }

    @Override
    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
        String target = databaseTypeHelper.buildTarget(outArg);
        sb.append("  ");
        sb.append(":");
        sb.append(outArg.outIndex);
        sb.append(" := ");
        sb.append(target);
        sb.append(";\n");
    }
}